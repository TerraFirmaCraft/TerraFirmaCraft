/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EventHandler
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
            ChunkData data;
            ChunkDataProvider chunkDataProvider = ChunkDataProvider.get(world);
            if (chunkDataProvider != null)
            {
                data = chunkDataProvider.get(event.getObject());
            }
            else
            {
                data = new ChunkData();
            }

            event.addCapability(ChunkDataCapability.KEY, data);
        }
    }

    private EventHandler() {}
}
