/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public final class TuyaNoise implements CenterOrDistanceNoise
{
    private static float calculateEasing(float f1)
    {
        return Mth.map(f1, 0, 0.23f, 1, 0);
    }

    private static float calculateClampedEasing(float f1)
    {
        return Mth.clamp(calculateEasing(f1), 0, 1);
    }

    /**
     * @param t The unscaled square distance from the tuya, roughly in [0, 1.2]
     * @return A noise function determining the tuya's height at any given position, in the range [0, 1]
     */
    private static float calculateShape(float t)
    {
        return t < 0.015f ? Mth.map(t, 0f, 0.015f, 0.8f, 1f)// Caldera
            : t < 0.125f ? Mth.map(t, 0.025f, 0.125f, 1f, 0.75f) // Shallow slope down
            : Mth.clampedMap(t, 0.125f, 0.16f, 0.75f, 0f); // Cliff
    }

    /**
     * @param t The unscaled square distance from the tuya, roughly in [0, 1.2]
     * @return A noise function determining the tuya's height at any given position, in the range [0, 1]
     */
    private static float calculateIcyShape(float t)
    {
        return t < 0.015f ? Mth.map(t, 0f, 0.015f, 0.8f, 1f) // Caldera
            : t < 0.125f ? Mth.map(t, 0.025f, 0.125f, 1f, 0.75f) // Shallow slope down
            : t < 0.16 ? Mth.map(t, 0.125f, 0.16f, 0.75f, 0f) // Cliff
            : Mth.clampedMap(t, 0.16f, 0.19f, 0f, 0.5f); // Ice edge
    }

    private final Cellular2D cellNoise;
    private final Noise2D jitterNoise;
    private final Seed seed;

    /**
     * @param seed The level seed - important, this is used from multiple different locations (base noise, surface builder, placement/decorator), and must have the same seed.
     */
    public TuyaNoise(Seed seed)
    {
        this.seed = seed;
        cellNoise = new Cellular2D(seed.seed(), 0.21f, 1).spread(0.0033f);
        jitterNoise = new OpenSimplex2D(seed.seed() + 1234123L).octaves(2).scaled(-0.016f, 0.016f).spread(0.128f);
    }

    public double modifyHeight(double x, double z, double baseHeight, int rarity, int baseVolcanoHeight, int scaleHeight, boolean icy)
    {
        final Cellular2D.Cell cell = sampleCell(x, z, rarity);
        if (cell != null)
        {
            final float easing = Mth.clamp(TuyaNoise.calculateEasing((float) cell.f1()) + (float) jitterNoise.noise(x, z), 0, 1);
            final float shape = icy ? TuyaNoise.calculateIcyShape(1 - easing) : TuyaNoise.calculateShape(1 - easing);
            final float additionalHeight = shape * scaleHeight + addNoise(seed.seed(), x, z);
            final float tuyaHeight = SEA_LEVEL_Y + baseVolcanoHeight + additionalHeight;
            return Mth.lerp(easing, baseHeight, 0.5f * (tuyaHeight + Math.max(tuyaHeight, baseHeight + 0.4f * additionalHeight)));
        }
        return baseHeight;
    }

    /**
     * Perlin noise surface
     */
    public float addNoise(long seed, double x, double z)
    {
        return (float) new OpenSimplex2D(seed).octaves(2).spread(0.1).scaled(-2, 3).noise(x, z);
    }

    @Override
    public boolean isValidBiome(BiomeExtension biome)
    {
        return biome.hasTuyas();
    }

    @Override
    public int getRarity(BiomeExtension biome)
    {
        return biome.getTuyaRarity();
    }

    /**
     * Calculate the closeness value to a tuya, in the range [0, 1]. 1 = Center of a tuya, 0 = Nowhere near.
     */
    public float calculateEasing(int x, int z, int rarity)
    {
        final Cellular2D.Cell cell = sampleCell(x, z, rarity);
        if (cell != null)
        {
            return calculateClampedEasing((float) cell.f1());
        }
        return 0;
    }

    /**
     * Calculate the center of the nearest tuya, if one exists, to the given x, z, at the given y.
     */
    @Nullable
    public BlockPos calculateCenter(int x, int y, int z, int rarity)
    {
        final Cellular2D.Cell cell = sampleCell(x, z, rarity);
        if (cell != null)
        {
            return new BlockPos((int) cell.x(), y, (int) cell.y());
        }
        return null;
    }

    /**
     * Sample the nearest tuya cell to a given position.
     * Returns {@code null} if the cell was excluded due to a rarity condition, or if the cell was too close to adjacent cells (possibly causing overlapping tuyas)
     */
    @Nullable
    private Cellular2D.Cell sampleCell(double x, double z, int rarity)
    {
        final Cellular2D.Cell cell = cellNoise.cell(x, z);
        if (Math.abs(cell.noise()) <= 1f / rarity)
        {
            return cell;
        }
        return null;
    }
}
