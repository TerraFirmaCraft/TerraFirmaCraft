/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
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
import net.dries007.tfc.world.classic.biomes.BiomeTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.*;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

/**
 * Seasonally regenerates rocks, sticks, snow, plants, crops and bushes.
 */
@Mod.EventBusSubscriber(modid = MOD_ID)
public final class WorldRegenHandler
{
    /* The list of chunk positions to check for world regeneration */
    private static final Queue<ChunkPos> POSITIONS_TO_CHECK = new LinkedList<>();

    private static final WorldGenLooseRocks ROCKS_GEN = new WorldGenLooseRocks(false);
    private static final WorldGenSnowIce SNOW_GEN = new WorldGenSnowIce();
    private static final WorldGenWildCrops CROPS_GEN = new WorldGenWildCrops();
    private static final WorldGenBerryBushes BUSH_GEN = new WorldGenBerryBushes();

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onChunkLoad(ChunkDataEvent.Load event)
    {
        if (event.getWorld().provider.getDimension() == 0)
        {
            ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.getChunk());
            if (chunkDataTFC.isInitialized())
            {
                POSITIONS_TO_CHECK.offer(event.getChunk().getPos());
            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (!event.world.isRemote && event.phase == TickEvent.Phase.END)
        {
            ChunkPos pos = POSITIONS_TO_CHECK.poll();
            if (pos != null)
            {
                IChunkProvider chunkProvider = event.world.getChunkProvider();
                IChunkGenerator chunkGenerator = ((ChunkProviderServer) chunkProvider).chunkGenerator;
                ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.world.getChunk(pos.x, pos.z));

                // Loose rocks / sticks (debris)
                if (ConfigTFC.GENERAL.regenSticksRocks > 0)
                {
                    long deltaRocks = CalendarTFC.TOTAL_TIME.getTicks() - chunkDataTFC.getLastUpdateRocks();
                    double looseRegeneration = deltaRocks / (ICalendar.TICKS_IN_DAY * ConfigTFC.GENERAL.regenSticksRocks);
                    if (looseRegeneration > 0.1D)
                    {
                        if (looseRegeneration > 1)
                        {
                            looseRegeneration = 1;
                        }
                        chunkDataTFC.setLastUpdateRocks(CalendarTFC.CALENDAR_TIME.getTicks());

                        ROCKS_GEN.setFactor(looseRegeneration);
                        ROCKS_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);

                        int stickDensity = (int) (looseRegeneration * (1 + (int) (3f * chunkDataTFC.getFloraDensity())));
                        WorldGenTrees.generateLooseSticks(RANDOM, pos.x, pos.z, event.world, stickDensity);
                    }
                }

                // Plant regeneration
                if (ConfigTFC.GENERAL.regenPlants > 0)
                {
                    long deltaPlants = CalendarTFC.CALENDAR_TIME.getTicks() - chunkDataTFC.getLastUpdatePlants();
                    double deltaPlantsMonths = deltaPlants / (double) (CalendarTFC.INSTANCE.getDaysInMonth() * ICalendar.TICKS_IN_DAY);

                    if (deltaPlantsMonths > ConfigTFC.GENERAL.regenPlants)
                    {
                        chunkDataTFC.setLastUpdatePlants(CalendarTFC.CALENDAR_TIME.getTicks());

                        BlockPos biomePos = new BlockPos(pos.getXStart(), 0, pos.getZStart());
                        Biome biome = event.world.getBiome(biomePos);
                        if (biome instanceof BiomeTFC)
                        {
                            ((BiomeTFC) biome).decorator.decorate(event.world, RANDOM, biome, biomePos);
                        }
                    }
                }

                // Update snow / ice from large calendar changes
                long deltaSnow = CalendarTFC.CALENDAR_TIME.getTicks() - chunkDataTFC.getLastUpdateSnow();
                double deltaSnowMonths = deltaSnow / (double) (CalendarTFC.INSTANCE.getDaysInMonth() * ICalendar.TICKS_IN_DAY);
                if (deltaSnowMonths > 0.5D)
                {
                    chunkDataTFC.setLastUpdateSnow(CalendarTFC.CALENDAR_TIME.getTicks());
                    SNOW_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);
                }

                if (ConfigTFC.GENERAL.regenCrops > 0)
                {
                    long deltaCrops = CalendarTFC.CALENDAR_TIME.getTicks() - chunkDataTFC.getLastUpdateCrops();
                    double deltaCropsMonths = deltaCrops / (double) (CalendarTFC.INSTANCE.getDaysInMonth() * ICalendar.TICKS_IN_DAY);

                    if (deltaCropsMonths > ConfigTFC.GENERAL.regenCrops)
                    {
                        chunkDataTFC.setLastUpdateCrops(CalendarTFC.CALENDAR_TIME.getTicks());

                        if (RANDOM.nextInt(20) == 0)
                        {
                            BlockPos chunkPos = new BlockPos(pos.x << 4, 0, pos.z << 4);
                            BlockPos start = event.world.getHeight(chunkPos.add(RANDOM.nextInt(16) + 8, 0, RANDOM.nextInt(16) + 8));
                            CROPS_GEN.generate(event.world, RANDOM, start);
                        }
                        BUSH_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);
                    }
                }
            }
        }
    }
}
