package net.dries007.tfc.world.noise;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.world.ChunkBaseBlockSource;
import net.dries007.tfc.world.NoiseBasedAquifer;
import net.dries007.tfc.world.TFCChunkGenerator;

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

    public void updateForXZ(final double x, final double z)
    {
        interpolators.forEach(i -> i.updateForXZ(x, z));
    }

    public void updateForY(final double y)
    {
        interpolators.forEach(i -> i.updateForY(y));
    }

    public void swapSlices()
    {
        interpolators.forEach(TrilinearInterpolator::swapSlices);
    }

    /**
     * @param terrainNoise The terrain noise for the position. Positive values indicate solid terrain, in the range [-1, 1]
     * @return The block state for the position, including the aquifer, noise and noodle caves, and terrain.
     */
    public BlockState sample(int x, int y, int z, double terrainNoise)
    {
        // Compose noodle caves
        if (noodleToggle.sample() >= 0)
        {
            final double thickness = Mth.clampedMap(noodleThickness.sample(), -1, 1, 0.05, 0.1);
            final double ridgeA = Math.abs(1.5 * noodleRidgeA.sample()) - thickness;
            final double ridgeB = Math.abs(1.5 * noodleRidgeB.sample()) - thickness;
            final double ridge = Math.max(ridgeA, ridgeB);

            terrainNoise = Math.min(terrainNoise, ridge);
        }

        // Compose noise caves
        final double noiseCavesValue = noiseCaves.sample();
        terrainNoise = Math.min(terrainNoise, noiseCavesValue);

        // todo: improve aquifer placement and general handling. Many things be broken with this impl.
        final BlockState state = aquifer.computeSubstance(x, y, z, 0, terrainNoise);
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
