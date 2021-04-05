/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.world.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.*;
import net.dries007.tfc.common.blocks.devices.BurningLogPileBlock;
import net.dries007.tfc.common.blocks.devices.PitKilnBlock;
import net.dries007.tfc.common.capabilities.forge.ForgingCapability;
import net.dries007.tfc.common.capabilities.forge.ForgingHandler;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.HeatDefinition;
import net.dries007.tfc.common.capabilities.heat.HeatManager;
import net.dries007.tfc.common.command.TFCCommands;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.CollapseRecipe;
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

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeEventHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Duplicates logic from {@link net.minecraft.server.MinecraftServer#setInitialSpawn(ServerWorld, IServerWorldInfo, boolean, boolean, boolean)} as that version only asks the dimension for the sea level...
     */
    @SubscribeEvent
    public static void onCreateWorldSpawn(WorldEvent.CreateSpawnPosition event)
    {
        // Forge why you make everything `IWorld`, it's literally only called from `ServerWorld`...
        if (event.getWorld() instanceof ServerWorld)
        {
            final ServerWorld world = (ServerWorld) event.getWorld();
            final IServerWorldInfo settings = event.getSettings();
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

    @SubscribeEvent
    public static void onAttachCapabilitiesChunk(AttachCapabilitiesEvent<Chunk> event)
    {
        if (!event.getObject().isEmpty())
        {
            World world = event.getObject().getLevel();
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

    @SubscribeEvent
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

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event)
    {
        if (!Helpers.isClientSide(event.getWorld()) && !(event.getChunk() instanceof EmptyChunk))
        {
            ChunkPos pos = event.getChunk().getPos();
            ChunkData.getCapability(event.getChunk()).ifPresent(data -> {
                ChunkDataCache.SERVER.update(pos, data);
                ChunkDataCache.WATCH_QUEUE.dequeueLoadedChunk(pos, data);
            });
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event)
    {
        // Clear server side chunk data cache
        if (!Helpers.isClientSide(event.getWorld()) && !(event.getChunk() instanceof EmptyChunk))
        {
            ChunkDataCache.SERVER.remove(event.getChunk().getPos());
        }
    }

    @SubscribeEvent
    public static void onChunkUnwatch(ChunkWatchEvent.UnWatch event)
    {
        // Send an update packet to the client when un-watching the chunk
        ChunkPos pos = event.getPos();
        PacketHandler.send(PacketDistributor.PLAYER.with(event::getPlayer), new ChunkUnwatchPacket(pos));
        ChunkDataCache.WATCH_QUEUE.dequeueChunk(pos, event.getPlayer());
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesWorld(AttachCapabilitiesEvent<World> event)
    {
        event.addCapability(WorldTrackerCapability.KEY, new WorldTracker());
    }

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event)
    {
        // Resource reload listeners
        IReloadableResourceManager resourceManager = (IReloadableResourceManager) event.getDataPackRegistries().getResourceManager();
        resourceManager.registerReloadListener(RockManager.INSTANCE);
        resourceManager.registerReloadListener(MetalManager.INSTANCE);
        resourceManager.registerReloadListener(MetalItemManager.INSTANCE);
        resourceManager.registerReloadListener(FuelManager.INSTANCE);
        resourceManager.registerReloadListener(SupportManager.INSTANCE);
        resourceManager.registerReloadListener(HeatManager.INSTANCE);

        resourceManager.registerReloadListener(CacheInvalidationListener.INSTANCE);
    }

    @SubscribeEvent
    public static void beforeServerStart(FMLServerAboutToStartEvent event)
    {
        CacheInvalidationListener.INSTANCE.invalidateAll();
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event)
    {
        LOGGER.debug("Registering TFC Commands");
        TFCCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent event)
    {
        CacheInvalidationListener.INSTANCE.invalidateAll();
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event)
    {
        // Check for possible collapse
        IWorld world = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);

        if (TFCTags.Blocks.CAN_TRIGGER_COLLAPSE.contains(state.getBlock()) && world instanceof World)
        {
            CollapseRecipe.tryTriggerCollapse((World) world, pos);
        }
    }

    @SubscribeEvent
    public static void onNeighborUpdate(BlockEvent.NeighborNotifyEvent event)
    {
        if (event.getWorld() instanceof ServerWorld)
        {
            final ServerWorld world = (ServerWorld) event.getWorld();
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

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event)
    {
        if (event.getWorld() instanceof ServerWorld)
        {
            final ServerWorld world = (ServerWorld) event.getWorld();
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

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            event.world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.tick(event.world));
        }
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event)
    {
        if (!event.getWorld().isClientSide)
        {
            event.getWorld().getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addCollapsePositions(new BlockPos(event.getExplosion().getPosition()), event.getAffectedBlocks()));
        }
    }

    @SubscribeEvent
    public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event)
    {
        ItemStack stack = event.getObject();
        if (!stack.isEmpty())
        {
            // Every item has a forging capability
            event.addCapability(ForgingCapability.KEY, new ForgingHandler(stack));

            // Attach heat capability to the ones defined by data packs
            HeatDefinition def = HeatManager.get(stack);
            if (def != null)
            {
                event.addCapability(HeatCapability.KEY, def.create());
            }
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event)
    {
        if (event.getWorld() instanceof ServerWorld && ((ServerWorld) event.getWorld()).dimension() == World.OVERWORLD)
        {
            final ServerWorld world = (ServerWorld) event.getWorld();
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

    @SubscribeEvent
    public static void onCreateNetherPortal(BlockEvent.PortalSpawnEvent event)
    {
        if (!TFCConfig.SERVER.enableNetherPortals.get())
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
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

    @SubscribeEvent
    public static void onFireStart(StartFireEvent event)
    {
        World world = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        Block block = state.getBlock();

        if (!world.isClientSide())
        {
            if (block.is(TFCBlocks.FIREPIT.get()) || block.is(TFCBlocks.POT.get()) || block.is(TFCBlocks.GRILL.get()))
            {
                world.setBlock(pos, state.setValue(TFCBlockStateProperties.LIT, true), 2);
                event.setCanceled(true);
            }
            else if (block.is(TFCBlocks.TORCH.get()) || block.is(TFCBlocks.WALL_TORCH.get()))
            {
                TickCounterTileEntity te = Helpers.getTileEntity(world, pos, TickCounterTileEntity.class);
                if (te != null)
                    te.resetCounter();
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
                    kiln.tryLight();
            }

            ItemStack item = event.getItemStack();
            if (item != null && item.getItem() == TFCItems.TORCH.get())
            {
                event.setCanceled(true); // so torches don't start fires
            }
        }

        if (!event.isCanceled() && AbstractFireBlock.canBePlacedAt(world, pos, event.getTargetedFace()))
            world.setBlock(pos, AbstractFireBlock.getState(world, pos), 11);
    }

    @SubscribeEvent
    public static void onArrowHit(ProjectileImpactEvent.Arrow event)
    {
        if (!TFCConfig.SERVER.enableFireArrowSpreading.get()) return;
        AbstractArrowEntity arrow = event.getArrow();
        RayTraceResult result = event.getRayTraceResult();
        if (result.getType() == RayTraceResult.Type.BLOCK && arrow.isOnFire())
        {
            BlockRayTraceResult blockResult = (BlockRayTraceResult) result;
            BlockPos pos = blockResult.getBlockPos();
            World world = arrow.level;
            StartFireEvent.startFire(world, pos, world.getBlockState(pos), blockResult.getDirection(), null, ItemStack.EMPTY);
        }
    }

    @SubscribeEvent
    public static void onArrowHit(ProjectileImpactEvent.Fireball event)
    {
        if (!TFCConfig.SERVER.enableFireArrowSpreading.get()) return;
        DamagingProjectileEntity fireball = event.getFireball();
        RayTraceResult result = event.getRayTraceResult();
        if (result.getType() == RayTraceResult.Type.BLOCK)
        {
            BlockRayTraceResult blockResult = (BlockRayTraceResult) result;
            BlockPos pos = blockResult.getBlockPos();
            World world = fireball.level;
            StartFireEvent.startFire(world, pos, world.getBlockState(pos), blockResult.getDirection(), null, ItemStack.EMPTY);
        }
    }
}