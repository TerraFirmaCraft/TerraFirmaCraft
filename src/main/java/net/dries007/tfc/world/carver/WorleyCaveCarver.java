/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.carver;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.WorldCarver;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.noise.*;

public class WorleyCaveCarver extends WorldCarver<WorleyCaveConfig> implements IContextCarver
{
    private final AirBlockCarver blockCarver;

    private long cachedSeed;
    private INoise3D caveNoiseWorley;
    private boolean initialized;

    public WorleyCaveCarver(Codec<WorleyCaveConfig> codec)
    {
        super(codec, 255);

        blockCarver = new AirBlockCarver();
        cachedSeed = 0;
        initialized = false;
    }

    @Override
    public void setContext(long worldSeed, BitSet airCarvingMask, BitSet liquidCarvingMask, RockData rockData, @Nullable BitSet waterAdjacencyMask)
    {
        if (this.cachedSeed != worldSeed || !initialized)
        {
            caveNoiseWorley = new Cellular3D(worldSeed + 2, 2.0f, CellularNoiseType.F1_MUL_F2).spread(0.04f).warped(
                new OpenSimplex3D(worldSeed + 3).octaves(4).spread(0.08f).scaled(-18, 18),
                new OpenSimplex3D(worldSeed + 4).octaves(4).spread(0.08f).scaled(-18, 18),
                new OpenSimplex3D(worldSeed + 5).octaves(4).spread(0.08f).scaled(-18, 18)
            ).scaled(0, 1);

            cachedSeed = worldSeed;
            initialized = true;
        }

        this.blockCarver.setContext(worldSeed, airCarvingMask, liquidCarvingMask, rockData, waterAdjacencyMask);
    }

    @Override
    public boolean carve(IChunk chunkIn, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, WorleyCaveConfig config)
    {
        // This carver is entirely noise based, so we need to only carve chunks when we're at the start chunk
        if (chunkX == chunkXOffset && chunkZ == chunkZOffset)
        {
            if (!initialized)
            {
                throw new IllegalStateException("Not properly initialized! Cannot use WorleyCaveCarver with a chunk generator that does not respect IContextCarver");
            }
            carve(chunkIn, chunkX << 4, chunkZ << 4, rand, seaLevel, config);
            return true;
        }
        return false;
    }

    @Override
    public boolean isStartChunk(Random rand, int chunkX, int chunkZ, WorleyCaveConfig config)
    {
        return true;
    }

    @Override
    protected boolean skip(double distX, double distY, double distZ, int posY)
    {
        return false; // Unused
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private void carve(IChunk chunkIn, int chunkX, int chunkZ, Random random, int seaLevel, WorleyCaveConfig config)
    {
        final int heightSampleRange = (config.heightFadeThreshold / 4) + 8;
        final float[] noiseValues = new float[5 * 5 * heightSampleRange];

        // Sample initial noise values
        for (int x = 0; x < 5; x++)
        {
            for (int z = 0; z < 5; z++)
            {
                for (int y = 0; y < heightSampleRange; y++)
                {
                    noiseValues[x + (z * 5) + (y * 25)] = caveNoiseWorley.noise((float) (chunkX + x * 4), y * 7f, (float) (chunkZ + z * 4));
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
                            float heightFadeValue = yPos > config.heightFadeThreshold ? 1 - 0.03f * (float) (yPos - config.heightFadeThreshold) : 1;
                            for (int x0 = x * 4; x0 < (x + 1) * 4; x0++)
                            {
                                for (int z0 = z * 4; z0 < (z + 1) * 4; z0++)
                                {
                                    // set the current position
                                    pos.set(chunkX + x0, yPos, chunkZ + z0);

                                    float finalNoise = NoiseUtil.lerp(section[x0 + 16 * z0], prevSection[x0 + 16 * z0], 0.25f * y0);
                                    finalNoise *= heightFadeValue;

                                    if (finalNoise > config.carvingThreshold)
                                    {
                                        blockCarver.carve(chunkIn, pos, random, seaLevel);
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