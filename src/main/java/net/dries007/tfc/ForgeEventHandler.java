/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.event.server.ServerAboutToStartEvent;

import net.dries007.tfc.common.TFCEffects;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.*;
import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.DeadWallTorchBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.TFCWallTorchBlock;
import net.dries007.tfc.common.blocks.devices.BurningLogPileBlock;
import net.dries007.tfc.common.blocks.devices.CharcoalForgeBlock;
import net.dries007.tfc.common.blocks.devices.PitKilnBlock;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodDefinition;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.dries007.tfc.common.capabilities.forge.ForgingCapability;
import net.dries007.tfc.common.capabilities.forge.ForgingHandler;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.HeatDefinition;
import net.dries007.tfc.common.capabilities.player.PlayerData;
import net.dries007.tfc.common.capabilities.player.PlayerDataCapability;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.commands.TFCCommands;
import net.dries007.tfc.common.entities.Fauna;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.accessor.ChunkAccessAccessor;
import net.dries007.tfc.mixin.accessor.SimpleReloadableResourceManagerAccessor;
import net.dries007.tfc.network.ChunkUnwatchPacket;
import net.dries007.tfc.network.ClimateSettingsUpdatePacket;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.PlayerDrinkPacket;
import net.dries007.tfc.util.*;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.events.StartFireEvent;
import net.dries007.tfc.util.tracker.WorldTracker;
import net.dries007.tfc.util.tracker.WorldTrackerCapability;
import net.dries007.tfc.world.NoopClimateSampler;
import net.dries007.tfc.world.biome.BiomeSourceExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCache;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;
import net.dries007.tfc.world.chunkdata.ChunkGeneratorExtension;
import net.dries007.tfc.world.settings.ClimateSettings;
import net.dries007.tfc.world.settings.RockLayerSettings;

public final class ForgeEventHandler
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final BlockHitResult FAKE_MISS = BlockHitResult.miss(Vec3.ZERO, Direction.UP, BlockPos.ZERO);

    public static void init()
    {
        final IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addListener(ForgeEventHandler::onCreateWorldSpawn);
        bus.addGenericListener(LevelChunk.class, ForgeEventHandler::attachChunkCapabilities);
        bus.addGenericListener(Level.class, ForgeEventHandler::attachWorldCapabilities);
        bus.addGenericListener(ItemStack.class, ForgeEventHandler::attachItemCapabilities);
        bus.addGenericListener(Entity.class, ForgeEventHandler::attachEntityCapabilities);
        bus.addListener(ForgeEventHandler::onChunkWatch);
        bus.addListener(ForgeEventHandler::onChunkUnwatch);
        bus.addListener(ForgeEventHandler::onChunkLoad);
        bus.addListener(ForgeEventHandler::onChunkUnload);
        bus.addListener(ForgeEventHandler::onChunkDataSave);
        bus.addListener(ForgeEventHandler::onChunkDataLoad);
        bus.addListener(ForgeEventHandler::addReloadListeners);
        bus.addListener(ForgeEventHandler::beforeServerStart);
        bus.addListener(ForgeEventHandler::registerCommands);
        bus.addListener(ForgeEventHandler::onBlockBroken);
        bus.addListener(ForgeEventHandler::onBlockPlace);
        bus.addListener(ForgeEventHandler::onNeighborUpdate);
        bus.addListener(ForgeEventHandler::onWorldTick);
        bus.addListener(ForgeEventHandler::onExplosionDetonate);
        bus.addListener(ForgeEventHandler::onWorldLoad);
        bus.addListener(ForgeEventHandler::onCreateNetherPortal);
        bus.addListener(ForgeEventHandler::onFluidPlaceBlock);
        bus.addListener(ForgeEventHandler::onFireStart);
        bus.addListener(ForgeEventHandler::onProjectileImpact);
        bus.addListener(ForgeEventHandler::onPlayerTick);
        bus.addListener(ForgeEventHandler::onEffectRemove);
        bus.addListener(ForgeEventHandler::onEffectExpire);
        bus.addListener(ForgeEventHandler::onItemExpire);
        bus.addListener(ForgeEventHandler::onEntityJoinWorld);
        bus.addListener(ForgeEventHandler::onPlayerLoggedIn);
        bus.addListener(ForgeEventHandler::onPlayerRespawn);
        bus.addListener(ForgeEventHandler::onPlayerChangeDimension);
        bus.addListener(ForgeEventHandler::onServerChat);
        bus.addListener(ForgeEventHandler::onPlayerRightClickBlock);
        bus.addListener(ForgeEventHandler::onPlayerRightClickItem);
        bus.addListener(ForgeEventHandler::onPlayerRightClickEmpty);
        bus.addListener(ForgeEventHandler::onDataPackSync);
        bus.addListener(ForgeEventHandler::onBoneMeal);
    }

    /**
     * Duplicates logic from {@link MinecraftServer#setInitialSpawn(ServerLevel, ServerLevelData, boolean, boolean)} as that version only asks the dimension for the sea level...
     */
    public static void onCreateWorldSpawn(WorldEvent.CreateSpawnPosition event)
    {
        if (event.getWorld() instanceof ServerLevel world && world.getChunkSource().getGenerator() instanceof ChunkGeneratorExtension extension)
        {
            final ChunkGenerator generator = extension.self();
            final ServerLevelData settings = event.getSettings();
            final BiomeSourceExtension source = extension.getBiomeSource();
            final Random random = new Random(world.getSeed());

            BlockPos pos = generator.getBiomeSource().findBiomeHorizontal(source.getSpawnCenterX(), 0, source.getSpawnCenterZ(), source.getSpawnDistance(), source.getSpawnDistance() / 256, biome -> TFCBiomes.getExtensionOrThrow(world, biome).variants().isSpawnable(), random, false, NoopClimateSampler.INSTANCE);
            ChunkPos chunkPos;
            if (pos == null)
            {
                LOGGER.warn("Unable to find spawn biome!");
                pos = new BlockPos(0, generator.getSeaLevel(), 0);
            }
            chunkPos = new ChunkPos(pos);

            settings.setSpawn(chunkPos.getWorldPosition().offset(8, generator.getSpawnHeight(world), 8), 0.0F);
            boolean foundExactSpawn = false;
            int x = 0, z = 0;
            int xStep = 0;
            int zStep = -1;

            for (int tries = 0; tries < 1024; ++tries)
            {
                if (x > -16 && x <= 16 && z > -16 && z <= 16)
                {
                    final BlockPos spawnPos = PlayerRespawnLogic.getSpawnPosInChunk(world, new ChunkPos(chunkPos.x + x, chunkPos.z + z));
                    if (spawnPos != null)
                    {
                        settings.setSpawn(spawnPos, 0);
                        foundExactSpawn = true;
                        break;
                    }
                }

                if ((x == z) || (x < 0 && x == -z) || (x > 0 && x == 1 - z))
                {
                    final int swap = xStep;
                    xStep = -zStep;
                    zStep = swap;
                }

                x += xStep;
                z += zStep;
            }

            if (!foundExactSpawn)
            {
                LOGGER.warn("Unable to find a suitable spawn location!");
            }

            if (world.getServer().getWorldData().worldGenSettings().generateBonusChest())
            {
                LOGGER.warn("No bonus chest for you, you cheaty cheater!");
            }

            event.setCanceled(true);
        }
    }

    public static void attachChunkCapabilities(AttachCapabilitiesEvent<LevelChunk> event)
    {
        final LevelChunk chunk = event.getObject();
        if (!chunk.isEmpty())
        {
            final Level level = event.getObject().getLevel();
            final ChunkPos chunkPos = event.getObject().getPos();

            ChunkData data;
            if (Helpers.isClientSide(level))
            {
                // This may happen before or after the chunk is watched and synced to client
                // Default to using the cache. If later the sync packet arrives it will update the same instance in the chunk capability and cache
                // We don't want to use getOrEmpty here, as the instance has to be mutable. In addition, we can't just wait for the chunk data to arrive, we have to assign one.
                data = ChunkDataCache.CLIENT.computeIfAbsent(chunkPos, ChunkData::createClient);
            }
            else
            {
                // Chunk was created on server thread.
                // We try and promote partial data, if it's available via an identifiable chunk generator.
                // Otherwise, we fallback to empty data.
                if (level instanceof ServerLevel serverLevel && serverLevel.getChunkSource().getGenerator() instanceof ChunkGeneratorExtension ex)
                {
                    data = ex.getChunkDataProvider().promotePartialOrCreate(chunkPos);
                }
                else
                {
                    data = new ChunkData(chunkPos, RockLayerSettings.EMPTY);
                }

            }
            event.addCapability(ChunkDataCapability.KEY, data);
        }
    }

    public static void attachWorldCapabilities(AttachCapabilitiesEvent<Level> event)
    {
        event.addCapability(WorldTrackerCapability.KEY, new WorldTracker());
    }

    public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event)
    {
        ItemStack stack = event.getObject();
        if (!stack.isEmpty())
        {
            // Attach mandatory capabilities
            event.addCapability(ForgingCapability.KEY, new ForgingHandler(stack));

            // Optional capabilities
            HeatDefinition def = HeatCapability.get(stack);
            if (def != null)
            {
                event.addCapability(HeatCapability.KEY, def.create());
            }

            FoodDefinition food = FoodCapability.get(stack);
            if (food != null)
            {
                event.addCapability(FoodCapability.KEY, new FoodHandler(food.getData()));
            }
        }
    }

    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof Player player)
        {
            event.addCapability(PlayerDataCapability.KEY, new PlayerData(player));
        }
    }

    public static void onChunkWatch(ChunkWatchEvent.Watch event)
    {
        // Send an update packet to the client when watching the chunk
        ChunkPos pos = event.getPos();
        ChunkData chunkData = ChunkData.get(event.getWorld(), pos);
        if (chunkData.getStatus() != ChunkData.Status.EMPTY)
        {
            PacketHandler.send(PacketDistributor.PLAYER.with(event::getPlayer), chunkData.getUpdatePacket());
        }
        else
        {
            // Chunk does not exist yet but it's queue'd for watch. Queue an update packet to be sent on chunk load
            ChunkDataCache.WATCH_QUEUE.enqueueUnloadedChunk(pos, event.getPlayer());
        }
    }

    public static void onChunkUnwatch(ChunkWatchEvent.UnWatch event)
    {
        // Send an update packet to the client when un-watching the chunk
        ChunkPos pos = event.getPos();
        PacketHandler.send(PacketDistributor.PLAYER.with(event::getPlayer), new ChunkUnwatchPacket(pos));
        ChunkDataCache.WATCH_QUEUE.dequeueChunk(pos, event.getPlayer());
    }

    public static void onChunkLoad(ChunkEvent.Load event)
    {
        if (!Helpers.isClientSide(event.getWorld()) && !(event.getChunk() instanceof EmptyLevelChunk))
        {
            ChunkPos pos = event.getChunk().getPos();
            ChunkData.getCapability(event.getChunk()).ifPresent(data -> {
                ChunkDataCache.SERVER.update(pos, data);
                ChunkDataCache.WATCH_QUEUE.dequeueLoadedChunk(pos, data);
            });
        }
    }

    public static void onChunkUnload(ChunkEvent.Unload event)
    {
        // Clear server side chunk data cache
        if (!Helpers.isClientSide(event.getWorld()) && !(event.getChunk() instanceof EmptyLevelChunk))
        {
            ChunkDataCache.SERVER.remove(event.getChunk().getPos());
        }
    }

    /**
     * Serialize chunk data on chunk primers, before the chunk data capability is present.
     * - This saves the effort of re-generating the same data for proto chunks
     * - And, due to the late setting of part of chunk data ({@link net.dries007.tfc.world.chunkdata.RockData#setSurfaceHeight(int[])}, avoids that being nullified when saving and reloading during the noise phase of generation
     */
    public static void onChunkDataSave(ChunkDataEvent.Save event)
    {
        if (event.getChunk().getStatus().getChunkType() == ChunkStatus.ChunkType.PROTOCHUNK && event.getChunk() instanceof ProtoChunk chunk && ((ServerChunkCache) event.getWorld().getChunkSource()).getGenerator() instanceof ChunkGeneratorExtension ex)
        {
            CompoundTag nbt = ex.getChunkDataProvider().savePartial(chunk);
            if (nbt != null)
            {
                event.getData().put("tfc_protochunk_data", nbt);
            }
        }
    }

    /**
     * @see #onChunkDataSave(ChunkDataEvent.Save)
     */
    public static void onChunkDataLoad(ChunkDataEvent.Load event)
    {
        if (event.getChunk().getStatus().getChunkType() == ChunkStatus.ChunkType.PROTOCHUNK && event.getData().contains("tfc_protochunk_data", Tag.TAG_COMPOUND) && event.getChunk() instanceof ProtoChunk chunk && ((ChunkAccessAccessor) chunk).accessor$getLevelHeightAccessor() instanceof ServerLevel level && level.getChunkSource().getGenerator() instanceof ChunkGeneratorExtension generator)
        {
            generator.getChunkDataProvider().loadPartial(chunk, event.getData().getCompound("tfc_protochunk_data"));
        }
    }

    public static void addReloadListeners(AddReloadListenerEvent event)
    {
        // Alloy recipes are loaded as part of recipes, but have a hard dependency on metals.
        // So, we hack internal resource lists in order to stick metals before recipes.
        final ResourceManager resourceManager = event.getDataPackRegistries().getResourceManager();
        if (resourceManager instanceof SimpleReloadableResourceManager resources)
        {
            final List<PreparableReloadListener> listeners = ((SimpleReloadableResourceManagerAccessor) resources).accessor$getListeners();
            final RecipeManager recipes = event.getDataPackRegistries().getRecipeManager();
            Helpers.insertBefore(listeners, Metal.MANAGER, recipes);
        }

        // All other resource reload listeners can be inserted after recipes.
        event.addListener(Fuel.MANAGER);
        event.addListener(Drinkable.MANAGER);
        event.addListener(Support.MANAGER);
        event.addListener(Fertilizer.MANAGER);
        event.addListener(ItemSizeManager.MANAGER);
        event.addListener(ClimateRange.MANAGER);
        event.addListener(Fauna.MANAGER);

        event.addListener(HeatCapability.MANAGER);
        event.addListener(FoodCapability.MANAGER);

        // Last
        event.addListener(CacheInvalidationListener.INSTANCE);
    }

    public static void beforeServerStart(ServerAboutToStartEvent event)
    {
        CacheInvalidationListener.INSTANCE.invalidateServerCaches(event.getServer());
    }

    public static void registerCommands(RegisterCommandsEvent event)
    {
        LOGGER.debug("Registering TFC Commands");
        TFCCommands.registerCommands(event.getDispatcher());
    }

    public static void onBlockBroken(BlockEvent.BreakEvent event)
    {
        // Trigger a collapse
        final LevelAccessor world = event.getWorld();
        final BlockPos pos = event.getPos();
        final BlockState state = world.getBlockState(pos);

        if (TFCTags.Blocks.CAN_TRIGGER_COLLAPSE.contains(state.getBlock()) && world instanceof Level level)
        {
            CollapseRecipe.tryTriggerCollapse(level, pos);
            return;
        }

        // Chop down a tree
        final ItemStack stack = event.getPlayer().getMainHandItem();
        if (AxeLoggingHelper.isLoggingAxe(stack) && AxeLoggingHelper.isLoggingBlock(state))
        {
            event.setCanceled(true); // Cancel regardless of outcome of logging
            AxeLoggingHelper.doLogging(world, pos, event.getPlayer(), stack);
        }
    }

    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event)
    {
        if (event.getWorld() instanceof final ServerLevel world)
        {
            final BlockPos pos = event.getPos();
            final BlockState state = event.getState();

            if (TFCTags.Blocks.CAN_LANDSLIDE.contains(state.getBlock()))
            {
                world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addLandslidePos(pos));
            }

            if (TFCTags.Blocks.BREAKS_WHEN_ISOLATED.contains(state.getBlock()))
            {
                world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addIsolatedPos(pos));
            }
        }
    }

    public static void onNeighborUpdate(BlockEvent.NeighborNotifyEvent event)
    {
        if (event.getWorld() instanceof final ServerLevel level)
        {
            for (Direction direction : event.getNotifiedSides())
            {
                // Check each notified block for a potential gravity block
                final BlockPos pos = event.getPos().relative(direction);
                final BlockState state = level.getBlockState(pos);

                if (TFCTags.Blocks.CAN_LANDSLIDE.contains(state.getBlock()))
                {
                    level.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addLandslidePos(pos));
                }

                if (TFCTags.Blocks.BREAKS_WHEN_ISOLATED.contains(state.getBlock()))
                {
                    level.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addIsolatedPos(pos));
                }
            }
        }
    }

    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            event.world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.tick(event.world));
        }
    }

    public static void onExplosionDetonate(ExplosionEvent.Detonate event)
    {
        if (!event.getWorld().isClientSide)
        {
            event.getWorld().getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addCollapsePositions(new BlockPos(event.getExplosion().getPosition()), event.getAffectedBlocks()));
        }
    }

    public static void onWorldLoad(WorldEvent.Load event)
    {
        if (event.getWorld() instanceof final ServerLevel level)
        {
            final MinecraftServer server = level.getServer();

            if (TFCConfig.SERVER.enableForcedTFCGameRules.get())
            {
                final GameRules rules = level.getGameRules();

                rules.getRule(GameRules.RULE_NATURAL_REGENERATION).set(false, server);
                rules.getRule(GameRules.RULE_DOINSOMNIA).set(false, server);
                rules.getRule(GameRules.RULE_DO_PATROL_SPAWNING).set(false, server);
                rules.getRule(GameRules.RULE_DO_TRADER_SPAWNING).set(false, server);

                LOGGER.info("Updating TFC Relevant Game Rules for level {}.", level.dimension().location());
            }

            if (level.dimension() == Level.OVERWORLD && level.getChunkSource().getGenerator() instanceof ChunkGeneratorExtension ex)
            {
                // Update climate settings
                final ClimateSettings settings = ex.getBiomeSource().getTemperatureSettings();

                Climate.updateCachedSettings(level, settings, ex.getClimateSeed()); // Server
                PacketHandler.send(PacketDistributor.ALL.noArg(), new ClimateSettingsUpdatePacket(settings, ex.getClimateSeed())); // Client
            }
        }
    }

    public static void onCreateNetherPortal(BlockEvent.PortalSpawnEvent event)
    {
        if (!TFCConfig.SERVER.enableNetherPortals.get())
        {
            event.setCanceled(true);
        }
    }

    public static void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event)
    {
        Block originalBlock = event.getOriginalState().getBlock();
        if (originalBlock == Blocks.STONE)
        {
            event.setNewState(TFCBlocks.ROCK_BLOCKS.get(net.dries007.tfc.common.blocks.rock.Rock.GABBRO).get(net.dries007.tfc.common.blocks.rock.Rock.BlockType.HARDENED).get().defaultBlockState());
        }
        else if (originalBlock == Blocks.COBBLESTONE)
        {
            event.setNewState(TFCBlocks.ROCK_BLOCKS.get(net.dries007.tfc.common.blocks.rock.Rock.RHYOLITE).get(net.dries007.tfc.common.blocks.rock.Rock.BlockType.HARDENED).get().defaultBlockState());
        }
        else if (originalBlock == Blocks.BASALT)
        {
            event.setNewState(TFCBlocks.ROCK_BLOCKS.get(net.dries007.tfc.common.blocks.rock.Rock.BASALT).get(Rock.BlockType.HARDENED).get().defaultBlockState());
        }
    }

    public static void onFireStart(StartFireEvent event)
    {
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        Block block = state.getBlock();

        if (block == TFCBlocks.FIREPIT.get() || block == TFCBlocks.POT.get() || block == TFCBlocks.GRILL.get())
        {
            final BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof AbstractFirepitBlockEntity<?> firepit && firepit.light(state))
            {
                event.setCanceled(true);
            }
        }
        else if (block == TFCBlocks.TORCH.get() || block == TFCBlocks.WALL_TORCH.get())
        {
            world.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(TickCounterBlockEntity::resetCounter);
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.DEAD_TORCH.get())
        {
            world.setBlockAndUpdate(pos, TFCBlocks.TORCH.get().defaultBlockState());
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.DEAD_WALL_TORCH.get())
        {
            Direction direction = state.getValue(DeadWallTorchBlock.FACING);
            world.setBlockAndUpdate(pos, TFCBlocks.WALL_TORCH.get().defaultBlockState().setValue(TFCWallTorchBlock.FACING, direction));
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.LOG_PILE.get())
        {
            BurningLogPileBlock.tryLightLogPile(world, pos);
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.PIT_KILN.get() && state.getValue(PitKilnBlock.STAGE) == 15)
        {
            world.getBlockEntity(pos, TFCBlockEntities.PIT_KILN.get()).ifPresent(PitKilnBlockEntity::tryLight);
        }
        else if (block == TFCBlocks.CHARCOAL_PILE.get() && state.getValue(CharcoalPileBlock.LAYERS) >= 7 && CharcoalForgeBlock.isValid(world, pos))
        {
            CharcoalForgeBlockEntity.createFromCharcoalPile(world, pos);
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.CHARCOAL_FORGE.get() && CharcoalForgeBlock.isValid(world, pos))
        {
            final BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof CharcoalForgeBlockEntity forge && forge.light(state))
            {
                event.setCanceled(true);
            }
        }
    }

    public static void onProjectileImpact(ProjectileImpactEvent event)
    {
        if (!TFCConfig.SERVER.enableFireArrowSpreading.get()) return;
        Projectile projectile = event.getProjectile();
        HitResult result = event.getRayTraceResult();
        if (result.getType() == HitResult.Type.BLOCK && projectile.isOnFire())
        {
            BlockHitResult blockResult = (BlockHitResult) result;
            BlockPos pos = blockResult.getBlockPos();
            StartFireEvent.startFire(projectile.level, pos, projectile.level.getBlockState(pos), blockResult.getDirection(), null, ItemStack.EMPTY);
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        // When facing up in the rain, player slowly recovers thirst.
        final Player player = event.player;
        final Level level = player.getLevel();
        final float angle = Mth.wrapDegrees(player.getXRot()); // Copied from DebugScreenOverlay, which is the value in F3
        if (angle <= -80 && !level.isClientSide() && level.isRainingAt(player.blockPosition()) && player.getFoodData() instanceof TFCFoodData foodData)
        {
            foodData.addThirst(TFCConfig.SERVER.thirstGainedFromDrinkingInTheRain.get().floatValue());
        }

        // todo: PotionExpiryEvent is not called on the client due to bad design. So we have to fake it here. Needs a forge PR
        if (level.isClientSide)
        {
            final MobEffectInstance effect = player.getEffect(TFCEffects.PINNED.get());
            if (effect != null && effect.getDuration() <= 1)
            {
                player.setForcedPose(null);
            }
        }
    }

    public static void onEffectRemove(PotionEvent.PotionRemoveEvent event)
    {
        if (event.getPotion() == TFCEffects.PINNED.get() && event.getEntityLiving() instanceof Player player)
        {
            player.setForcedPose(null);
        }
    }

    public static void onEffectExpire(PotionEvent.PotionExpiryEvent event)
    {
        MobEffectInstance instance = event.getPotionEffect();
        if (instance != null && instance.getEffect() == TFCEffects.PINNED.get() && event.getEntityLiving() instanceof Player player)
        {
            player.setForcedPose(null);
        }
    }

    /**
     * Set a very short lifespan to item entities that are cool-able. This causes ItemExpireEvent to fire at regular intervals
     */
    public static void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (event.getEntity() instanceof ItemEntity entity && !event.getWorld().isClientSide && TFCConfig.SERVER.coolHeatablesinLevel.get())
        {
            final ItemStack item = entity.getItem();
            item.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> {
                if (cap.getTemperature() > 0f)
                {
                    entity.lifespan = TFCConfig.SERVER.ticksBeforeItemCool.get();
                }
            });
        }
    }

    /**
     * If the item is heated, we check for blocks below and within that would cause it to cool.
     * Since we don't want the item to actually expire, we set the expiry time to a small number that allows us to revisit the same code soon.
     *
     * By cancelling the event, we guarantee that the item will not actually expire.
     */
    public static void onItemExpire(ItemExpireEvent event)
    {
        if (!TFCConfig.SERVER.coolHeatablesinLevel.get()) return;
        final ItemEntity entity = event.getEntityItem();
        final ServerLevel level = (ServerLevel) entity.getLevel();
        final ItemStack stack = entity.getItem();
        final BlockPos pos = entity.blockPosition();

        stack.getCapability(HeatCapability.CAPABILITY).ifPresent(heat -> {
            final int lifespan = stack.getItem().getEntityLifespan(stack, level);
            if (entity.lifespan >= lifespan) return; // the case where the item has been sitting out for longer than the lifespan. So it should be removed by the game.

            final float itemTemp = heat.getTemperature();
            if (itemTemp > 0f)
            {
                float coolAmount = 0;
                final BlockState state = level.getBlockState(pos);
                final FluidState fluid = level.getFluidState(pos);
                if (fluid.is(FluidTags.WATER))
                {
                    coolAmount = 50f;
                    if (level.random.nextFloat() < 0.001F)
                    {
                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
                }
                else if (state.is(Blocks.SNOW))
                {
                    coolAmount = 70f;
                    if (level.random.nextFloat() < 0.1F)
                    {
                        final int layers = state.getValue(SnowLayerBlock.LAYERS);
                        if (layers > 1)
                        {
                            level.setBlockAndUpdate(pos, state.setValue(SnowLayerBlock.LAYERS, layers - 1));
                        }
                        else
                        {
                            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        }
                    }
                }
                else
                {
                    final BlockPos belowPos = pos.below();
                    final BlockState belowState = level.getBlockState(belowPos);
                    if (belowState.is(Blocks.SNOW_BLOCK))
                    {
                        coolAmount = 75f;
                        if (level.random.nextFloat() < 0.1F)
                        {
                            level.setBlockAndUpdate(belowPos, Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 7));
                        }
                    }
                    else if (belowState.getMaterial() == Material.ICE)
                    {
                        coolAmount = 100f;
                        if (level.random.nextFloat() < 0.01F)
                        {
                            level.setBlockAndUpdate(belowPos, belowState.is(TFCBlocks.SEA_ICE.get()) ? TFCBlocks.SALT_WATER.get().defaultBlockState() : Blocks.WATER.defaultBlockState());
                        }
                    }
                    else if (belowState.getMaterial() == Material.ICE_SOLID)
                    {
                        coolAmount = 125f;
                        if (level.random.nextFloat() < 0.005F)
                        {
                            level.setBlockAndUpdate(belowPos, Blocks.WATER.defaultBlockState());
                        }
                    }
                }

                if (coolAmount > 0f)
                {
                    heat.setTemperature(Math.max(0f, heat.getTemperature() - coolAmount));
                    Helpers.playSound(level, pos, SoundEvents.LAVA_EXTINGUISH);
                    level.sendParticles(ParticleTypes.SMOKE, entity.getX(), entity.getY(), entity.getZ(), 1, 0D, 0D, 0D, 1f);
                }
                event.setExtraLife(heat.getTemperature() == 0f ? lifespan : TFCConfig.SERVER.ticksBeforeItemCool.get());
                //entity.setNoPickUpDelay();
            }
            else
            {
                event.setExtraLife(lifespan);
            }
            event.setCanceled(true);
        });
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayer)
        {
            TFCFoodData.replaceFoodStats(event.getPlayer());

            final ServerLevel overworld = ServerLifecycleHooks.getCurrentServer().overworld();
            if (overworld.getChunkSource().getGenerator() instanceof ChunkGeneratorExtension ex)
            {
                PacketHandler.send(PacketDistributor.ALL.noArg(), new ClimateSettingsUpdatePacket(ex.getBiomeSource().getTemperatureSettings(), ex.getClimateSeed()));
            }
        }
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayer)
        {
            TFCFoodData.replaceFoodStats(event.getPlayer());
        }
    }

    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayer)
        {
            TFCFoodData.replaceFoodStats(event.getPlayer());
        }
    }

    public static void onServerChat(ServerChatEvent event)
    {
        // Apply intoxication after six hours
        final long intoxicatedTicks = event.getPlayer().getCapability(PlayerDataCapability.CAPABILITY).map(p -> p.getIntoxicatedTicks() - 6 * ICalendar.TICKS_IN_HOUR).orElse(0L);
        if (intoxicatedTicks > 0)
        {
            final float intoxicationChance = Mth.clamp((float) (intoxicatedTicks - 6 * ICalendar.TICKS_IN_HOUR) / PlayerData.MAX_INTOXICATED_TICKS, 0, 0.7f);
            final Random random = event.getPlayer().getRandom();
            final String originalMessage = event.getMessage();
            final String[] words = originalMessage.split(" ");
            for (int i = 0; i < words.length; i++)
            {
                String word = words[i];
                if (word.length() == 0)
                {
                    continue;
                }

                // Swap two letters
                if (random.nextFloat() < intoxicationChance && word.length() >= 2)
                {
                    int pos = random.nextInt(word.length() - 1);
                    word = word.substring(0, pos) + word.charAt(pos + 1) + word.charAt(pos) + word.substring(pos + 2);
                }

                // Repeat / slur letters
                if (random.nextFloat() < intoxicationChance)
                {
                    int pos = random.nextInt(word.length());
                    char repeat = word.charAt(pos);
                    int amount = 1 + random.nextInt(3);
                    word = word.substring(0, pos) + new String(new char[amount]).replace('\0', repeat) + (pos + 1 < word.length() ? word.substring(pos + 1) : "");
                }

                // Add additional letters
                if (random.nextFloat() < intoxicationChance)
                {
                    int pos = random.nextInt(word.length());
                    char replacement = ALPHABET.charAt(random.nextInt(ALPHABET.length()));
                    if (Character.isUpperCase(word.charAt(random.nextInt(word.length()))))
                    {
                        replacement = Character.toUpperCase(replacement);
                    }
                    word = word.substring(0, pos) + replacement + (pos + 1 < word.length() ? word.substring(pos + 1) : "");
                }

                words[i] = word;
            }
            event.setComponent(new TranslatableComponent("<" + event.getUsername() + "> " + String.join(" ", words)));
        }
    }

    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getHand() == InteractionHand.MAIN_HAND && event.getItemStack().isEmpty())
        {
            final InteractionResult result = Drinkable.attemptDrink(event.getWorld(), event.getPlayer(), true);
            if (result != InteractionResult.PASS)
            {
                event.setCanceled(true);
                event.setCancellationResult(result);
            }
        }
    }

    public static void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        final UseOnContext context = new UseOnContext(event.getPlayer(), event.getHand(), FAKE_MISS);
        InteractionManager.onItemUse(event.getItemStack(), context, true).ifPresent(result -> {
            event.setCanceled(true);
            event.setCancellationResult(result);
        });
    }

    public static void onPlayerRightClickEmpty(PlayerInteractEvent.RightClickEmpty event)
    {
        if (event.getHand() == InteractionHand.MAIN_HAND && event.getItemStack().isEmpty())
        {
            // Cannot be cancelled, only fired on client.
            InteractionResult result = Drinkable.attemptDrink(event.getWorld(), event.getPlayer(), false);
            if (result == InteractionResult.SUCCESS)
            {
                PacketHandler.send(PacketDistributor.SERVER.noArg(), new PlayerDrinkPacket());
            }
        }
    }

    public static void onDataPackSync(OnDatapackSyncEvent event)
    {
        // Sync managers
        final ServerPlayer player = event.getPlayer();
        final PacketDistributor.PacketTarget target = player == null ? PacketDistributor.ALL.noArg() : PacketDistributor.PLAYER.with(() -> player);

        PacketHandler.send(target, Metal.MANAGER.createSyncPacket());
        PacketHandler.send(target, Fuel.MANAGER.createSyncPacket());
        PacketHandler.send(target, Fertilizer.MANAGER.createSyncPacket());
        PacketHandler.send(target, HeatCapability.MANAGER.createSyncPacket());
        PacketHandler.send(target, FoodCapability.MANAGER.createSyncPacket());
        PacketHandler.send(target, ItemSizeManager.MANAGER.createSyncPacket());
    }

    /**
     * Deny all traditional uses of bone meal directly to grow crops.
     * Fertilizer is used as a replacement.
     */
    public static void onBoneMeal(BonemealEvent event)
    {
        if (!TFCConfig.SERVER.enableVanillaBonemeal.get())
        {
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }
}