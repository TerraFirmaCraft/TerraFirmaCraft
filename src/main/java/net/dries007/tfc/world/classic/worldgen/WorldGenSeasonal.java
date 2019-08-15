/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

/**
 * Seasonally regenerates rocks, sticks, small ores and anything that should be regenerated with time.
 */
@Mod.EventBusSubscriber(modid = MOD_ID)
public final class WorldGenSeasonal
{
    private static final int REGEN_NEEDED_DAYS = 1; //Total days needed to regen chunk

    private static final Set<ChunkPos> REGENERATE_CHUNK = new HashSet<>(); //Chunks marked to regen

    @SubscribeEvent
    public static void onChunkLoad(ChunkDataEvent.Load event)
    {
        if (event.getWorld().provider.getDimension() == 0)
        {
            ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.getChunk());
            if (chunkDataTFC.isInitialized() && chunkDataTFC.getLastSeasonalTick() + REGEN_NEEDED_DAYS * ICalendar.TICKS_IN_DAY < CalendarTFC.TOTAL_TIME.getTicks())
            {
                REGENERATE_CHUNK.add(event.getChunk().getPos());
            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (!event.world.isRemote && event.phase == TickEvent.Phase.END && REGENERATE_CHUNK.size() > 0)
        {
            //one regen per sec
            ChunkPos pos = REGENERATE_CHUNK.iterator().next();
            REGENERATE_CHUNK.remove(pos);

            Random rand = new Random(event.world.getSeed());
            rand.setSeed((long) pos.x * (rand.nextLong() / 2L * 2L + 1L) + (long) pos.z * (rand.nextLong() / 2L * 2L + 1L) ^ event.world.getSeed());

            WorldGenLooseRocks.generateLooseRocks(rand, pos.x, pos.z, event.world);
            //todo snow?

            ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.world.getChunk(pos.x, pos.z));
            chunkDataTFC.setSeasonalTick(CalendarTFC.TOTAL_TIME.getTicks());
        }
    }
}
