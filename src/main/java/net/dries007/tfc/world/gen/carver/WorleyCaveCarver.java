/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.carver;

import java.util.Arrays;
import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

import net.dries007.tfc.world.noise.*;

@ParametersAreNonnullByDefault
public class WorleyCaveCarver
{
    /* The number of vertical samples to take. Noise is sampled every 4 blocks, then interpolated */
    private static final int SAMPLE_HEIGHT = 28;
    /* Depth to fill the lower levels with lava */
    private static final int LAVA_DEPTH = 11;

    private static final float NOISE_THRESHOLD = 0.38f;
    private static final float HEIGHT_FADE_THRESHOLD = 80;

    private static final BlockState LAVA = Blocks.LAVA.getDefaultState();
    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private static final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();

    private final INoise3D caveNoise;

    public WorleyCaveCarver(Random seedGenerator)
    {
        INoise2D caveNoiseBase = new SimplexNoise2D(seedGenerator.nextLong()).spread(0.01f).scaled(0, 3f);
        INoise3D caveNoiseWorley = new WorleyNoise3D(seedGenerator.nextLong()).spread(0.016f).warped(
            new SimplexNoise3D(seedGenerator.nextLong()).octaves(4).spread(0.08f).scaled(-18, 18),
            new SimplexNoise3D(seedGenerator.nextLong()).octaves(4).spread(0.08f).scaled(-18, 18),
            new SimplexNoise3D(seedGenerator.nextLong()).octaves(4).spread(0.08f).scaled(-18, 18)
        ).scaled(0, 1);

        this.caveNoise = (x, y, z) -> {
            float baseNoise = caveNoiseBase.noise(x, z);
            if (baseNoise > 1f)
            {
                return caveNoiseWorley.noise(x, y, z);
            }
            else if (baseNoise < NOISE_THRESHOLD)
            {
                return 0;
            }
            return caveNoiseWorley.noise(x, y, z);
        };
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    public void carve(IChunk chunkIn, int chunkX, int chunkZ)
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
                            float heightFadeValue = yPos > HEIGHT_FADE_THRESHOLD ? 1 - 0.02f * (yPos - HEIGHT_FADE_THRESHOLD) : 1;

                            // Replacement state for cave interior based on height
                            BlockState replacementState = yPos <= LAVA_DEPTH ? LAVA : AIR;

                            for (int x0 = x * 4; x0 < (x + 1) * 4; x0++)
                            {
                                for (int z0 = z * 4; z0 < (z + 1) * 4; z0++)
                                {
                                    // set the current position
                                    pos.setPos(chunkX + x0, yPos, chunkZ + z0);

                                    float finalNoise = NoiseUtil.lerp(section[x0 + 16 * z0], prevSection[x0 + 16 * z0], 0.25f * y0);
                                    finalNoise *= heightFadeValue;

                                    if (finalNoise > NOISE_THRESHOLD)
                                    {
                                        // Create cave if possible
                                        BlockState originalState = chunkIn.getBlockState(pos);
                                        if (!originalState.isAir(chunkIn, pos) && originalState != BEDROCK && !originalState.getMaterial().isLiquid())
                                        {
                                            chunkIn.setBlockState(pos, replacementState, false);
                                        }
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
}
