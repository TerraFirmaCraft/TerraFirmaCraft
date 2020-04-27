/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.carver;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.noise.*;

public class WorleyCaveCarver
{
    /* The number of vertical samples to take. Noise is sampled every 4 blocks, then interpolated */
    private static final int SAMPLE_HEIGHT = 28;
    /* Depth to fill the lower levels with lava */
    private static final int LAVA_DEPTH = 11;

    private static final BlockState LAVA = Blocks.LAVA.getDefaultState();
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();

    private final INoise3D caveNoise;
    private final Set<Block> carvableBlocks;
    private final double heightFadeThreshold;
    private final double baseNoiseCutoff;
    private final double worleyNoiseCutoff;

    public WorleyCaveCarver(Random seedGenerator)
    {
        INoise2D caveNoiseBase = new SimplexNoise2D(seedGenerator.nextLong()).spread(0.01f).scaled(0, 1);
        INoise3D caveNoiseWorley = new WorleyNoise3D(seedGenerator.nextLong()).spread(0.012f).warped(
            new SimplexNoise3D(seedGenerator.nextLong()).octaves(4).spread(0.08f).scaled(-18, 18),
            new SimplexNoise3D(seedGenerator.nextLong()).octaves(4).spread(0.08f).scaled(-18, 18),
            new SimplexNoise3D(seedGenerator.nextLong()).octaves(4).spread(0.08f).scaled(-18, 18)
        ).scaled(0, 1);

        this.heightFadeThreshold = TFCConfig.COMMON.worleyCaveHeightFade.get();
        this.baseNoiseCutoff = TFCConfig.COMMON.worleyCaveBaseNoiseCutoff.get();
        this.worleyNoiseCutoff = TFCConfig.COMMON.worleyCaveWorleyNoiseCutoff.get();

        this.caveNoise = (x, y, z) -> {
            float baseNoise = caveNoiseBase.noise(x, z);
            if (baseNoise > this.baseNoiseCutoff)
            {
                return caveNoiseWorley.noise(x, y, z);
            }
            return 0;
        };

        this.carvableBlocks = TFCWorldCarvers.fixCarvableBlocksList(Collections.emptySet());
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    public void carve(IChunk chunkIn, int chunkX, int chunkZ, BitSet carvingMask)
    {
        float[] noiseValues = new float[5 * 5 * SAMPLE_HEIGHT];

        // Sample initial noise values
        for (int x = 0; x < 5; x++)
        {
            for (int z = 0; z < 5; z++)
            {
                for (int y = 0; y < SAMPLE_HEIGHT; y++)
                {
                    noiseValues[x + (z * 5) + (y * 25)] = caveNoise.noise(chunkX + x * 4, y * 7.3f, chunkZ + z * 4);
                }
            }
        }

        float[] section = new float[16 * 16];
        float[] prevSection = null;
        BlockPos.Mutable pos = new BlockPos.Mutable();

        // Create caves, layer by layer
        for (int y = SAMPLE_HEIGHT - 1; y >= 0; y--)
        {
            for (int x = 0; x < 4; x++)
            {
                for (int z = 0; z < 4; z++)
                {
                    float noiseUNW = noiseValues[(x + 0) + ((z + 0) * 5) + ((y + 0) * 25)];
                    float noiseUNE = noiseValues[(x + 1) + ((z + 0) * 5) + ((y + 0) * 25)];
                    float noiseUSW = noiseValues[(x + 0) + ((z + 1) * 5) + ((y + 0) * 25)];
                    float noiseUSE = noiseValues[(x + 1) + ((z + 1) * 5) + ((y + 0) * 25)];

                    float noiseMidN, noiseMidS;

                    // Lerp east-west edges
                    for (int sx = 0; sx < 4; sx++)
                    {
                        // Increasing x -> moving east
                        noiseMidN = NoiseUtil.lerp(noiseUNW, noiseUNE, 0.25f * sx);
                        noiseMidS = NoiseUtil.lerp(noiseUSW, noiseUSE, 0.25f * sx);

                        // Lerp faces
                        for (int sz = 0; sz < 4; sz++)
                        {
                            // Increasing z -> moving south
                            section[(x * 4 + sx) + (z * 4 + sz) * 16] = NoiseUtil.lerp(noiseMidN, noiseMidS, 0.25f * sz);
                        }
                    }

                    if (prevSection != null)
                    {
                        // We aren't on the first section, so we need to interpolate between sections, and assign blocks from the previous section up until this one
                        for (int y0 = 4 - 1; y0 >= 0; y0--)
                        {
                            int yPos = y * 4 + y0;
                            float heightFadeValue = yPos > heightFadeThreshold ? 1 - 0.02f * (float) (yPos - heightFadeThreshold) : 1;
                            for (int x0 = x * 4; x0 < (x + 1) * 4; x0++)
                            {
                                for (int z0 = z * 4; z0 < (z + 1) * 4; z0++)
                                {
                                    // set the current position
                                    pos.setPos(chunkX + x0, yPos, chunkZ + z0);

                                    float finalNoise = NoiseUtil.lerp(section[x0 + 16 * z0], prevSection[x0 + 16 * z0], 0.25f * y0);
                                    finalNoise *= heightFadeValue;

                                    if (finalNoise > worleyNoiseCutoff)
                                    {
                                        carveBlock(chunkIn, carvingMask, pos);
                                    }
                                }
                            }
                        }
                    }
                    // End of x/z iteration
                }
            }
            // End of x/z loop, so move section to previous
            prevSection = Arrays.copyOf(section, section.length);
        }
    }

    protected boolean canCarveBlock(BlockState state, BlockState aboveState)
    {
        // todo: allow carving sand + gravel type blocks if aboveState is not fluid. See WorldCarver#canCarveBlock
        return carvableBlocks.contains(state.getBlock());
    }

    /**
     * Tries to carve a single block
     *
     * @return true if the surface was reached, we can stop carving this column
     */
    private boolean carveBlock(IChunk chunk, BitSet carvingMask, BlockPos.Mutable pos)
    {
        boolean reachedSurface = false;
        int maskIndex = (pos.getX() & 15) | ((pos.getZ() & 15) << 4) | (pos.getY() << 8);
        if (!carvingMask.get(maskIndex))
        {
            carvingMask.set(maskIndex);
            BlockState stateAt = chunk.getBlockState(pos);
            pos.move(Direction.UP);
            BlockState stateAbove = chunk.getBlockState(pos);
            pos.move(Direction.DOWN);
            if (stateAt.getBlock() == Blocks.GRASS_BLOCK || stateAt.getBlock() == Blocks.MYCELIUM)
            {
                // todo: use this?
                reachedSurface = true;
            }
            if (canCarveBlock(stateAt, stateAbove))
            {
                if (pos.getY() < LAVA_DEPTH)
                {
                    chunk.setBlockState(pos, LAVA, false);
                }
                else
                {
                    chunk.setBlockState(pos, CAVE_AIR, false);
                    // todo: vanilla updates the bottom material based on the surface material here, should we?
                    //pos.setPos(pos).move(Direction.DOWN);
                    //if (chunk.getBlockState(pos).getBlock() == Blocks.DIRT)
                    //{
                    //    chunk.setBlockState(pos, biomeGetter.apply(pos).getSurfaceBuilderConfig().getTop(), false);
                    //}
                }
            }
        }
        return reachedSurface;
    }
}
