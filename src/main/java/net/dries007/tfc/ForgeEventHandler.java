/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.world.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.DeadWallTorchBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.TFCWallTorchBlock;
import net.dries007.tfc.common.blocks.devices.BurningLogPileBlock;
import net.dries007.tfc.common.blocks.devices.CharcoalForgeBlock;
import net.dries007.tfc.common.blocks.devices.PitKilnBlock;
import net.dries007.tfc.common.capabilities.forge.ForgingCapability;
import net.dries007.tfc.common.capabilities.forge.ForgingHandler;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.HeatDefinition;
import net.dries007.tfc.common.capabilities.heat.HeatManager;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.command.TFCCommands;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.common.tileentity.AbstractFirepitTileEntity;
import net.dries007.tfc.common.tileentity.CharcoalForgeTileEntity;
import net.dries007.tfc.common.tileentity.PitKilnTileEntity;
import net.dries007.tfc.common.tileentity.TickCounterTileEntity;
import net.dries007.tfc.common.types.*;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.ChunkUnwatchPacket;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.util.CacheInvalidationListener;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.events.StartFireEvent;
import net.dries007.tfc.util.support.SupportManager;
import net.dries007.tfc.util.tracker.WorldTracker;
import net.dries007.tfc.util.tracker.WorldTrackerCapability;
import net.dries007.tfc.world.biome.ITFCBiomeProvider;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCache;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;
import net.dries007.tfc.world.chunkdata.ITFCChunkGenerator;

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
        bus.addListener(ForgeEventHandler::onArrowImpact);
        bus.addListener(ForgeEventHandler::onFireballImpact);
    }

    /**
     * Duplicates logic from {@link net.minecraft.server.MinecraftServer#setInitialSpawn(ServerWorld, IServerWorldInfo, boolean, boolean, boolean)} as that version only asks the dimension for the sea level...
     */
    public static void onCreateWorldSpawn(WorldEvent.CreateSpawnPosition event)
    {
        // Forge why you make everything `IWorld`, it's literally only called from `ServerWorld`...
        if (event.getWorld() instanceof ServerLevel)
        {
            final ServerLevel world = (ServerLevel) event.getWorld();
            final ServerLevelData settings = event.getSettings();
            final ChunkGenerator generator = world.getChunkSource().getGenerator();
            if (generator instanceof ITFCChunkGenerator)
            {
                final ITFCBiomeProvider biomeProvider = ((ITFCChunkGenerator) generator).getBiomeSource();
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

                settings.setSpawn(chunkPos.getWorldPosition().offset(8, generator.getSpawnHeight(), 8), 0.0F);
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
    }

    public static void attachChunkCapabilities(AttachCapabilitiesEvent<LevelChunk> event)
    {
        if (!event.getObject().isEmpty())
        {
            Level world = event.getObject().getLevel();
            ChunkPos chunkPos = event.getObject().getPos();
            ChunkData data;
            if (!Helpers.isClientSide(world))
            {
                // Chunk was created on server thread.
                // 1. If this was due to world gen, it won't have any cap data. This is where we clear the world gen cache and attach it to the chunk
                // 2. If this was due to chunk loading, the caps will be deserialized from NBT after this event is posted. Attach empty data here
                data = ChunkDataCache.WORLD_GEN.remove(chunkPos);
                if (data == null)
                {
                    data = new ChunkData(chunkPos);
                }
            }
            else
            {
                // This may happen before or after the chunk is watched and synced to client
                // Default to using the cache. If later the sync packet arrives it will update the same instance in the chunk capability and cache
                data = ChunkDataCache.CLIENT.getOrCreate(chunkPos);
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
            HeatDefinition def = HeatManager.get(stack);
            if (def != null)
            {
                event.addCapability(HeatCapability.KEY, def.create());
            }
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
        if (event.getChunk().getStatus().getChunkType() == ChunkStatus.ChunkType.PROTOCHUNK && event.getData().contains("tfc_protochunk_data", Constants.NBT.TAG_COMPOUND))
        {
            final ChunkPos pos = event.getChunk().getPos();
            final ChunkData data = ChunkDataCache.WORLD_GEN.getOrCreate(pos);
            data.deserializeNBT(event.getData().getCompound("tfc_protochunk_data"));
        }
    }

    public static void addReloadListeners(AddReloadListenerEvent event)
    {
        // Resource reload listeners
        ReloadableResourceManager resourceManager = (ReloadableResourceManager) event.getDataPackRegistries().getResourceManager();
        resourceManager.registerReloadListener(RockManager.INSTANCE);
        resourceManager.registerReloadListener(MetalManager.INSTANCE);
        resourceManager.registerReloadListener(MetalItemManager.INSTANCE);
        resourceManager.registerReloadListener(FuelManager.INSTANCE);
        resourceManager.registerReloadListener(SupportManager.INSTANCE);
        resourceManager.registerReloadListener(HeatManager.INSTANCE);
        resourceManager.registerReloadListener(ItemSizeManager.INSTANCE);

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
        if (event.getWorld() instanceof ServerLevel)
        {
            final ServerLevel world = (ServerLevel) event.getWorld();
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
        if (event.getWorld() instanceof ServerLevel)
        {
            final ServerLevel world = (ServerLevel) event.getWorld();
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
        if (event.getWorld() instanceof ServerLevel && ((ServerLevel) event.getWorld()).dimension() == Level.OVERWORLD)
        {
            final ServerLevel world = (ServerLevel) event.getWorld();
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
            event.setNewState(TFCBlocks.ROCK_BLOCKS.get(Rock.Default.GABBRO).get(Rock.BlockType.HARDENED).get().defaultBlockState());
        }
        else if (originalBlock == Blocks.COBBLESTONE)
        {
            event.setNewState(TFCBlocks.ROCK_BLOCKS.get(Rock.Default.RHYOLITE).get(Rock.BlockType.HARDENED).get().defaultBlockState());
        }
        else if (originalBlock == Blocks.BASALT)
        {
            event.setNewState(TFCBlocks.ROCK_BLOCKS.get(Rock.Default.BASALT).get(Rock.BlockType.HARDENED).get().defaultBlockState());
        }
    }

    public static void onFireStart(StartFireEvent event)
    {
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        Block block = state.getBlock();

        if (block.is(TFCBlocks.FIREPIT.get()) || block.is(TFCBlocks.POT.get()) || block.is(TFCBlocks.GRILL.get()))
        {
            AbstractFirepitTileEntity<?> firepit = Helpers.getTileEntity(world, pos, AbstractFirepitTileEntity.class);
            if (firepit != null)
            {
                firepit.light(state);
            }
            event.setCanceled(true);
        }
        else if (block.is(TFCBlocks.TORCH.get()) || block.is(TFCBlocks.WALL_TORCH.get()))
        {
            TickCounterTileEntity te = Helpers.getTileEntity(world, pos, TickCounterTileEntity.class);
            if (te != null)
            {
                te.resetCounter();
            }
            event.setCanceled(true);
        }
        else if (block.is(TFCBlocks.DEAD_TORCH.get()))
        {
            world.setBlockAndUpdate(pos, TFCBlocks.TORCH.get().defaultBlockState());
            event.setCanceled(true);
        }
        else if (block.is(TFCBlocks.DEAD_WALL_TORCH.get()))
        {
            Direction direction = state.getValue(DeadWallTorchBlock.FACING);
            world.setBlockAndUpdate(pos, TFCBlocks.WALL_TORCH.get().defaultBlockState().setValue(TFCWallTorchBlock.FACING, direction));
            event.setCanceled(true);
        }
        else if (block.is(TFCBlocks.LOG_PILE.get()))
        {
            BurningLogPileBlock.tryLightLogPile(world, pos);
            event.setCanceled(true);
        }
        else if (block.is(TFCBlocks.PIT_KILN.get()) && state.getValue(PitKilnBlock.STAGE) == 15)
        {
            PitKilnTileEntity kiln = Helpers.getTileEntity(world, pos, PitKilnTileEntity.class);
            if (kiln != null)
            {
                kiln.tryLight();
            }
        }
        else if (block.is(TFCBlocks.CHARCOAL_PILE.get()) && state.getValue(CharcoalPileBlock.LAYERS) >= 7 && CharcoalForgeBlock.isValid(world, pos))
        {
            world.setBlockAndUpdate(pos, TFCBlocks.CHARCOAL_FORGE.get().defaultBlockState().setValue(HEAT, 2));
            CharcoalForgeTileEntity forge = Helpers.getTileEntity(world, pos, CharcoalForgeTileEntity.class);
            if (forge != null)
            {
                forge.onCreate();
            }
        }
        else if (block.is(TFCBlocks.CHARCOAL_FORGE.get()) && CharcoalForgeBlock.isValid(world, pos))
        {
            world.setBlockAndUpdate(pos, state.setValue(HEAT, 2));
        }
    }

    public static void onArrowImpact(ProjectileImpactEvent.Arrow event)
    {
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
    }

    public static void onFireballImpact(ProjectileImpactEvent.Fireball event)
    {
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
    }
}