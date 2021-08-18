/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppedEvent;

import net.dries007.tfc.common.TFCTags;
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
import net.dries007.tfc.common.capabilities.food.TFCFoodStats;
import net.dries007.tfc.common.capabilities.forge.ForgingCapability;
import net.dries007.tfc.common.capabilities.forge.ForgingHandler;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.HeatDefinition;
import net.dries007.tfc.common.capabilities.player.PlayerData;
import net.dries007.tfc.common.capabilities.player.PlayerDataCapability;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.command.TFCCommands;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.common.tileentity.AbstractFirepitTileEntity;
import net.dries007.tfc.common.tileentity.CharcoalForgeTileEntity;
import net.dries007.tfc.common.tileentity.PitKilnTileEntity;
import net.dries007.tfc.common.tileentity.TickCounterTileEntity;
import net.dries007.tfc.common.types.FuelManager;
import net.dries007.tfc.common.types.Metal;
import net.dries007.tfc.common.types.MetalItemManager;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.ChunkUnwatchPacket;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.util.CacheInvalidationListener;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.events.StartFireEvent;
import net.dries007.tfc.util.support.SupportManager;
import net.dries007.tfc.util.tracker.WorldTracker;
import net.dries007.tfc.util.tracker.WorldTrackerCapability;
import net.dries007.tfc.world.biome.BiomeSourceExtension;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCache;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;
import net.dries007.tfc.world.chunkdata.ChunkGeneratorExtension;
import net.dries007.tfc.world.settings.RockLayerSettings;

import static net.dries007.tfc.common.blocks.devices.CharcoalForgeBlock.HEAT;

public final class ForgeEventHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

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
        bus.addListener(ForgeEventHandler::onServerStopped);
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
        bus.addListener(ForgeEventHandler::onPlayerLoggedIn);
        bus.addListener(ForgeEventHandler::onPlayerRespawn);
        bus.addListener(ForgeEventHandler::onPlayerChangeDimension);
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
            final BiomeSourceExtension biomeProvider = extension.getBiomeSource();
            final Random random = new Random(world.getSeed());
            final int spawnDistance = biomeProvider.getSpawnDistance();

            BlockPos pos = biomeProvider.findBiomeIgnoreClimate(biomeProvider.getSpawnCenterX(), generator.getSeaLevel(), biomeProvider.getSpawnCenterZ(), spawnDistance, spawnDistance / 256, biome -> biome.getMobSettings().playerSpawnFriendly(), random);
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
                    BlockPos spawnPos = Helpers.findValidSpawnLocation(world, new ChunkPos(chunkPos.x + x, chunkPos.z + z));
                    if (spawnPos != null)
                    {
                        settings.setSpawn(spawnPos, 0);
                        foundExactSpawn = true;
                        break;
                    }
                }

                if ((x == z) || (x < 0 && x == -z) || (x > 0 && x == 1 - z))
                {
                    int temp = xStep;
                    xStep = -zStep;
                    zStep = temp;
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
            final Level world = event.getObject().getLevel();
            final ChunkPos chunkPos = event.getObject().getPos();

            ChunkData data;
            if (Helpers.isClientSide(world))
            {
                // This may happen before or after the chunk is watched and synced to client
                // Default to using the cache. If later the sync packet arrives it will update the same instance in the chunk capability and cache
                data = ChunkDataCache.CLIENT.getOrCreate(chunkPos, RockLayerSettings.EMPTY);
            }
            else
            {
                // Chunk was created on server thread.
                // 1. If this was due to world gen, it won't have any cap data. This is where we clear the world gen cache and attach it to the chunk
                // 2. If this was due to chunk loading, the caps will be deserialized from NBT after this event is posted. Attach empty data here
                data = ChunkDataCache.WORLD_GEN.remove(chunkPos);
                if (data == null && world instanceof ServerLevel serverWorld && serverWorld.getChunkSource().getGenerator() instanceof ChunkGeneratorExtension generator)
                {
                    data = new ChunkData(chunkPos, generator.getRockLayerSettings());
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
        if (event.getChunk().getStatus().getChunkType() == ChunkStatus.ChunkType.PROTOCHUNK)
        {
            final ChunkPos pos = event.getChunk().getPos();
            final ChunkData data = ChunkDataCache.WORLD_GEN.get(pos);
            if (data != null && data.getStatus() != ChunkData.Status.EMPTY)
            {
                event.getData().put("tfc_protochunk_data", data.serializeNBT());
            }
        }
    }

    /**
     * @see #onChunkDataSave(ChunkDataEvent.Save)
     */
    public static void onChunkDataLoad(ChunkDataEvent.Load event)
    {
        if (event.getChunk().getStatus().getChunkType() == ChunkStatus.ChunkType.PROTOCHUNK && event.getData().contains("tfc_protochunk_data", Constants.NBT.TAG_COMPOUND) && event.getChunk() instanceof ProtoChunk chunk && chunk.levelHeightAccessor instanceof ServerLevel level && level.getChunkSource().getGenerator() instanceof ChunkGeneratorExtension generator)
        {
            final ChunkPos pos = event.getChunk().getPos();
            final ChunkData data = ChunkDataCache.WORLD_GEN.getOrCreate(pos, generator.getRockLayerSettings());
            data.deserializeNBT(event.getData().getCompound("tfc_protochunk_data"));
        }
    }

    public static void addReloadListeners(AddReloadListenerEvent event)
    {
        // Resource reload listeners
        ReloadableResourceManager resourceManager = (ReloadableResourceManager) event.getDataPackRegistries().getResourceManager();
        resourceManager.registerReloadListener(Metal.MANAGER);
        resourceManager.registerReloadListener(MetalItemManager.MANAGER);
        resourceManager.registerReloadListener(FuelManager.MANAGER);
        resourceManager.registerReloadListener(SupportManager.INSTANCE);
        resourceManager.registerReloadListener(HeatCapability.MANAGER);
        resourceManager.registerReloadListener(ItemSizeManager.MANAGER);
        resourceManager.registerReloadListener(FoodCapability.MANAGER);

        // Last
        resourceManager.registerReloadListener(CacheInvalidationListener.INSTANCE);
    }

    public static void beforeServerStart(FMLServerAboutToStartEvent event)
    {
        CacheInvalidationListener.INSTANCE.invalidateAll();
    }

    public static void onServerStopped(FMLServerStoppedEvent event)
    {
        CacheInvalidationListener.INSTANCE.invalidateAll();
    }

    public static void registerCommands(RegisterCommandsEvent event)
    {
        LOGGER.debug("Registering TFC Commands");
        TFCCommands.register(event.getDispatcher());
    }

    public static void onBlockBroken(BlockEvent.BreakEvent event)
    {
        // Check for possible collapse
        LevelAccessor world = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);

        if (TFCTags.Blocks.CAN_TRIGGER_COLLAPSE.contains(state.getBlock()) && world instanceof Level)
        {
            CollapseRecipe.tryTriggerCollapse((Level) world, pos);
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
        if (event.getWorld() instanceof final ServerLevel world)
        {
            for (Direction direction : event.getNotifiedSides())
            {
                // Check each notified block for a potential gravity block
                final BlockPos pos = event.getPos().relative(direction);
                final BlockState state = world.getBlockState(pos);

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
        if (event.getWorld() instanceof final ServerLevel world)
        {
            if (TFCConfig.SERVER.enableForcedTFCGameRules.get())
            {
                final GameRules rules = world.getGameRules();
                final MinecraftServer server = world.getServer();

                rules.getRule(GameRules.RULE_NATURAL_REGENERATION).set(false, server);
                rules.getRule(GameRules.RULE_DOINSOMNIA).set(false, server);
                rules.getRule(GameRules.RULE_DO_PATROL_SPAWNING).set(false, server);
                rules.getRule(GameRules.RULE_DO_TRADER_SPAWNING).set(false, server);

                LOGGER.info("Updating TFC Relevant Game Rules.");
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

        if (block == (TFCBlocks.FIREPIT.get()) || block == (TFCBlocks.POT.get()) || block == (TFCBlocks.GRILL.get()))
        {
            AbstractFirepitTileEntity<?> firepit = Helpers.getTileEntity(world, pos, AbstractFirepitTileEntity.class);
            if (firepit != null)
            {
                firepit.light(state);
            }
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.TORCH.get() || block == TFCBlocks.WALL_TORCH.get())
        {
            TickCounterTileEntity te = Helpers.getTileEntity(world, pos, TickCounterTileEntity.class);
            if (te != null)
            {
                te.resetCounter();
            }
            event.setCanceled(true);
        }
        else if (block == (TFCBlocks.DEAD_TORCH.get()))
        {
            world.setBlockAndUpdate(pos, TFCBlocks.TORCH.get().defaultBlockState());
            event.setCanceled(true);
        }
        else if (block == (TFCBlocks.DEAD_WALL_TORCH.get()))
        {
            Direction direction = state.getValue(DeadWallTorchBlock.FACING);
            world.setBlockAndUpdate(pos, TFCBlocks.WALL_TORCH.get().defaultBlockState().setValue(TFCWallTorchBlock.FACING, direction));
            event.setCanceled(true);
        }
        else if (block == (TFCBlocks.LOG_PILE.get()))
        {
            BurningLogPileBlock.tryLightLogPile(world, pos);
            event.setCanceled(true);
        }
        else if (block == (TFCBlocks.PIT_KILN.get()) && state.getValue(PitKilnBlock.STAGE) == 15)
        {
            PitKilnTileEntity kiln = Helpers.getTileEntity(world, pos, PitKilnTileEntity.class);
            if (kiln != null)
            {
                kiln.tryLight();
            }
        }
        else if (block == (TFCBlocks.CHARCOAL_PILE.get()) && state.getValue(CharcoalPileBlock.LAYERS) >= 7 && CharcoalForgeBlock.isValid(world, pos))
        {
            world.setBlockAndUpdate(pos, TFCBlocks.CHARCOAL_FORGE.get().defaultBlockState().setValue(HEAT, 2));
            CharcoalForgeTileEntity forge = Helpers.getTileEntity(world, pos, CharcoalForgeTileEntity.class);
            if (forge != null)
            {
                forge.onCreate();
            }
        }
        else if (block == (TFCBlocks.CHARCOAL_FORGE.get()) && CharcoalForgeBlock.isValid(world, pos))
        {
            world.setBlockAndUpdate(pos, state.setValue(HEAT, 2));
        }
    }

    public static void onProjectileImpact(ProjectileImpactEvent event)
    {
        // todo: find a way to use this event generically for both arrows and fireballs
        /*
        if (!TFCConfig.SERVER.enableFireArrowSpreading.get()) return;
        AbstractArrow arrow = event.getArrow();
        HitResult result = event.getRayTraceResult();
        if (result.getType() == HitResult.Type.BLOCK && arrow.isOnFire())
        {
            BlockHitResult blockResult = (BlockHitResult) result;
            BlockPos pos = blockResult.getBlockPos();
            Level world = arrow.level;
            StartFireEvent.startFire(world, pos, world.getBlockState(pos), blockResult.getDirection(), null, ItemStack.EMPTY);
        }

        if (!TFCConfig.SERVER.enableFireArrowSpreading.get()) return;
        AbstractHurtingProjectile fireball = event.getFireball();
        HitResult result = event.getRayTraceResult();
        if (result.getType() == HitResult.Type.BLOCK)
        {
            BlockHitResult blockResult = (BlockHitResult) result;
            BlockPos pos = blockResult.getBlockPos();
            Level world = fireball.level;
            StartFireEvent.startFire(world, pos, world.getBlockState(pos), blockResult.getDirection(), null, ItemStack.EMPTY);
        }
         */
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayer)
        {
            TFCFoodStats.replaceFoodStats(event.getPlayer());
        }
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayer)
        {
            TFCFoodStats.replaceFoodStats(event.getPlayer());
        }
    }

    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayer)
        {
            TFCFoodStats.replaceFoodStats(event.getPlayer());
        }
    }
}