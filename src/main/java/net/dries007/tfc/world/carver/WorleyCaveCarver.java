/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.carver;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.noise.*;

public class WorleyCaveCarver
{
    private final INoise3D caveNoise;
    private final double heightFadeThreshold;
    private final int heightSampleRange;
    private final double baseNoiseCutoff;
    private final double worleyNoiseCutoff;
    private final CaveBlockReplacer blockCarver;

    public WorleyCaveCarver(Random seedGenerator)
    {
        INoise2D caveNoiseBase = new SimplexNoise2D(seedGenerator.nextLong()).spread(0.01f).scaled(0, 1);
        INoise3D caveNoiseWorley = new WorleyNoise3D(seedGenerator.nextLong()).spread(0.012f).warped(
            new SimplexNoise3D(seedGenerator.nextLong()).octaves(4).spread(0.08f).scaled(-18, 18),
            new SimplexNoise3D(seedGenerator.nextLong()).octaves(4).spread(0.08f).scaled(-18, 18),
            new SimplexNoise3D(seedGenerator.nextLong()).octaves(4).spread(0.08f).scaled(-18, 18)
        ).scaled(0, 1);

        // The y level to start slowly fading out worley caves, to not rip up the surface too much
        this.heightFadeThreshold = TFCConfig.COMMON.worleyCaveHeightFade.get();
        // The number of vertical samples to take. Noise is sampled every 4 blocks, then interpolated
        // +8 is to sample above the height fade, so it's actually a fade and not a cutoff
        this.heightSampleRange = (int) (heightFadeThreshold / 4) + 8;
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

        this.blockCarver = new CaveBlockReplacer();
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    public void carve(IChunk chunkIn, int chunkX, int chunkZ, BitSet carvingMask)
    {
        float[] noiseValues = new float[5 * 5 * heightSampleRange];

        // Sample initial noise values
        for (int x = 0; x < 5; x++)
        {
            for (int z = 0; z < 5; z++)
            {
                for (int y = 0; y < heightSampleRange; y++)
                {
                    noiseValues[x + (z * 5) + (y * 25)] = caveNoise.noise(chunkX + x * 4, y * 7.3f, chunkZ + z * 4);
                }
            }
        }

        float[] section = new float[16 * 16];
        float[] prevSection = null;
        BlockPos.Mutable pos = new BlockPos.Mutable();

        // Create caves, layer by layer
        for (int y = heightSampleRange - 1; y >= 0; y--)
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
                            float heightFadeValue = yPos > heightFadeThreshold ? 1 - 0.03f * (float) (yPos - heightFadeThreshold) : 1;
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
                                        blockCarver.carveBlock(chunkIn, pos, carvingMask);
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
