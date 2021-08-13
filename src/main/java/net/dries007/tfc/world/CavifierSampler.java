package net.dries007.tfc.world;

import java.util.List;

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
        return interpolator.calculateValue(deltaZ) / 128d;
    }

    private void fillNoiseColumn(double[] noiseValues, int cellX, int cellZ, int minY, int cellCountY)
    {
        for (int cellIndexY = 0; cellIndexY <= cellCountY; ++cellIndexY)
        {
            int cellY = cellIndexY + minY;
            double noise = 180; // figure out what to pass in to be 'modified' by the cavifier
            noise = this.cavifier.modifyNoise(noise, cellY * cellHeight, cellZ * cellWidth, cellX * cellWidth);
            // noise = this.applySlide(noise, cellY); // biases the noise to the bottom
            noiseValues[cellIndexY] = noise;
        }
    }

    void addInterpolators(List<NoiseInterpolator> interpolators)
    {
        interpolators.add(interpolator);
    }
}
