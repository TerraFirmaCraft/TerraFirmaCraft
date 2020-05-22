/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import com.mojang.brigadier.CommandDispatcher;
import net.dries007.tfc.command.ClearWorldCommand;
import net.dries007.tfc.network.ChunkDataRequestPacket;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.objects.TFCTags;
import net.dries007.tfc.objects.recipes.CollapseRecipe;
import net.dries007.tfc.objects.recipes.LandslideRecipe;
import net.dries007.tfc.objects.types.MetalManager;
import net.dries007.tfc.objects.types.RockManager;
import net.dries007.tfc.util.TFCReloadListener;
import net.dries007.tfc.util.support.SupportManager;
import net.dries007.tfc.world.TFCWorldType;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.tracker.WorldTracker;
import net.dries007.tfc.world.tracker.WorldTrackerCapability;
import net.dries007.tfc.world.vein.VeinTypeManager;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeEventHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Duplicates logic from {@link ServerWorld#createSpawnPosition(WorldSettings)} as that version only asks the dimension for the sea level...
     */
    @SubscribeEvent
    public static void onCreateWorldSpawn(WorldEvent.CreateSpawnPosition event)
    {
        // Forge why you make everything `IWorld`, it's literally only called from `ServerWorld`...
        if (event.getWorld() instanceof ServerWorld && ((World) event.getWorld()).getWorldType() == TFCWorldType.INSTANCE)
        {
            ServerWorld world = (ServerWorld) event.getWorld();
            event.setCanceled(true);

            BiomeProvider biomeProvider = world.getChunkProvider().getChunkGenerator().getBiomeProvider();
            Random random = new Random(world.getSeed());
            BlockPos pos = biomeProvider.func_225531_a_(0, world.getChunkProvider().getChunkGenerator().getSeaLevel(), 0, 256, biomeProvider.getBiomesToSpawnIn(), random);
            ChunkPos chunkPos = pos == null ? new ChunkPos(0, 0) : new ChunkPos(pos);
            if (pos == null)
            {
                LOGGER.warn("Unable to find spawn biome");
            }

            boolean flag = false;
            for (Block block : BlockTags.VALID_SPAWN.getAllElements())
            {
                if (biomeProvider.getSurfaceBlocks().contains(block.getDefaultState()))
                {
                    flag = true;
                    break;
                }
            }
            // Initial guess at spawn position
            world.getWorldInfo().setSpawn(chunkPos.asBlockPos().add(8, world.getChunkProvider().getChunkGenerator().getGroundHeight(), 8));
            int x = 0;
            int z = 0;
            int xStep = 0;
            int zStep = -1;
            // Step around until we find a valid spawn position / chunk
            for (int tries = 0; tries < 1024; ++tries)
            {
                if (x > -16 && x <= 16 && z > -16 && z <= 16)
                {
                    BlockPos spawnPos = world.dimension.findSpawn(new ChunkPos(chunkPos.x + x, chunkPos.z + z), flag);
                    if (spawnPos != null)
                    {
                        world.getWorldInfo().setSpawn(spawnPos);
                        break;
                    }
                }

                if (x == z || x < 0 && x == -z || x > 0 && x == 1 - z)
                {
                    int temp = xStep;
                    xStep = -zStep;
                    zStep = temp;
                }

                x += xStep;
                z += zStep;
            }

            // Don't create bonus chest
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesChunk(AttachCapabilitiesEvent<Chunk> event)
    {
        World world = event.getObject().getWorld();
        if (world.getWorldType() == TFCWorldType.INSTANCE)
        {
            // Add the rock data to the chunk capability, for long term storage
            ChunkData data = ChunkDataProvider.get(world)
                .map(provider -> provider.remove(event.getObject().getPos()))
                .orElseGet(ChunkData::new);
            event.addCapability(ChunkDataCapability.KEY, data);
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesWorld(AttachCapabilitiesEvent<World> event)
    {
        event.addCapability(WorldTrackerCapability.KEY, new WorldTracker());
    }

    @SubscribeEvent
    public static void beforeServerStart(FMLServerAboutToStartEvent event)
    {
        LOGGER.debug("Before Server Start");

        // Initializes json data listeners
        IReloadableResourceManager resourceManager = event.getServer().getResourceManager();
        resourceManager.addReloadListener(RockManager.INSTANCE);
        resourceManager.addReloadListener(MetalManager.INSTANCE);
        resourceManager.addReloadListener(VeinTypeManager.INSTANCE);
        resourceManager.addReloadListener(SupportManager.INSTANCE);

        // generic reload listener
        resourceManager.addReloadListener(TFCReloadListener.INSTANCE);
    }

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event)
    {
        LOGGER.debug("On Server Starting");

        CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
        ClearWorldCommand.register(dispatcher);
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event)
    {
        if (event.getWorld() != null && event.getWorld().isRemote())
        {
            // Client
            // Ask the server for the chunk data and climate information
            ChunkPos pos = event.getChunk().getPos();
            PacketHandler.get().send(PacketDistributor.SERVER.noArg(), new ChunkDataRequestPacket(pos.x, pos.z));
        }
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event)
    {
        // Check for possible collapse
        IWorld world = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);

        if (TFCTags.CAN_TRIGGER_COLLAPSE.contains(state.getBlock()) && world instanceof World)
        {
            CollapseRecipe.tryTriggerCollapse((World) world, pos);
        }
    }

    @SubscribeEvent
    public static void onNeighborUpdate(BlockEvent.NeighborNotifyEvent event)
    {
        IWorld world = event.getWorld();
        for (Direction direction : event.getNotifiedSides())
        {
            // Check each notified block for a potential gravity block
            BlockPos pos = event.getPos().offset(direction);
            BlockState state = world.getBlockState(pos);
            if (TFCTags.CAN_LANDSLIDE.contains(state.getBlock()) && world instanceof World)
            {
                // Here, we just record the position rather than immediately updating as this is called from `setBlockState` so it's preferred to handle it with just a little latency
                ((World) world).getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addLandslidePos(pos));
            }
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event)
    {
        IWorld world = event.getWorld();
        if (TFCTags.CAN_LANDSLIDE.contains(event.getState().getBlock()) && world instanceof World)
        {
            LandslideRecipe.tryLandslide((World) event.getWorld(), event.getPos(), event.getState());
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
}
