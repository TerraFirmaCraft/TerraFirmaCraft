package net.dries007.tfc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
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
import net.dries007.tfc.objects.types.RockManager;
import net.dries007.tfc.util.TFCReloadListener;
import net.dries007.tfc.util.support.SupportManager;
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

    @SubscribeEvent
    public static void onCreateWorldSpawn(WorldEvent.CreateSpawnPosition event)
    {
        if (event.getWorld().getWorldInfo().getGenerator() == TerraFirmaCraft.getWorldType())
        {
            // todo: handle this better
            event.getWorld().getWorldInfo().setSpawn(new BlockPos(0, 100, 0));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesChunk(AttachCapabilitiesEvent<Chunk> event)
    {
        World world = event.getObject().getWorld();
        if (world.getWorldType() == TerraFirmaCraft.getWorldType())
        {
            // Add the rock data to the chunk capability, for long term storage
            ChunkData data = ChunkDataProvider.get(world)
                .map(provider -> provider.get(event.getObject()))
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
