/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.region;

import net.dries007.tfc.world.layer.framework.Area;

import static net.dries007.tfc.world.layer.TFCLayers.*;

public enum ChooseBiomes implements RegionTask
{
    INSTANCE;

    private static final int[] MOUNTAIN_ALTITUDE_BIOMES = {MOUNTAINS, MOUNTAINS, MOUNTAINS, OLD_MOUNTAINS, OLD_MOUNTAINS, PLATEAU, HIGHLANDS};
    private static final int[] OCEANIC_MOUNTAIN_ALTITUDE_BIOMES = {VOLCANIC_MOUNTAINS, VOLCANIC_OCEANIC_MOUNTAINS, VOLCANIC_OCEANIC_MOUNTAINS, OCEANIC_MOUNTAINS, OCEANIC_MOUNTAINS, ROLLING_HILLS};
    private static final int[][] ALTITUDE_BIOMES = {
        {PLAINS, PLAINS, HILLS, HILLS, ROLLING_HILLS, LOW_CANYONS, LOWLANDS, LOWLANDS}, // Low
        {PLAINS, HILLS, ROLLING_HILLS, HIGHLANDS, INVERTED_BADLANDS, BADLANDS, PLATEAU, CANYONS, LOW_CANYONS}, // Mid
        {HIGHLANDS, HIGHLANDS, HIGHLANDS, ROLLING_HILLS, BADLANDS, PLATEAU, PLATEAU, OLD_MOUNTAINS, OLD_MOUNTAINS}, // High
    };
//    private static final int[] ISLAND_BIOMES = {PLAINS, HILLS, ROLLING_HILLS, VOLCANIC_OCEANIC_MOUNTAINS, VOLCANIC_OCEANIC_MOUNTAINS};
    private static final int[] ISLAND_BIOMES = {ATOLL};
    private static final int[] MID_DEPTH_OCEAN_BIOMES = {DEEP_OCEAN, OCEAN, OCEAN, OCEAN_REEF, OCEAN_REEF, OCEAN_REEF};

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final Area blobArea = context.generator().biomeArea.get();
        final long rngSeed = context.random.nextLong();
        final long climateSeed = context.random.nextLong();

        for (int x = region.minX(); x <= region.maxX(); x++)
        {
            for (int z = region.minZ(); z <= region.maxZ(); z++)
            {
                final Region.Point point = region.maybeAt(x, z);
                if (point != null)
                {
                    final int areaSeed = blobArea.get(x, z);
                    if (point.island())
                    {
                        point.biome = randomSeededFrom(rngSeed, areaSeed, ISLAND_BIOMES);
                    }
                    else if (point.mountain())
                    {
                        point.biome = randomSeededFrom(rngSeed, areaSeed, point.coastalMountain()
                            ? OCEANIC_MOUNTAIN_ALTITUDE_BIOMES
                            : MOUNTAIN_ALTITUDE_BIOMES);
                    }
                    else if (point.land())
                    {
                        point.biome = randomSeededFrom(rngSeed, areaSeed, ALTITUDE_BIOMES[point.discreteBiomeAltitude()]);
                    }
                    else if (point.baseOceanDepth < 3)
                    {
                        point.biome = OCEAN;
                    }
                    else if (point.baseOceanDepth > 9)
                    {
                        point.biome = DEEP_OCEAN_TRENCH;
                    }
                    else if (point.baseOceanDepth >= 5 || point.distanceToEdge < 2)
                    {
                        point.biome = DEEP_OCEAN;
                    }
                    else
                    {
                        point.biome = randomSeededFrom(rngSeed, areaSeed, MID_DEPTH_OCEAN_BIOMES);
                    }

                    // Adjust certain biome placements by climate. Low, freshwater biomes don't make much sense appearing in
                    // very low rainfall areas, so replace them with slightly higher biomes
                    final float minRainForLowFreshWaterBiomes = 90f + Math.floorMod(areaSeed ^ climateSeed, 40);
                    if (point.rainfall < minRainForLowFreshWaterBiomes)
                    {
                        if (point.biome == LOWLANDS) point.biome = PLAINS;
                        else if (point.biome == LOW_CANYONS) point.biome = CANYONS;
                    }

                    // Prevent badlands from appearing in very high rainfall environments
                    final float maxRainfallForBadlands = 420f + Math.floorMod(areaSeed ^ climateSeed, 40);
                    if (point.rainfall > maxRainfallForBadlands)
                    {
                        if (point.biome == BADLANDS) point.biome = HIGHLANDS;
                        else if (point.biome == INVERTED_BADLANDS) point.biome = ROLLING_HILLS;
                    }
                }
            }
        }
    }

    private int randomSeededFrom(long rngSeed, int areaSeed, int[] choices)
    {
        return choices[Math.floorMod(rngSeed ^ areaSeed, choices.length)];
    }
}
