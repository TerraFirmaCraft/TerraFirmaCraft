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
    private static final int[] HIGH_ALTITUDE_BIOMES = {HIGHLANDS, HIGHLANDS, HIGHLANDS, ROLLING_HILLS, BADLANDS, PLATEAU, PLATEAU, OLD_MOUNTAINS, OLD_MOUNTAINS};
    private static final int[] MID_ALTITUDE_BIOMES = {PLAINS, HILLS, ROLLING_HILLS, HIGHLANDS, INVERTED_BADLANDS, BADLANDS, PLATEAU, CANYONS, LOW_CANYONS};
    private static final int[] LOW_ALTITUDE_BIOMES = {PLAINS, PLAINS, HILLS, HILLS, ROLLING_HILLS, LOW_CANYONS, LOWLANDS, LOWLANDS};
    private static final int[] ISLAND_BIOMES = {PLAINS, HILLS, ROLLING_HILLS, VOLCANIC_OCEANIC_MOUNTAINS, VOLCANIC_OCEANIC_MOUNTAINS};
    private static final int[] MID_DEPTH_OCEAN_BIOMES = {DEEP_OCEAN, OCEAN, OCEAN, OCEAN_REEF, OCEAN_REEF, OCEAN_REEF};

    @Override
    public void apply(RegionGenerator.Context context)
    {
        final Region region = context.region;
        final Area blobArea = context.generator().biomeArea.get();
        final long rngSeed = context.random.nextLong();

        for (int dx = 0; dx < region.sizeX(); dx++)
        {
            for (int dz = 0; dz < region.sizeZ(); dz++)
            {
                final int index = dx + region.sizeX() * dz;
                final Region.Point point = region.data()[index];
                final int areaSeed = blobArea.get(region.minX() + dx, region.minZ() + dz);
                if (point != null)
                {
                    if (point.island())
                    {
                        point.biome = randomSeededFrom(rngSeed, areaSeed, ISLAND_BIOMES);
                    }
                    else if (point.mountain())
                    {
                        point.biome = randomSeededFrom(rngSeed, areaSeed, point.coastalMountain() ? OCEANIC_MOUNTAIN_ALTITUDE_BIOMES : MOUNTAIN_ALTITUDE_BIOMES);
                    }
                    else if (point.land())
                    {
                        point.biome = randomSeededFrom(rngSeed, areaSeed, switch (point.discreteBiomeAltitude()) {
                            case 2 -> HIGH_ALTITUDE_BIOMES;
                            case 1 -> MID_ALTITUDE_BIOMES;
                            case 0 -> LOW_ALTITUDE_BIOMES;
                            default -> throw new IllegalStateException("Invalid: " + point.discreteBiomeAltitude());
                        });
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
                }
            }
        }
    }

    private int randomSeededFrom(long rngSeed, int areaSeed, int[] choices)
    {
        return choices[Math.floorMod(rngSeed ^ areaSeed, choices.length)];
    }
}
