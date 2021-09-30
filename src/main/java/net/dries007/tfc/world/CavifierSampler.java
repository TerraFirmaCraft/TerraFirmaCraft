/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.List;

import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Cavifier;
import net.minecraft.world.level.levelgen.NoiseInterpolator;

public class CavifierSampler
{
    private final Cavifier cavifier;
    private final int cellWidth, cellHeight;
    private final NoiseInterpolator interpolator;

    public CavifierSampler(Cavifier cavifier, ChunkPos chunkPos, int cellWidth, int cellHeight, int cellCountX, int cellCountY, int cellCountZ, int minCellY)
    {
        this.cavifier = cavifier;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.interpolator = new NoiseInterpolator(cellCountX, cellCountY, cellCountZ, chunkPos, minCellY, this::fillNoiseColumn);
    }

    public double sample(double deltaZ)
    {
        return interpolator.calculateValue(deltaZ);
    }

    public void addInterpolators(List<NoiseInterpolator> interpolators)
    {
        interpolators.add(interpolator);
    }

    private void fillNoiseColumn(double[] noiseValues, int cellX, int cellZ, int minCellY, int cellCountY)
    {
        final int cellCutoffY = Mth.intFloorDiv(50, cellCountY) - minCellY;
        for (int cellIndexY = 0; cellIndexY <= cellCountY; ++cellIndexY)
        {
            int cellY = cellIndexY + minCellY;
            double noise = 180; // figure out what to pass in to be 'modified' by the cavifier
            noise = this.cavifier.modifyNoise(noise, cellY * cellHeight, cellZ * cellWidth, cellX * cellWidth);
            noise *= (1 / 128d); // range of [-1, 1], >0 = solid
            if (cellY >= cellCutoffY)
            {
                double slideFactor = Mth.inverseLerp(cellY, cellCutoffY, cellCountY); // [0, 1], 1 = top of world
                noise = Mth.lerp(slideFactor, noise, 4);
            }
            noiseValues[cellIndexY] = noise;
        }
    }
}
