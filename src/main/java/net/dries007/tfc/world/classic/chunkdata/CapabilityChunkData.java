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
import net.dries007.tfc.util.climate.ClimateTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

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
        // Per #922, if there's no world or no world type, something is seriously violating our assumptions and we will just fail.
        //noinspection ConstantConditions
        if (event.getObject().getWorld() != null && event.getObject().getWorld().getWorldType() == TerraFirmaCraft.getWorldType())
        {
            event.addCapability(CHUNK_DATA, new ChunkDataProvider());
        }
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
                // Update server side climate
                ClimateTFC.update(chunk.getPos(), data.getRegionalTemp(), data.getRainfall());

                // Update client side data
                NBTTagCompound nbt = (NBTTagCompound) ChunkDataProvider.CHUNK_DATA_CAPABILITY.writeNBT(data, null);
                TerraFirmaCraft.getNetwork().sendTo(new PacketChunkData(chunk.getPos(), nbt, data.getRegionalTemp(), data.getRainfall()), event.getPlayer());
            }
        }
    }
}
