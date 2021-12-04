package net.dries007.tfc.world.noise;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

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
    // todo

    public ChunkNoiseSampler(NoiseSampler sampler, ChunkNoiseSamplingSettings settings)
    {
        this.settings = settings;
        this.interpolators = new ArrayList<>();

        // Noise Caves
        this.noiseCaves = create(sampler.noiseCaves);

        // Noodle Caves
        this.noodleToggle = create(sampler.noodleToggle);
        this.noodleThickness = create(sampler.noodleThickness);
        this.noodleRidgeA = create(sampler.noodleRidgeA);
        this.noodleRidgeB = create(sampler.noodleRidgeB);
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
        // todo: where does terrainNoise come into the equation?

        // base world generation and noise caves
        double value = noiseCaves.sample();

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

        // value += filler.calculateNoise(x, y, z); // beardifier

        return noiseChunk.aquifer().computeSubstance(x, y, z, value, value); // aquifer todo: where on earth is this supposed to go reeeeeee
    }

    private TrilinearInterpolator create(TrilinearInterpolator.Source source)
    {
        final TrilinearInterpolator interpolator = new TrilinearInterpolator(settings, source);
        interpolators.add(interpolator);
        return interpolator;
    }
}
