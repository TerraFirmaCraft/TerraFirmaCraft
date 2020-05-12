/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.*;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Seasonally regenerates rocks, sticks, snow, plants, crops and bushes.
 */
@Mod.EventBusSubscriber(modid = MOD_ID)
public final class WorldRegenHandler
{
    private static final WorldGenLooseRocks ROCKS_GEN = new WorldGenLooseRocks(false);
    private static final WorldGenSnowIce SNOW_GEN = new WorldGenSnowIce();
    private static final WorldGenWildCrops CROPS_GEN = new WorldGenWildCrops();
    private static final WorldGenBerryBushes BUSH_GEN = new WorldGenBerryBushes();

    private static final Random RANDOM = new Random();
    private static final List<ChunkPos> POSITIONS = new LinkedList<>();

    @SubscribeEvent
    public static void onChunkLoad(ChunkDataEvent.Load event)
    {
        // This is disabled for now, as it was causing runaway regeneration of stuff (after multiple attempts to fix it), and potential performance issues
        // This needs some proper design work on how it is going to operate
        // todo: fix this
        /*if (event.getWorld().provider.getDimension() == 0)
        {
            ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.getChunk());
            if (chunkDataTFC.isInitialized() && !chunkDataTFC.isSpawnProtected() && (chunkDataTFC.getLastUpdateYear() > CalendarTFC.CALENDAR_TIME.getTotalYears() || CalendarTFC.PLAYER_TIME.getTicks() - chunkDataTFC.getLastUpdateTick() > 6 * ICalendar.TICKS_IN_DAY))
            {
                //POSITIONS.add(event.getChunk().getPos());
            }
        }*/
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (!event.world.isRemote && event.phase == TickEvent.Phase.END)
        {
            if (!POSITIONS.isEmpty())
            {
                ChunkPos pos = POSITIONS.remove(0);
                ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.world, pos.getBlock(0, 0, 0));

                IChunkProvider chunkProvider = event.world.getChunkProvider();
                IChunkGenerator chunkGenerator = ((ChunkProviderServer) chunkProvider).chunkGenerator;

                // If past the update time, then run some regeneration of natural resources
                long updateDelta = CalendarTFC.PLAYER_TIME.getTicks() - chunkDataTFC.getLastUpdateTick();
                if (updateDelta > ConfigTFC.General.WORLD_REGEN.minimumTime * ICalendar.TICKS_IN_DAY && !chunkDataTFC.isSpawnProtected())
                {
                    float regenerationModifier = MathHelper.clamp((float) updateDelta / (4 * ConfigTFC.General.WORLD_REGEN.minimumTime * ICalendar.TICKS_IN_DAY), 0, 1);

                    // Loose rocks - factors in time since last update
                    if (ConfigTFC.General.WORLD_REGEN.sticksRocksModifier > 0)
                    {
                        double rockModifier = ConfigTFC.General.WORLD_REGEN.sticksRocksModifier * regenerationModifier;
                        ROCKS_GEN.setFactor(rockModifier);
                        ROCKS_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);

                        int stickDensity = (int) (rockModifier * (1 + (int) (3f * chunkDataTFC.getFloraDensity())));
                        WorldGenTrees.generateLooseSticks(RANDOM, pos.x, pos.z, event.world, stickDensity);
                    }

                    chunkDataTFC.resetLastUpdateTick();
                }

                // Plants + crops. Only runs once (maximum) each year
                if (CalendarTFC.CALENDAR_TIME.getMonthOfYear().isWithin(Month.APRIL, Month.JULY) && !chunkDataTFC.isSpawnProtected() && CalendarTFC.CALENDAR_TIME.getTotalYears() > chunkDataTFC.getLastUpdateYear())
                {
                    if (RANDOM.nextInt(20) == 0)
                    {
                        CROPS_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);
                    }
                    BUSH_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);

                    chunkDataTFC.resetLastUpdateYear();
                }

                // Update snow / ice from large calendar changes
                if (updateDelta > ICalendar.TICKS_IN_DAY * 4)
                {
                    SNOW_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);
                }
            }
        }
    }
}
