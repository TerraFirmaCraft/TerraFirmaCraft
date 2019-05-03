/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.chunkdata;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.network.PacketChunkData;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public final class CapabilityChunkData
{
    public static final ResourceLocation CHUNK_DATA = new ResourceLocation(MOD_ID, "chunkdata");

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(ChunkDataTFC.class, new ChunkDataTFC.ChunkDataStorage(), ChunkDataTFC::new);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesChunk(AttachCapabilitiesEvent<Chunk> event)
    {
        if (event.getObject().getWorld().getWorldType() == TerraFirmaCraft.getWorldTypeTFC())
            event.addCapability(CHUNK_DATA, new ChunkDataProvider());
    }

    @SubscribeEvent
    public static void onChunkWatchWatch(ChunkWatchEvent.Watch event)
    {
        Chunk chunk = event.getChunkInstance();
        if (chunk != null)
        {
            ChunkDataTFC data = chunk.getCapability(ChunkDataProvider.CHUNK_DATA_CAPABILITY, null);
            if (data != null && data.isInitialized())
            {
                NBTTagCompound nbt = (NBTTagCompound) ChunkDataProvider.CHUNK_DATA_CAPABILITY.writeNBT(data, null);
                TerraFirmaCraft.getNetwork().sendTo(new PacketChunkData(chunk.getPos(), nbt, data.getBaseTemp(), data.getRainfall()), event.getPlayer());
            }
        }
    }
}
