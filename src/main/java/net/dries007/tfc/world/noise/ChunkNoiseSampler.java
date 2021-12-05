package net.dries007.tfc.world.noise;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.world.ChunkBaseBlockSource;
import net.dries007.tfc.world.NoiseBasedAquifer;

public class ChunkNoiseSampler
{
    private final ChunkNoiseSamplingSettings settings;
    private final List<TrilinearInterpolator> interpolators;

    // Noise Caves
    private final TrilinearInterpolator noiseCaves;

    // Noodle Caves
    private final TrilinearInterpolator noodleToggle;
    private final TrilinearInterpolator noodleThickness;
    private final TrilinearInterpolator noodleRidgeA;
    private final TrilinearInterpolator noodleRidgeB;

    // Aquifer
    private final NoiseBasedAquifer aquifer;

    private final ChunkBaseBlockSource baseBlockSource;

    public ChunkNoiseSampler(ChunkPos chunkPos, NoiseSampler sampler, ChunkBaseBlockSource baseBlockSource, ChunkNoiseSamplingSettings settings, int seaLevel)
    {
        this.settings = settings;
        this.interpolators = new ArrayList<>();
        this.baseBlockSource = baseBlockSource;

        // Noise Caves
        this.noiseCaves = create(sampler.noiseCaves);

        // Noodle Caves
        this.noodleToggle = create(sampler.noodleToggle);
        this.noodleThickness = create(sampler.noodleThickness);
        this.noodleRidgeA = create(sampler.noodleRidgeA);
        this.noodleRidgeB = create(sampler.noodleRidgeB);

        // Aquifer
        this.aquifer = sampler.createAquifer(chunkPos, settings, seaLevel);
    }

    public void initializeForFirstCellX()
    {
        interpolators.forEach(TrilinearInterpolator::initializeForFirstCellX);
    }

    public void advanceCellX(final int cellX)
    {
        interpolators.forEach(i -> i.advanceCellX(cellX));
    }

    public void selectCellYZ(final int cellY, final int cellZ)
    {
        interpolators.forEach(i -> i.selectCellYZ(cellY, cellZ));
    }

    public void updateForY(final double y)
    {
        interpolators.forEach(i -> i.updateForY(y));
    }

    public void updateForX(final double x)
    {
        interpolators.forEach(i -> i.updateForX(x));
    }

    public void updateForZ(final double z)
    {
        interpolators.forEach(i -> i.updateForZ(z));
    }

    public void swapSlices()
    {
        interpolators.forEach(TrilinearInterpolator::swapSlices);
    }

    public BlockState sample(int x, int y, int z, double terrainNoise)
    {
        // base world generation and noise caves
        double noiseCavesValue = Mth.clamp(noiseCaves.sample() * 0.64, -1, 1);
        double value = Math.min(terrainNoise, noiseCavesValue);

        // noodle caves
        value = Mth.clamp(value * 0.64, -1, 1);
        value = value / 2 - value * value * value / 24;
        if (noodleToggle.sample() >= 0)
        {
            double thickness = Mth.clampedMap(noodleThickness.sample(), -1, 1, 0.05, 0.1);
            double ridgeA = Math.abs(1.5 * noodleRidgeA.sample()) - thickness;
            double ridgeB = Math.abs(1.5 * noodleRidgeB.sample()) - thickness;
            value = Math.min(value, Math.max(ridgeA, ridgeB));
        }

        final BlockState state = aquifer.computeSubstance(x, y, z, terrainNoise * 64, value);
        if (state != null)
        {
            return state;
        }
        return baseBlockSource.getBaseBlock(x, y, z);
    }

    public NoiseBasedAquifer aquifer()
    {
        return aquifer;
    }

    private TrilinearInterpolator create(TrilinearInterpolator.Source source)
    {
        final TrilinearInterpolator interpolator = new TrilinearInterpolator(settings, source);
        interpolators.add(interpolator);
        return interpolator;
    }
}
