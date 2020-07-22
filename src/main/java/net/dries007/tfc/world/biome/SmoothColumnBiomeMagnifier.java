/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.minecraft.util.FastRandom;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.IBiomeMagnifier;

/**
 * This is both faster than the vanilla {@link net.minecraft.world.biome.ColumnFuzzedBiomeMagnifier} because it's *actually* a 2D version, not a fake 2D version, and has a customizable fuzz value.
 */
public class SmoothColumnBiomeMagnifier implements IBiomeMagnifier
{
    public static final SmoothColumnBiomeMagnifier VANILLA = new SmoothColumnBiomeMagnifier(0.9);
    public static final SmoothColumnBiomeMagnifier SMOOTH = new SmoothColumnBiomeMagnifier(0.7);

    private final double fuzzValue;

    public SmoothColumnBiomeMagnifier(double fuzzValue)
    {
        this.fuzzValue = fuzzValue;
    }

    public Biome getBiome(long seed, int x, int y, int z, BiomeManager.IBiomeReader biomeReader)
    {
        int offsetX = x - 2;
        int offsetZ = z - 2;
        int coordX = offsetX >> 2;
        int coordZ = offsetZ >> 2;
        double localX = (double) (offsetX & 3) / 4;
        double localZ = (double) (offsetZ & 3) / 4;
        double minDistance = Double.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < 4; i++)
        {
            boolean flagX = (i & 2) == 0;
            boolean flagZ = (i & 1) == 0;
            double distance = distanceToCorner(seed, flagX ? coordX : coordX + 1, flagZ ? coordZ : coordZ + 1, flagX ? localX : localX - 1, flagZ ? localZ : localZ - 1);
            if (distance < minDistance)
            {
                minDistance = distance;
                index = i;
            }
        }

        return biomeReader.getNoiseBiome((index & 2) == 0 ? coordX : coordX + 1, 0, (index & 1) == 0 ? coordZ : coordZ + 1);
    }

    private double distanceToCorner(long seed, int coordX, int coordZ, double localX, double localZ)
    {
        long localSeed = FastRandom.mix(seed, coordX);
        localSeed = FastRandom.mix(localSeed, coordZ);
        localSeed = FastRandom.mix(localSeed, coordX);
        localSeed = FastRandom.mix(localSeed, coordZ);
        double varianceX = randomValue(localSeed);
        localSeed = FastRandom.mix(localSeed, seed);
        double varianceZ = randomValue(localSeed);
        return square(localZ + varianceZ) + square(localX + varianceX);
    }

    private double randomValue(long seed)
    {
        return (double) ((int) Math.floorMod(seed >> 24, 1024L)) / 1024.0D * fuzzValue - 0.5D * fuzzValue;
    }

    private double square(double value)
    {
        return value * value;
    }
}
