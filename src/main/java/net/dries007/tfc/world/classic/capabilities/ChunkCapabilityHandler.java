package net.dries007.tfc.world.classic.capabilities;

import net.dries007.tfc.TerraFirmaCraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.dries007.tfc.Constants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public final class ChunkCapabilityHandler
{
    public static final ResourceLocation CHUNK_DATA = new ResourceLocation(MOD_ID, "chunkdata");

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(ChunkDataTFC.class, new ChunkDataTFC.ChunkDataStorage(), ChunkDataTFC::new);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesChunk(AttachCapabilitiesEvent<Chunk> event)
    {
        event.addCapability(CHUNK_DATA, new ChunkDataProvider());
    }

    @SubscribeEvent
    public static void onChunkWatchWatch(ChunkWatchEvent.Watch event)
    {
        // todo: error proof
        Chunk c = event.getPlayer().world.getChunkFromChunkCoords(event.getChunk().x, event.getChunk().z);
        ChunkDataTFC data = c.getCapability(ChunkDataProvider.CHUNK_DATA_CAPABILITY, null);
        //noinspection ConstantConditions
        NBTTagCompound nbt = (NBTTagCompound) ChunkDataProvider.CHUNK_DATA_CAPABILITY.writeNBT(data, null);
        TerraFirmaCraft.getNetwork().sendTo(new ChunkDataMessage(event.getChunk(), nbt), event.getPlayer());
    }
}
