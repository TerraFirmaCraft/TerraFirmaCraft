/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import java.util.Random;
import java.util.concurrent.Executor;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BambooBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCEffects;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blockentities.BloomeryBlockEntity;
import net.dries007.tfc.common.blockentities.CharcoalForgeBlockEntity;
import net.dries007.tfc.common.blockentities.PitKilnBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.TFCCandleBlock;
import net.dries007.tfc.common.blocks.TFCCandleCakeBlock;
import net.dries007.tfc.common.blocks.devices.AnvilBlock;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.blocks.devices.BlastFurnaceBlock;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.blocks.devices.BurningLogPileBlock;
import net.dries007.tfc.common.blocks.devices.CharcoalForgeBlock;
import net.dries007.tfc.common.blocks.devices.LampBlock;
import net.dries007.tfc.common.blocks.devices.PitKilnBlock;
import net.dries007.tfc.common.blocks.devices.PowderkegBlock;
import net.dries007.tfc.common.blocks.devices.SluiceBlock;
import net.dries007.tfc.common.blocks.rock.AqueductBlock;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockAnvilBlock;
import net.dries007.tfc.common.blocks.wood.TFCLecternBlock;
import net.dries007.tfc.common.capabilities.egg.EggCapability;
import net.dries007.tfc.common.capabilities.egg.EggHandler;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodDefinition;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.dries007.tfc.common.capabilities.forge.Forging;
import net.dries007.tfc.common.capabilities.forge.ForgingBonus;
import net.dries007.tfc.common.capabilities.forge.ForgingCapability;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.HeatDefinition;
import net.dries007.tfc.common.capabilities.player.PlayerData;
import net.dries007.tfc.common.capabilities.player.PlayerDataCapability;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.commands.TFCCommands;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.Container;
import net.dries007.tfc.common.container.PestContainer;
import net.dries007.tfc.common.entities.Fauna;
import net.dries007.tfc.common.entities.HoldingMinecart;
import net.dries007.tfc.common.entities.predator.Predator;
import net.dries007.tfc.common.items.DynamicBowlFood;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.accessor.ChunkAccessAccessor;
import net.dries007.tfc.mixin.accessor.RecipeManagerAccessor;
import net.dries007.tfc.network.ChunkUnwatchPacket;
import net.dries007.tfc.network.EffectExpirePacket;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.PlayerDrinkPacket;
import net.dries007.tfc.network.UpdateClimateModelPacket;
import net.dries007.tfc.util.AxeLoggingHelper;
import net.dries007.tfc.util.Drinkable;
import net.dries007.tfc.util.EntityDamageResistance;
import net.dries007.tfc.util.Fertilizer;
import net.dries007.tfc.util.Fuel;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.InteractionManager;
import net.dries007.tfc.util.ItemDamageResistance;
import net.dries007.tfc.util.LampFuel;
import net.dries007.tfc.util.LegacyMaterials;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.Pannable;
import net.dries007.tfc.util.PhysicalDamageType;
import net.dries007.tfc.util.SelfTests;
import net.dries007.tfc.util.Sluiceable;
import net.dries007.tfc.util.Support;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.events.LoggingEvent;
import net.dries007.tfc.util.events.SelectClimateModelEvent;
import net.dries007.tfc.util.events.StartFireEvent;
import net.dries007.tfc.util.tracker.WeatherHelpers;
import net.dries007.tfc.util.tracker.WorldTracker;
import net.dries007.tfc.util.tracker.WorldTrackerCapability;
import net.dries007.tfc.world.NoopClimateSampler;
import net.dries007.tfc.world.biome.BiomeSourceExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCache;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;
import net.dries007.tfc.world.chunkdata.ChunkGeneratorExtension;
import net.dries007.tfc.world.settings.RockLayerSettings;

public final class ForgeEventHandler
{
    private static final Logger LOGGER = LogUtils.getLogger();
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
        bus.addListener(ForgeEventHandler::registerCommands);
        bus.addListener(ForgeEventHandler::onBlockBroken);
        bus.addListener(ForgeEventHandler::onBlockPlace);
        bus.addListener(ForgeEventHandler::onBreakSpeed);
        bus.addListener(ForgeEventHandler::onNeighborUpdate);
        bus.addListener(ForgeEventHandler::onExplosionDetonate);
        bus.addListener(ForgeEventHandler::onWorldTick);
        bus.addListener(ForgeEventHandler::onWorldLoad);
        bus.addListener(ForgeEventHandler::onCreateNetherPortal);
        bus.addListener(ForgeEventHandler::onFluidPlaceBlock);
        bus.addListener(ForgeEventHandler::onFluidCreateSource);
        bus.addListener(ForgeEventHandler::onFireStart);
        bus.addListener(ForgeEventHandler::onProjectileImpact);
        bus.addListener(ForgeEventHandler::onPlayerTick);
        bus.addListener(ForgeEventHandler::onEffectRemove);
        bus.addListener(ForgeEventHandler::onEffectExpire);
        bus.addListener(ForgeEventHandler::onLivingJump);
        bus.addListener(ForgeEventHandler::onLivingHurt);
        bus.addListener(ForgeEventHandler::onShieldBlock);
        bus.addListener(ForgeEventHandler::onLivingSpawnCheck);
        bus.addListener(ForgeEventHandler::onEntityJoinWorld);
        bus.addListener(ForgeEventHandler::onItemExpire);
        bus.addListener(ForgeEventHandler::onPlayerLoggedIn);
        bus.addListener(ForgeEventHandler::onPlayerRespawn);
        bus.addListener(ForgeEventHandler::onPlayerDeath);
        bus.addListener(ForgeEventHandler::onPlayerChangeDimension);
        bus.addListener(ForgeEventHandler::onServerChat);
        bus.addListener(ForgeEventHandler::onPlayerRightClickBlock);
        bus.addListener(EventPriority.LOWEST, true, ForgeEventHandler::onPlayerRightClickBlockLowestPriority);
        bus.addListener(ForgeEventHandler::onPlayerRightClickItem);
        bus.addListener(ForgeEventHandler::onPlayerRightClickEmpty);
        bus.addListener(ForgeEventHandler::onItemUseFinish);
        bus.addListener(ForgeEventHandler::addReloadListeners);
        bus.addListener(ForgeEventHandler::onDataPackSync);
        bus.addListener(ForgeEventHandler::onTagsUpdated);
        bus.addListener(ForgeEventHandler::onBoneMeal);
        bus.addListener(ForgeEventHandler::onSelectClimateModel);
        bus.addListener(ForgeEventHandler::onAnimalTame);
        bus.addListener(ForgeEventHandler::onContainerOpen);
        bus.addListener(ForgeEventHandler::onCropsGrow);
        bus.addListener(ForgeEventHandler::onMount);
        bus.addListener(ForgeEventHandler::onEntityInteract);
    }

    /**
     * Duplicates logic from {@link MinecraftServer#
     * setInitialSpawn(ServerLevel, ServerLevelData, boolean, boolean)} as that version only asks the dimension for the sea level...
     */
    public static void onCreateWorldSpawn(WorldEvent.CreateSpawnPosition event)
    {
        if (event.getWorld() instanceof ServerLevel level && level.getChunkSource().getGenerator() instanceof ChunkGeneratorExtension extension)
        {
            final ChunkGenerator generator = extension.self();
            final ServerLevelData settings = event.getSettings();
            final BiomeSourceExtension source = extension.getBiomeSourceExtension();
            final Random random = new Random(level.getSeed());

            Pair<BlockPos, Holder<Biome>> posPair = generator.getBiomeSource().findBiomeHorizontal(source.settings().spawnCenterX(), 0, source.settings().spawnCenterZ(), source.settings().spawnDistance(), source.settings().spawnDistance() / 256, biome -> TFCBiomes.getExtensionOrThrow(level, biome.value()).isSpawnable(), random, false, NoopClimateSampler.INSTANCE);
            BlockPos pos;
            ChunkPos chunkPos;
            if (posPair == null)
            {
                LOGGER.warn("Unable to find spawn biome!");
                pos = new BlockPos(0, generator.getSeaLevel(), 0);
            }
            else
            {
                pos = posPair.getFirst();
            }
            chunkPos = new ChunkPos(pos);

            settings.setSpawn(chunkPos.getWorldPosition().offset(8, generator.getSpawnHeight(level), 8), 0.0F);
            boolean foundExactSpawn = false;
            int x = 0, z = 0;
            int xStep = 0;
            int zStep = -1;

            for (int tries = 0; tries < 1024; ++tries)
            {
                if (x > -16 && x <= 16 && z > -16 && z <= 16)
                {
                    final BlockPos spawnPos = PlayerRespawnLogic.getSpawnPosInChunk(level, new ChunkPos(chunkPos.x + x, chunkPos.z + z));
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

            if (level.getServer().getWorldData().worldGenSettings().generateBonusChest())
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
        event.addCapability(WorldTrackerCapability.KEY, new WorldTracker(event.getObject()));
    }

    public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event)
    {
        ItemStack stack = event.getObject();
        if (!stack.isEmpty())
        {
            // Attach mandatory capabilities
            event.addCapability(ForgingCapability.KEY, new Forging(stack));

            // Optional capabilities
            HeatDefinition def = HeatCapability.get(stack);
            if (def != null)
            {
                event.addCapability(HeatCapability.KEY, def.create());
            }

            FoodDefinition food = FoodCapability.get(stack);
            if (food != null)
            {
                event.addCapability(FoodCapability.KEY, FoodDefinition.getHandler(food, stack));
            }

            if (stack.getItem() == Items.EGG)
            {
                event.addCapability(EggCapability.KEY, new EggHandler(stack));
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

    public static void registerCommands(RegisterCommandsEvent event)
    {
        LOGGER.debug("Registering TFC Commands");
        TFCCommands.registerCommands(event.getDispatcher());
    }

    public static void onBlockBroken(BlockEvent.BreakEvent event)
    {
        // Trigger a collapse
        final LevelAccessor levelAccess = event.getWorld();
        final BlockPos pos = event.getPos();
        final BlockState state = levelAccess.getBlockState(pos);

        if (Helpers.isBlock(state, TFCTags.Blocks.CAN_TRIGGER_COLLAPSE) && levelAccess instanceof Level level)
        {
            CollapseRecipe.tryTriggerCollapse(level, pos);
            return;
        }

        // Chop down a tree
        final ItemStack stack = event.getPlayer().getMainHandItem();
        if (AxeLoggingHelper.isLoggingAxe(stack) && AxeLoggingHelper.isLoggingBlock(state) && !MinecraftForge.EVENT_BUS.post(new LoggingEvent(levelAccess, pos, state, stack)))
        {
            event.setCanceled(true); // Cancel regardless of outcome of logging
            AxeLoggingHelper.doLogging(levelAccess, pos, event.getPlayer(), stack);
        }
    }

    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event)
    {
        if (event.getWorld() instanceof final ServerLevel world)
        {
            final BlockPos pos = event.getPos();
            final BlockState state = event.getState();

            if (Helpers.isBlock(state, TFCTags.Blocks.CAN_LANDSLIDE))
            {
                world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addLandslidePos(pos));
            }

            if (Helpers.isBlock(state, TFCTags.Blocks.BREAKS_WHEN_ISOLATED))
            {
                world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addIsolatedPos(pos));
            }
        }
    }

    public static void onBreakSpeed(PlayerEvent.BreakSpeed event)
    {
        // Apply mining speed modifiers from forging bonuses
        final ForgingBonus bonus = ForgingBonus.get(event.getPlayer().getMainHandItem());
        if (bonus != ForgingBonus.NONE)
        {
            event.setNewSpeed(event.getNewSpeed() * bonus.efficiency());
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

                if (Helpers.isBlock(state, TFCTags.Blocks.CAN_LANDSLIDE))
                {
                    level.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addLandslidePos(pos));
                }

                if (Helpers.isBlock(state.getBlock(), TFCTags.Blocks.BREAKS_WHEN_ISOLATED))
                {
                    level.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addIsolatedPos(pos));
                }
            }
        }
    }

    public static void onExplosionDetonate(ExplosionEvent.Detonate event)
    {
        if (!event.getWorld().isClientSide)
        {
            event.getWorld().getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addCollapsePositions(new BlockPos(event.getExplosion().getPosition()), event.getAffectedBlocks()));
        }
    }

    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && event.world instanceof ServerLevel level)
        {
            WeatherHelpers.preAdvancedWeatherCycle(level);
            level.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.tick(level));
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

            Climate.onWorldLoad(level);
            if (level.dimension() == Level.OVERWORLD)
            {
                ItemSizeManager.applyItemStackSizeOverrides();
                SelfTests.runServerSelfTests();
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
        // Currently, getOriginalState gets the fluid block that's placing the block, not the block getting placed
        BlockState state = event.getNewState();
        if (Helpers.isBlock(state, Blocks.STONE))
        {
            event.setNewState(TFCBlocks.ROCK_BLOCKS.get(net.dries007.tfc.common.blocks.rock.Rock.GABBRO).get(net.dries007.tfc.common.blocks.rock.Rock.BlockType.HARDENED).get().defaultBlockState());
        }
        else if (Helpers.isBlock(state, Blocks.COBBLESTONE))
        {
            event.setNewState(TFCBlocks.ROCK_BLOCKS.get(net.dries007.tfc.common.blocks.rock.Rock.RHYOLITE).get(net.dries007.tfc.common.blocks.rock.Rock.BlockType.HARDENED).get().defaultBlockState());
        }
        else if (Helpers.isBlock(state, Blocks.BASALT))
        {
            event.setNewState(TFCBlocks.ROCK_BLOCKS.get(net.dries007.tfc.common.blocks.rock.Rock.BASALT).get(Rock.BlockType.HARDENED).get().defaultBlockState());
        }
    }

    public static void onFluidCreateSource(BlockEvent.CreateFluidSourceEvent event)
    {
        final LevelReader level = event.getWorld();
        final BlockPos pos = event.getPos();
        final BlockState state = event.getState();

        if (state.getBlock() instanceof AqueductBlock)
        {
            event.setResult(Event.Result.DENY); // Waterlogged aqueducts do not count as the source when creating source blocks
        }

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            final BlockPos relPos = pos.relative(direction).above();
            final BlockState relState = level.getBlockState(relPos);
            if (relState.getBlock() instanceof SluiceBlock && !relState.getValue(SluiceBlock.UPPER) && relState.getValue(SluiceBlock.FACING) == direction.getOpposite())
            {
                event.setResult(Event.Result.DENY); // This block might be being fed by a sluice - so don't allow it to create more source blocks.
            }
        }
    }

    public static void onFireStart(StartFireEvent event)
    {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        Block block = state.getBlock();

        if ((block == TFCBlocks.FIREPIT.get() || block == TFCBlocks.POT.get() || block == TFCBlocks.GRILL.get()) && event.isStrong())
        {
            final BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof AbstractFirepitBlockEntity<?> firepit && firepit.light(state))
            {
                event.setCanceled(true);
            }
        }
        else if (block == TFCBlocks.TORCH.get() || block == TFCBlocks.WALL_TORCH.get())
        {
            level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(TickCounterBlockEntity::resetCounter);
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.DEAD_TORCH.get())
        {
            level.setBlockAndUpdate(pos, TFCBlocks.TORCH.get().defaultBlockState());
            level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(TickCounterBlockEntity::resetCounter);
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.DEAD_WALL_TORCH.get())
        {
            level.setBlockAndUpdate(pos, TFCBlocks.WALL_TORCH.get().withPropertiesOf(state));
            level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(TickCounterBlockEntity::resetCounter);
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.LOG_PILE.get() && event.isStrong())
        {
            BurningLogPileBlock.tryLightLogPile(level, pos);
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.PIT_KILN.get() && state.getValue(PitKilnBlock.STAGE) == 15 && event.isStrong())
        {
            if (level.getBlockEntity(pos) instanceof PitKilnBlockEntity kiln && kiln.tryLight())
            {
                event.setCanceled(true);
                event.setFireResult(StartFireEvent.FireResult.ALWAYS);
            }
        }
        else if (block == TFCBlocks.CHARCOAL_PILE.get() && state.getValue(CharcoalPileBlock.LAYERS) >= 7 && CharcoalForgeBlock.isValid(level, pos) && event.isStrong())
        {
            CharcoalForgeBlockEntity.createFromCharcoalPile(level, pos);
            event.setCanceled(true);
        }
        else if (block == TFCBlocks.CHARCOAL_FORGE.get() && CharcoalForgeBlock.isValid(level, pos) && event.isStrong())
        {
            final BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof CharcoalForgeBlockEntity forge && forge.light(state))
            {
                event.setCanceled(true);
            }
        }
        else if (block == TFCBlocks.CRUCIBLE.get() && CharcoalForgeBlock.isValid(level, pos.below()) && event.isStrong())
        {
            final BlockEntity entity = level.getBlockEntity(pos.below());
            if (entity instanceof CharcoalForgeBlockEntity forge && forge.light(level.getBlockState(pos.below())))
            {
                event.setCanceled(true);
            }
        }
        else if (block == TFCBlocks.BLOOMERY.get() && !state.getValue(BloomeryBlock.LIT) && event.isStrong())
        {
            final BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof BloomeryBlockEntity bloomery && bloomery.light(state))
            {
                event.setCanceled(true);
            }
        }
        else if (block == TFCBlocks.POWDERKEG.get() && state.getValue(PowderkegBlock.SEALED) && event.isStrong())
        {
            level.getBlockEntity(pos, TFCBlockEntities.POWDERKEG.get()).ifPresent(entity -> {
                entity.setLit(true, event.getPlayer());
                event.setCanceled(true);
            });
        }
        else if (block == TFCBlocks.BLAST_FURNACE.get() && !state.getValue(BlastFurnaceBlock.LIT) && event.isStrong())
        {
            level.getBlockEntity(pos, TFCBlockEntities.BLAST_FURNACE.get()).ifPresent(blastFurnace -> {
                if (blastFurnace.light(level, pos, state))
                {
                    event.setCanceled(true);
                }
            });
        }
        else if (block instanceof LampBlock)
        {
            if (!state.getValue(LampBlock.LIT))
            {
                level.getBlockEntity(pos, TFCBlockEntities.LAMP.get()).ifPresent(lamp -> {
                    if (lamp.getFuel() != null)
                    {
                        level.setBlock(pos, state.setValue(LampBlock.LIT, true), 3);
                        lamp.resetCounter();
                    }
                });
                event.setCanceled(true);
            }
        }
        else if (block instanceof TFCCandleBlock || block instanceof TFCCandleCakeBlock)
        {
            level.setBlock(pos, state.setValue(TFCCandleBlock.LIT, true), Block.UPDATE_ALL_IMMEDIATE);
            TickCounterBlockEntity.reset(level, pos);
            event.setCanceled(true);
        }
        else if (block == Blocks.CARVED_PUMPKIN || block == TFCBlocks.JACK_O_LANTERN.get())
        {
            level.setBlockAndUpdate(pos, Helpers.copyProperty(TFCBlocks.JACK_O_LANTERN.get().defaultBlockState(), state, HorizontalDirectionalBlock.FACING));
            TickCounterBlockEntity.reset(level, pos);
            event.setCanceled(true);
        }
        else if (block instanceof TntBlock tnt)
        {
            tnt.onCaughtFire(state, level, pos, event.getTargetedFace(), event.getPlayer());
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            event.setCanceled(true);
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
        if (angle <= -80 && !level.isClientSide() && level.isRainingAt(player.eyeBlockPosition()) && player.getFoodData() instanceof TFCFoodData foodData)
        {
            foodData.addThirst(TFCConfig.SERVER.thirstGainedFromDrinkingInTheRain.get().floatValue());
        }
        if (!level.isClientSide() && !player.getAbilities().invulnerable && TFCConfig.SERVER.enableOverburdening.get() && level.getGameTime() % 20 == 0)
        {
            final int hugeHeavyCount = Helpers.countOverburdened(player.getInventory());
            if (hugeHeavyCount >= 1)
            {
                player.addEffect(Helpers.getExhausted(false));
            }
            if (hugeHeavyCount == 2)
            {
                player.addEffect(Helpers.getOverburdened(false));
            }
        }
    }

    public static void onEffectRemove(PotionEvent.PotionRemoveEvent event)
    {
        if (event.getEntityLiving() instanceof ServerPlayer player)
        {
            PacketHandler.send(PacketDistributor.PLAYER.with(() -> player), new EffectExpirePacket(event.getPotion()));
            if (event.getPotion() == TFCEffects.PINNED.get())
            {
                player.setForcedPose(null);
            }
        }
    }

    public static void onEffectExpire(PotionEvent.PotionExpiryEvent event)
    {
        final MobEffectInstance instance = event.getPotionEffect();
        if (instance != null && event.getEntityLiving() instanceof ServerPlayer player)
        {
            PacketHandler.send(PacketDistributor.PLAYER.with(() -> player), new EffectExpirePacket(instance.getEffect()));
            if (instance.getEffect() == TFCEffects.PINNED.get())
            {
                player.setForcedPose(null);
            }
        }
    }

    public static void onLivingJump(LivingEvent.LivingJumpEvent event)
    {
        LivingEntity entity = event.getEntityLiving();
        if (entity.hasEffect(TFCEffects.PINNED.get()))
        {
            entity.setDeltaMovement(0, 0, 0);
            entity.hasImpulse = false;
        }
    }

    /**
     * Apply modifications from damage types, player health, and forging bonus, before armor and absorption and other resources are consumed.
     */
    public static void onLivingHurt(LivingHurtEvent event)
    {
        float amount = event.getAmount();

        // Forging bonus
        final Entity attackerEntity = event.getSource().getEntity();
        if (attackerEntity instanceof LivingEntity livingEntity)
        {
            amount *= ForgingBonus.get(livingEntity.getMainHandItem()).damage();

            if (event.getEntityLiving() instanceof Player player)
            {
                Helpers.maybeDisableShield(livingEntity.getMainHandItem(), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY, player, livingEntity);
            }
        }

        // Physical damage type
        amount *= PhysicalDamageType.calculateMultiplier(event.getSource(), event.getEntity());

        // Player health modifier
        if (event.getEntityLiving() instanceof Player player && player.getFoodData() instanceof TFCFoodData foodData)
        {
            amount /= foodData.getHealthModifier();
        }

        event.setAmount(amount);
    }

    public static void onShieldBlock(ShieldBlockEvent event)
    {
        float damageModifier = 1f;
        final Item useItem = event.getEntityLiving().getUseItem().getItem();
        if (event.getDamageSource().getDirectEntity() instanceof LivingEntity livingEntity && livingEntity.getMainHandItem().getItem() instanceof TieredItem attackWeapon)
        {
            if (useItem instanceof TieredItem shieldItem && TierSortingRegistry.getTiersLowerThan(attackWeapon.getTier()).contains(shieldItem.getTier()))
            {
                damageModifier = 0.3f; // shield is worse tier than the attack weapon!
            }
        }
        if (useItem.equals(Items.SHIELD))
        {
            damageModifier = 0.25f; // wooden shield is bad
        }

        event.setBlockedDamage(event.getOriginalBlockedDamage() * damageModifier);
    }

    /**
     * This prevents vanilla mobs from spawning either at all or on the surface.
     */
    public static void onLivingSpawnCheck(LivingSpawnEvent.CheckSpawn event)
    {
        final LivingEntity entity = event.getEntityLiving();
        final LevelAccessor level = event.getWorld();
        final MobSpawnType spawn = event.getSpawnReason();
        // we only care about "natural" spawns
        if (spawn == MobSpawnType.NATURAL || spawn == MobSpawnType.CHUNK_GENERATION || spawn == MobSpawnType.REINFORCEMENT)
        {
            if (Helpers.isEntity(entity, TFCTags.Entities.VANILLA_MONSTERS))
            {
                if (TFCConfig.SERVER.enableVanillaMonsters.get())
                {
                    if (!TFCConfig.SERVER.enableVanillaMonstersOnSurface.get())
                    {
                        final BlockPos pos = entity.blockPosition();
                        if (entity.getType() != EntityType.SLIME && level.getRawBrightness(pos, 0) != 0)
                        {
                            event.setResult(Event.Result.DENY);
                        }
                        else if (level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) <= pos.getY())
                        {
                            event.setResult(Event.Result.DENY);
                        }
                        else if (!Helpers.isBlock(level.getBlockState(pos.below()), TFCTags.Blocks.MONSTER_SPAWNS_ON))
                        {
                            event.setResult(Event.Result.DENY);
                        }
                    }
                }
                else
                {
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    /**
     * Applies multiple effect for entities joining the world:
     * <p>
     * - Set a very short lifespan to item entities that are cool-able. This causes ItemExpireEvent to fire at regular intervals
     * - Causes lightning bolts to strip nearby logs
     * - Prevents skeleton trap horses from spawning (see {@link ServerLevel#tickChunk(LevelChunk, int)}
     * - Prevents some categories of mobs from spawning. Some can't be done in {@link LivingSpawnEvent.CheckSpawn} because Forge does not always fire it.
     */
    public static void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (event.loadedFromDisk())
        {
            // This event is used for modifications to entity spawning, so we shouldn't apply any effects for entities that already exist in the world.
            return;
        }

        final Level level = event.getWorld();

        Entity entity = event.getEntity();
        if (entity instanceof ItemEntity itemEntity && !level.isClientSide && TFCConfig.SERVER.coolHotItemEntities.get())
        {
            final ItemStack item = itemEntity.getItem();
            item.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> {
                if (cap.getTemperature() > 0f)
                {
                    itemEntity.lifespan = TFCConfig.SERVER.ticksBeforeItemCool.get();
                }
            });
        }
        else if (entity instanceof LightningBolt lightning && !level.isClientSide && !event.isCanceled())
        {
            if (!TFCConfig.SERVER.enableLightning.get())
            {
                event.setCanceled(true);
                return;
            }
            if (TFCConfig.SERVER.enableLightningStrippingLogs.get() && level.random.nextFloat() < 0.2f)
            {
                final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
                BlockPos pos = lightning.blockPosition();
                for (int x = -5; x <= 5; x++)
                {
                    for (int y = -5; y <= 5; y++)
                    {
                        for (int z = -5; z <= 5; z++)
                        {
                            if (level.random.nextInt(3) == 0 && x * x + y * y + z * z <= 25)
                            {
                                mutable.setWithOffset(pos, x, y, z);
                                BlockState state = level.getBlockState(mutable);
                                BlockState modified = state.getToolModifiedState(new UseOnContext(level, null, InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_AXE), new BlockHitResult(Vec3.atBottomCenterOf(mutable), Direction.DOWN, mutable, false)), ToolActions.AXE_STRIP, true);
                                if (modified != null)
                                {
                                    level.setBlockAndUpdate(mutable, modified);
                                }
                            }
                        }
                    }
                }
            }

        }
        if (entity instanceof Monster monster && !TFCConfig.SERVER.enableVanillaMobsSpawningWithVanillaEquipment.get())
        {
            if (Helpers.isItem(monster.getItemInHand(InteractionHand.MAIN_HAND), TFCTags.Items.DISABLED_MONSTER_HELD_ITEMS))
            {
                monster.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
            if (Helpers.isItem(monster.getItemInHand(InteractionHand.OFF_HAND), TFCTags.Items.DISABLED_MONSTER_HELD_ITEMS))
            {
                monster.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
        }

        if (!TFCConfig.SERVER.enableChickenJockies.get())
        {
            // Need to prevent both the chicken and the jockey from spawning
            if ((entity instanceof Chicken chicken && chicken.isChickenJockey)
                || (entity.getVehicle() != null && entity.getVehicle() instanceof Chicken vehicleChicken && vehicleChicken.isChickenJockey))
            {
                event.setCanceled(true);
            }
        }

        if (entity.getType() == EntityType.SKELETON)
        {
            entity.setItemSlot(EquipmentSlot.MAINHAND, Helpers.getRandomElement(ForgeRegistries.ITEMS, TFCTags.Items.SKELETON_WEAPONS, entity.level.getRandom()).orElse(Items.BOW).getDefaultInstance());
        }
        else if (entity.getType() == EntityType.SKELETON_HORSE && !TFCConfig.SERVER.enableVanillaSkeletonHorseSpawning.get())
        {
            event.setCanceled(true);
        }
        else if ((entity instanceof IronGolem || entity instanceof SnowGolem) && !TFCConfig.SERVER.enableVanillaGolems.get())
        {
            event.setCanceled(true);
        }
    }

    /**
     * If the item is heated, we check for blocks below and within that would cause it to cool.
     * Since we don't want the item to actually expire, we set the expiry time to a small number that allows us to revisit the same code soon.
     * <p>
     * By cancelling the event, we guarantee that the item will not actually expire.
     */
    public static void onItemExpire(ItemExpireEvent event)
    {
        if (!TFCConfig.SERVER.coolHotItemEntities.get()) return;
        final ItemEntity entity = event.getEntityItem();
        final ServerLevel level = (ServerLevel) entity.getLevel();
        final ItemStack stack = entity.getItem();
        final BlockPos pos = entity.blockPosition();

        stack.getCapability(HeatCapability.CAPABILITY).ifPresent(heat -> {
            final int lifespan = stack.getItem().getEntityLifespan(stack, level);
            if (entity.lifespan >= lifespan)
                return; // the case where the item has been sitting out for longer than the lifespan. So it should be removed by the game.

            final float itemTemp = heat.getTemperature();
            if (itemTemp > 0f)
            {
                float coolAmount = 0;
                final BlockState state = level.getBlockState(pos);
                final FluidState fluid = level.getFluidState(pos);
                if (Helpers.isFluid(fluid, FluidTags.WATER))
                {
                    coolAmount = 50f;
                    if (level.random.nextFloat() < 0.001F && state.getBlock() == Blocks.WATER)
                    {
                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
                }
                else if (Helpers.isBlock(state, Blocks.SNOW))
                {
                    coolAmount = 70f;
                    if (level.random.nextFloat() < 0.1F)
                    {
                        final int layers = state.getValue(SnowLayerBlock.LAYERS);
                        if (layers > 1)
                        {
                            level.destroyBlock(pos, false);
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
                    if (Helpers.isBlock(belowState, Blocks.SNOW_BLOCK))
                    {
                        coolAmount = 75f;
                        if (level.random.nextFloat() < 0.1F)
                        {
                            level.setBlockAndUpdate(belowPos, Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 7));
                        }
                    }
                    else if (LegacyMaterials.isMeltyIce(belowState))
                    {
                        coolAmount = 100f;
                        if (level.random.nextFloat() < 0.01F)
                        {
                            level.setBlockAndUpdate(belowPos, Helpers.isBlock(belowState, TFCBlocks.SEA_ICE.get()) ? TFCBlocks.SALT_WATER.get().defaultBlockState() : Blocks.WATER.defaultBlockState());
                        }
                    }
                    else if (LegacyMaterials.isSolidIce(belowState))
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
                    Helpers.playSound(level, pos, TFCSounds.ITEM_COOL.get());
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
        onNewPlayerInWorld(event.getPlayer());
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        onNewPlayerInWorld(event.getPlayer());
    }

    public static void onPlayerDeath(PlayerEvent.Clone event)
    {
        // This event fires before respawn event, and allows us to copy nutrition to the new player.
        // Respawn event will handle syncing to client, as the network connection is setup by then.
        if (TFCConfig.SERVER.keepNutritionAfterDeath.get() && event.isWasDeath())
        {
            TFCFoodData.restoreFoodStatsAfterDeath(event.getOriginal(), event.getPlayer());
        }
    }

    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        onNewPlayerInWorld(event.getPlayer());
    }

    /**
     * Common handling for creating new player entities. Called through logging in, changing dimension, and respawning.
     */
    private static void onNewPlayerInWorld(Player player)
    {
        if (player instanceof ServerPlayer serverPlayer)
        {
            TFCFoodData.replaceFoodStats(serverPlayer);

            serverPlayer.level.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(c -> c.syncTo(serverPlayer));
            serverPlayer.getCapability(PlayerDataCapability.CAPABILITY).ifPresent(PlayerData::sync);

            final ClimateModel model = Climate.model(serverPlayer.level);
            PacketHandler.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new UpdateClimateModelPacket(model));
        }
    }

    public static void onServerChat(ServerChatEvent event)
    {
        // Apply intoxication after six hours
        final long intoxicatedTicks = event.getPlayer().getCapability(PlayerDataCapability.CAPABILITY).map(p -> p.getIntoxicatedTicks(event.getPlayer().getLevel().isClientSide()) - 6 * ICalendar.TICKS_IN_HOUR).orElse(0L);
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
            event.setComponent(Helpers.translatable("<" + event.getUsername() + "> " + String.join(" ", words)));
        }
    }

    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        final Level level = event.getWorld();
        final BlockState state = level.getBlockState(event.getPos());
        final ItemStack stack = event.getItemStack();

        if (Helpers.isItem(stack, Items.WRITABLE_BOOK) || Helpers.isItem(stack, Items.WRITTEN_BOOK))
        {
            // Lecterns, we only do a modification for known items *and* known blocks, so there's no need to simulate any other interaction
            if (state.getBlock() instanceof TFCLecternBlock && LecternBlock.tryPlaceBook(event.getPlayer(), level, event.getPos(), state, stack))
            {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }

        // need position access to set smelled pos properly, so we cannot use container menus here.
        if (level.getBlockEntity(event.getPos()) instanceof BaseContainerBlockEntity container && container.canOpen(event.getPlayer()) && container instanceof PestContainer test && test.canBeInfested())
        {
            int infestation = 0;
            for (int i = 0; i < container.getContainerSize(); i++)
            {
                if (Helpers.isItem(container.getItem(i), TFCTags.Items.FOODS))
                {
                    infestation++;
                    if (infestation == 5)
                    {
                        break;
                    }
                }
            }
            Helpers.tickInfestation(level, container.getBlockPos(), infestation, event.getPlayer());
        }
    }

    public static void onPlayerRightClickBlockLowestPriority(PlayerInteractEvent.RightClickBlock event)
    {
        final Level level = event.getWorld();
        final BlockState state = level.getBlockState(event.getPos());
        final ItemStack stack = event.getItemStack();

        if (!event.isCanceled() && event.getHand() == InteractionHand.MAIN_HAND && stack.isEmpty())
        {
            // For drinking, when we have an empty hand, we want to first try and interact with a block.
            // We can't use interaction manager, as vanilla won't try and call onItemUse for empty stacks.
            // We do this on lowest priority, because we want other modifications to fire *first* - for instance, if a mod does a block interaction on this event, at normal priority
            // Thus if we get here, we're fairly certain another mod doesn't need to use this, so we can check the block `use()` method, and then if no, we can attempt drinking.
            // Possible issues:
            // - Right-click a chest underwater -> it should open the chest, not drink
            // - Try and remove the filter from a Create 'Basin', by right-clicking with an empty hand (create cancels this event)
            final InteractionResult useBlockResult = state.use(level, event.getPlayer(), event.getHand(), event.getHitVec());
            if (useBlockResult.consumesAction())
            {
                if (event.getPlayer() instanceof ServerPlayer serverPlayer)
                {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, event.getPos(), stack);
                }
                event.setCanceled(true);
                event.setCancellationResult(useBlockResult);
            }
            else
            {
                // If we haven't already interacted with a block, then we can attempt drinking.
                final InteractionResult result = Drinkable.attemptDrink(level, event.getPlayer(), true);
                if (result != InteractionResult.PASS)
                {
                    event.setCanceled(true);
                    event.setCancellationResult(result);
                }
            }
        }

        // Some blocks have interactions that respect sneaking, both with items in hand and not
        // These need to be able to interact, regardless of if an item has sneakBypassesUse set
        // So, we have to explicitly allow the Block.use() interaction for these blocks.
        //
        // This happens at lowest priority, regardless if the event was cancelled, as we don't want this `ALLOW` to be overwritten.
        // Otherwise it breaks anvil shift interactions, see: TerraFirmaCraft#2254
        if (state.getBlock() instanceof AnvilBlock || state.getBlock() instanceof RockAnvilBlock || Fertilizer.get(stack) != null || (state.getBlock() instanceof BarrelBlock && !state.getValue(BarrelBlock.RACK) && state.getValue(BarrelBlock.FACING).getAxis().isHorizontal() && stack.getItem() == TFCItems.BARREL_RACK.get()))
        {
            event.setUseBlock(Event.Result.ALLOW);
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

    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event)
    {
        final IFood food = event.getItem().getCapability(FoodCapability.CAPABILITY).resolve().orElse(null);
        if (food instanceof DynamicBowlFood.DynamicBowlHandler)
        {
            event.setResultStack(DynamicBowlFood.DynamicBowlHandler.onItemUse(event.getItem(), event.getResultStack(), event.getEntityLiving()));
        }
    }

    public static void addReloadListeners(AddReloadListenerEvent event)
    {
        // Alloy recipes are loaded as part of recipes, but have a hard dependency on metals.
        // So, we hack internal resource lists in order to stick metals before recipes.
        // see ReloadableServerResourcesMixin

        // All other resource reload listeners can be inserted after recipes.
        event.addListener(Fuel.MANAGER);
        event.addListener(Drinkable.MANAGER);
        event.addListener(Support.MANAGER);
        event.addListener(Pannable.MANAGER);
        event.addListener(Sluiceable.MANAGER);
        event.addListener(LampFuel.MANAGER);
        event.addListener(Fertilizer.MANAGER);
        event.addListener(ItemSizeManager.MANAGER);
        event.addListener(ClimateRange.MANAGER);
        event.addListener(Fauna.MANAGER);
        event.addListener(HeatCapability.MANAGER);
        event.addListener(FoodCapability.MANAGER);
        event.addListener(EntityDamageResistance.MANAGER);
        event.addListener(ItemDamageResistance.MANAGER);

        // In addition, we capture the recipe manager here
        Helpers.setCachedRecipeManager(event.getServerResources().getRecipeManager());
    }

    public static void onDataPackSync(OnDatapackSyncEvent event)
    {
        // Sync managers
        final ServerPlayer player = event.getPlayer();
        final PacketDistributor.PacketTarget target = player == null ? PacketDistributor.ALL.noArg() : PacketDistributor.PLAYER.with(() -> player);

        PacketHandler.send(target, Metal.MANAGER.createSyncPacket());
        PacketHandler.send(target, Fuel.MANAGER.createSyncPacket());
        PacketHandler.send(target, Fertilizer.MANAGER.createSyncPacket());
        PacketHandler.send(target, ItemDamageResistance.MANAGER.createSyncPacket());
        PacketHandler.send(target, HeatCapability.MANAGER.createSyncPacket());
        PacketHandler.send(target, FoodCapability.MANAGER.createSyncPacket());
        PacketHandler.send(target, ItemSizeManager.MANAGER.createSyncPacket());
        PacketHandler.send(target, ClimateRange.MANAGER.createSyncPacket());
        PacketHandler.send(target, Drinkable.MANAGER.createSyncPacket());
        PacketHandler.send(target, LampFuel.MANAGER.createSyncPacket());
        PacketHandler.send(target, Pannable.MANAGER.createSyncPacket());
        PacketHandler.send(target, Sluiceable.MANAGER.createSyncPacket());
        PacketHandler.send(target, Support.MANAGER.createSyncPacket());
    }

    /**
     * This is when tags are safe to be loaded, so we can do post reload actions that involve querying ingredients.
     * It is fired on both logical server and client after resources are reloaded (or, sent from server).
     * In addition, during the first load on a server in {@link net.minecraft.server.Main}, where {@link net.minecraft.server.WorldStem#load(WorldStem.InitConfig, WorldStem.DataPackConfigSupplier, WorldStem.WorldDataSupplier, Executor, Executor)} is invoked, the server won't exist yet at all.
     * In that case, we need to rely on the fact that {@link AddReloadListenerEvent} will be fired before that point, and we can capture the server's recipe manager there.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void onTagsUpdated(TagsUpdatedEvent event)
    {
        // First, reload all caches
        final RecipeManager manager = Helpers.getUnsafeRecipeManager();
        IndirectHashCollection.reloadAllCaches(manager);

        // Then apply post reload actions which may query the cache
        Support.updateMaximumSupportRange();
        Metal.updateMetalFluidMap();
        ItemSizeManager.applyItemStackSizeOverrides();
        FoodCapability.markRecipeOutputsAsNonDecaying(manager);

        if (TFCConfig.COMMON.enableDatapackTests.get())
        {
            SelfTests.validateDatapacks(manager);
        }

        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED)
        {
            ClientHelpers.updateSearchTrees();
        }

        final RecipeManagerAccessor accessor = (RecipeManagerAccessor) manager;
        for (RecipeType<?> type : Registry.RECIPE_TYPE)
        {
            LOGGER.info("Loaded {} recipes of type {}", accessor.invoke$byType((RecipeType) type).size(), Registry.RECIPE_TYPE.getKey(type));
        }
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

    public static void onSelectClimateModel(SelectClimateModelEvent event)
    {
        final ServerLevel level = event.level();
        if (event.level().dimension() == Level.OVERWORLD && level.getChunkSource().getGenerator() instanceof ChunkGeneratorExtension)
        {
            // TFC decides to select the climate model for the overworld, if we're using a TFC enabled chunk generator
            event.setModel(new OverworldClimateModel());
        }
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        final Player player = event.getPlayer();
        if (event.getTarget().getType() == EntityType.MINECART && event.getTarget() instanceof Minecart oldCart && player.isShiftKeyDown() && player.isSecondaryUseActive())
        {
            ItemStack held = player.getItemInHand(event.getHand());
            if (held.getItem() instanceof BlockItem bi && Helpers.isBlock(bi.getBlock(), TFCTags.Blocks.MINECART_HOLDABLE))
            {
                final ItemStack holdingItem = held.split(1);
                if (!player.level.isClientSide)
                {
                    final HoldingMinecart minecart = new HoldingMinecart(player.level, oldCart.getX(), oldCart.getY(), oldCart.getZ());
                    HoldingMinecart.copyMinecart(oldCart, minecart);
                    minecart.setHoldItem(holdingItem);
                    oldCart.discard();
                    player.level.addFreshEntity(minecart);
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    public static void onMount(EntityMountEvent event)
    {
        if (event.getEntityBeingMounted() instanceof Boat && event.getEntityMounting() instanceof Predator)
        {
            event.setCanceled(true);
        }
    }

    public static void onAnimalTame(AnimalTameEvent event)
    {
        if (Helpers.isEntity(event.getEntity(), TFCTags.Entities.HORSES))
        {
            event.setCanceled(true); // cancel vanilla taming methods
        }
    }

    public static void onContainerOpen(PlayerContainerEvent.Open event)
    {
        if (event.getContainer() instanceof BlockEntityContainer<?> container && event.getContainer() instanceof PestContainer test && test.canBeInfested())
        {
            final Player player = event.getPlayer();
            final Level level = player.level;
            if (level.isClientSide) return;
            int amount = 0;
            if (TFCConfig.SERVER.enableInfestations.get())
            {
                for (Slot slot : container.slots)
                {
                    if (container.typeOf(slot.index) == Container.IndexType.CONTAINER && Helpers.isItem(slot.getItem(), TFCTags.Items.FOODS))
                    {
                        amount++;
                        if (amount == 5)
                        {
                            break;
                        }
                    }
                }
            }
            Helpers.tickInfestation(level, container.getBlockEntity().getBlockPos(), amount, player);
        }
    }

    public static void onCropsGrow(BlockEvent.CropGrowEvent.Pre event)
    {
        final BlockState state = event.getState();
        final LevelAccessor level = event.getWorld();
        if (state.getBlock() instanceof BambooBlock)
        {
            if (level instanceof ServerLevel server && server.random.nextFloat() > TFCConfig.SERVER.plantLongGrowthChance.get())
            {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}