/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

import static net.dries007.tfc.world.TFCChunkGenerator.SEA_LEVEL_Y;

public final class VolcanoNoise
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
     * @param t The unscaled square distance from the volcano, roughly in [0, 1.2]
     * @return A noise function determining the volcano's height at any given position, in the range [0, 1]
     */
    private static float calculateShape(float t)
    {
        if (t > 0.025f)
        {
            return (5f / (9f * t + 1) - 0.5f) * 0.279173646008f;
        }
        else
        {
            float a = (t * 9f + 0.05f);
            return (8f * a * a + 2.97663265306f) * 0.279173646008f;
        }
    }

    private final Cellular2D cellNoise;
    private final Noise2D jitterNoise;

    /**
     * @param seed The level seed - important, this is used from multiple different locations (base noise, surface builder, placement/decorator), and must have the same seed.
     */
    public VolcanoNoise(long seed)
    {
        cellNoise = new Cellular2D(seed).spread(0.009f);
        jitterNoise = new OpenSimplex2D(seed + 1234123L).octaves(2).scaled(-0.0016f, 0.0016f).spread(0.128f);
    }

    public float modifyHeight(float x, float z, float baseHeight, int rarity, int baseVolcanoHeight, int scaleVolcanoHeight)
    {
        final Cellular2D.Cell cell = sampleCell(x, z, rarity);
        if (cell != null)
        {
            final float easing = Mth.clamp(VolcanoNoise.calculateEasing(cell.f1()) + jitterNoise.noise(x, z), 0, 1);
            final float shape = VolcanoNoise.calculateShape(1 - easing);
            final float volcanoHeight = SEA_LEVEL_Y + baseVolcanoHeight + shape * scaleVolcanoHeight;
            final float volcanoAdditionalHeight = shape * scaleVolcanoHeight;
            return Mth.lerp(easing, baseHeight, 0.5f * (volcanoHeight + Math.max(volcanoHeight, baseHeight + 0.4f * volcanoAdditionalHeight)));
        }
        return baseHeight;
    }

    /**
     * Calculate the closeness value to a volcano, in the range [0, 1]. 1 = Center of a volcano, 0 = Nowhere near.
     */
    public float calculateEasing(int x, int z, int rarity)
    {
        final Cellular2D.Cell cell = sampleCell(x, z, rarity);
        if (cell != null)
        {
            return calculateClampedEasing(cell.f1());
        }
        return 0;
    }

    /**
     * Calculate the center of the nearest volcano, if one exists, to the given x, z, at the given y.
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
     * Sample the nearest volcano cell to a given position.
     * Returns {@code null} if the cell was excluded due to a rarity condition, or if the cell was too close to adjacent cells (possibly causing overlapping volcanoes)
     */
    @Nullable
    private Cellular2D.Cell sampleCell(float x, float z, int rarity)
    {
        final Cellular2D.Cell cell = cellNoise.cell(x, z);
        if (Math.abs(cell.noise()) <= 1f / rarity)
        {
            return cell;
        }
        return null;
    }
}
