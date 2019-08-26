/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.*;

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
    private static final Map<ChunkPos, Double> REGENERATE_STICKS_ROCKS = new HashMap<>();
    private static final Set<ChunkPos> REGENERATE_SNOW = new HashSet<>();
    private static final Set<ChunkPos> REGENERATE_PLANTS = new HashSet<>();
    private static final Set<ChunkPos> REGENERATE_CROPS = new HashSet<>();

    private static final WorldGenLooseRocks ROCKS_GEN = new WorldGenLooseRocks(false);
    private static final WorldGenSnowIce SNOW_GEN = new WorldGenSnowIce();
    private static final WorldGenWildCrops CROPS_GEN = new WorldGenWildCrops();
    private static final WorldGenBerryBushes BUSH_GEN = new WorldGenBerryBushes();

    @SubscribeEvent
    public static void onChunkLoad(ChunkDataEvent.Load event)
    {
        if (event.getWorld().provider.getDimension() == 0)
        {
            ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.getChunk());
            if (chunkDataTFC.isInitialized())
            {
                ChunkPos chunkPos = event.getChunk().getPos();
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
                        REGENERATE_STICKS_ROCKS.put(chunkPos, looseRegeneration);
                    }
                }

                //Snow don't need a config
                long deltaSnow = CalendarTFC.CALENDAR_TIME.getTicks() - chunkDataTFC.getLastUpdateSnow();
                double deltaSnowMonths = deltaSnow / (double) (CalendarTFC.INSTANCE.getDaysInMonth() * ICalendar.TICKS_IN_DAY);

                if (deltaSnowMonths > 0.5D)
                {
                    REGENERATE_SNOW.add(chunkPos);
                }

                if (ConfigTFC.GENERAL.regenPlants > 0)
                {
                    long deltaPlants = CalendarTFC.CALENDAR_TIME.getTicks() - chunkDataTFC.getLastUpdatePlants();
                    double deltaPlantsMonths = deltaPlants / (double) (CalendarTFC.INSTANCE.getDaysInMonth() * ICalendar.TICKS_IN_DAY);

                    if (deltaPlantsMonths > ConfigTFC.GENERAL.regenPlants)
                    {
                        REGENERATE_PLANTS.add(chunkPos);
                    }
                }

                if (ConfigTFC.GENERAL.regenCrops > 0)
                {
                    long deltaCrops = CalendarTFC.CALENDAR_TIME.getTicks() - chunkDataTFC.getLastUpdateCrops();
                    double deltaCropsMonths = deltaCrops / (double) (CalendarTFC.INSTANCE.getDaysInMonth() * ICalendar.TICKS_IN_DAY);

                    if (deltaCropsMonths > ConfigTFC.GENERAL.regenCrops)
                    {
                        REGENERATE_CROPS.add(chunkPos);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (!event.world.isRemote && event.phase == TickEvent.Phase.END)
        {
            IChunkProvider chunkProvider = event.world.getChunkProvider();
            IChunkGenerator chunkGenerator = ((ChunkProviderServer) chunkProvider).chunkGenerator;
            if (REGENERATE_CROPS.size() > 0)
            {
                ChunkPos pos = REGENERATE_CROPS.iterator().next();
                REGENERATE_CROPS.remove(pos);

                ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.world.getChunk(pos.x, pos.z));
                chunkDataTFC.setLastUpdateCrops(CalendarTFC.CALENDAR_TIME.getTicks());

                Random rand = new Random(event.world.getSeed() + CalendarTFC.TOTAL_TIME.getTicks());
                rand.setSeed((long) pos.x * (rand.nextLong() / 2L * 2L + 1L) + (long) pos.z * (rand.nextLong() / 2L * 2L + 1L) ^ event.world.getSeed());

                if (rand.nextInt(20) == 0)
                {
                    BlockPos chunkPos = new BlockPos(pos.x << 4, 0, pos.z << 4);
                    BlockPos start = event.world.getHeight(chunkPos.add(rand.nextInt(16) + 8, 0, rand.nextInt(16) + 8));
                    CROPS_GEN.generate(event.world, rand, start);
                }
                BUSH_GEN.generate(rand, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);
            }
            if (REGENERATE_PLANTS.size() > 0)
            {
                ChunkPos pos = REGENERATE_PLANTS.iterator().next();
                REGENERATE_PLANTS.remove(pos);

                ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.world.getChunk(pos.x, pos.z));
                chunkDataTFC.setLastUpdatePlants(CalendarTFC.CALENDAR_TIME.getTicks());

                Random rand = new Random(event.world.getSeed() + CalendarTFC.TOTAL_TIME.getTicks());
                rand.setSeed((long) pos.x * (rand.nextLong() / 2L * 2L + 1L) + (long) pos.z * (rand.nextLong() / 2L * 2L + 1L) ^ event.world.getSeed());

                BlockPos biomePos = new BlockPos(pos.getXStart(), 0, pos.getZStart());
                Biome biome = event.world.getBiome(biomePos);
                if (biome instanceof BiomeTFC)
                {
                    ((BiomeTFC) biome).decorator.decorate(event.world, rand, biome, biomePos);
                }
            }
            if (REGENERATE_STICKS_ROCKS.size() > 0)
            {
                ChunkPos pos = REGENERATE_STICKS_ROCKS.keySet().iterator().next();
                double looseRegeneration = REGENERATE_STICKS_ROCKS.get(pos);
                REGENERATE_STICKS_ROCKS.remove(pos);

                ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.world.getChunk(pos.x, pos.z));
                chunkDataTFC.setLastUpdateCrops(CalendarTFC.CALENDAR_TIME.getTicks());

                Random rand = new Random(event.world.getSeed() + CalendarTFC.TOTAL_TIME.getTicks());
                rand.setSeed((long) pos.x * (rand.nextLong() / 2L * 2L + 1L) + (long) pos.z * (rand.nextLong() / 2L * 2L + 1L) ^ event.world.getSeed());

                ROCKS_GEN.setFactor(looseRegeneration);
                ROCKS_GEN.generate(rand, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);
                int stickDensity = (int) (looseRegeneration * (1 + (int) (3f * chunkDataTFC.getFloraDensity())));
                WorldGenTrees.generateLooseSticks(rand, pos.x, pos.z, event.world, stickDensity);
            }
            if (REGENERATE_SNOW.size() > 0)
            {
                ChunkPos pos = REGENERATE_SNOW.iterator().next();
                REGENERATE_SNOW.remove(pos);

                ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.world.getChunk(pos.x, pos.z));
                chunkDataTFC.setLastUpdateSnow(CalendarTFC.CALENDAR_TIME.getTicks());

                Random rand = new Random(event.world.getSeed() + CalendarTFC.TOTAL_TIME.getTicks());
                rand.setSeed((long) pos.x * (rand.nextLong() / 2L * 2L + 1L) + (long) pos.z * (rand.nextLong() / 2L * 2L + 1L) ^ event.world.getSeed());

                SNOW_GEN.generate(rand, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);
            }
        }
    }
}
