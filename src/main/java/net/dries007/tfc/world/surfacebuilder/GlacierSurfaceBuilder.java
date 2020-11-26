package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public class GlacierSurfaceBuilder extends SeededSurfaceBuilder<ParentedSurfaceBuilderConfig> implements IContextSurfaceBuilder<ParentedSurfaceBuilderConfig>
{
    private INoise2D glacierNoise;

    public GlacierSurfaceBuilder(Codec<ParentedSurfaceBuilderConfig> codec)
    {
        super(codec);
    }

    @Override
    public void apply(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, ParentedSurfaceBuilderConfig config)
    {
        throw new UnsupportedOperationException("GlacierSurfaceBuilder must be used with a chunk generator which supports IContextSurfaceBuilder!");
    }

    @Override
    public void applyWithContext(ChunkData chunkData, Biome biomeIn, Random random, IChunk chunkIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, ParentedSurfaceBuilderConfig config)
    {
        final BlockState packedIce = Blocks.PACKED_ICE.defaultBlockState();
        final BlockState snowBlock = Blocks.SNOW_BLOCK.defaultBlockState();
        final BlockState snowLayer = Blocks.SNOW.defaultBlockState();

        if (startHeight <= 150) // Maximum height for glaciers to generate at
        {
            final BlockPos.Mutable pos = new BlockPos.Mutable().set(x, TFCChunkGenerator.SEA_LEVEL, z);
            final float maxTemperature = Climate.calculateMonthlyTemperature(z, TFCChunkGenerator.SEA_LEVEL, chunkData.getAverageTemp(pos), 1);
            if (maxTemperature < 0)
            {
                float glacierHeight = glacierNoise.noise(x, z) + MathHelper.clamp(-0.2f * maxTemperature, 0, 4);
                if (glacierHeight > 0)
                {
                    // Scale glacier height by y level, to force glaciers into valleys
                    if (startHeight > 120)
                    {
                        glacierHeight *= (150d - startHeight) / 30d;
                    }

                    // Place glaciers
                    int snowHeight = MathHelper.ceil(0.2f * glacierHeight);
                    int packedIceHeight = MathHelper.floor(glacierHeight - snowHeight);
                    int snowLayers = MathHelper.clamp((int) (7 * MathHelper.frac(glacierHeight) - 1), 1, 7);

                    // Start height will be one above the top layer
                    pos.set(x, startHeight, z);
                    for (int i = 0; i < packedIceHeight; i++)
                    {
                        chunkIn.setBlockState(pos, packedIce, false);
                        pos.move(0, 1, 0);
                    }
                    for (int i = 0; i < snowHeight; i++)
                    {
                        chunkIn.setBlockState(pos, snowBlock, false);
                        pos.move(0, 1, 0);
                    }
                    if (snowLayers > 0)
                    {
                        chunkIn.setBlockState(pos, snowLayer.setValue(SnowBlock.LAYERS, snowLayers), false);
                    }

                    pos.set(x, startHeight - 1, z);
                    BlockState stateDown = chunkIn.getBlockState(pos);
                    if (stateDown.getFluidState().is(FluidTags.WATER))
                    {
                        // Fill water
                        int totalHeight = packedIceHeight + snowHeight;
                        for (int i = 0; i < totalHeight * 0.8f; i++)
                        {
                            if (!chunkIn.getBlockState(pos).getFluidState().is(FluidTags.WATER))
                            {
                                break;
                            }
                            chunkIn.setBlockState(pos, packedIce, false);
                            pos.move(0, -1, 0);
                        }
                        TFCSurfaceBuilders.applySurfaceBuilder(TFCSurfaceBuilders.UNDERWATER.get(), random, chunkIn, biomeIn, x, z, pos.getY(), noise, defaultBlock, defaultFluid, seaLevel, seed, config);
                    }
                    // Directly underneath glaciers should stay as raw rock
                    return;
                }
            }
        }

        // Reached here, so we delegate to the parent
        TFCSurfaceBuilders.applyIfPresent(config.getParent(), random, chunkData, chunkIn, biomeIn, x, z, startHeight, noise, seed, defaultBlock, defaultFluid, seaLevel);
    }

    @Override
    protected void initSeed(long seed)
    {
        glacierNoise = new OpenSimplex2D(seed + 1)
            .octaves(4)
            .spread(0.02f)
            .map(x -> {
                // Scale the glacier with large height gains in the middle of the noise range
                if (x < -0.2)
                {
                    return -4 + (x + 0.2f) * 10f / 0.8f;
                }
                else if (x > 0.2)
                {
                    return 4 + (x - 0.2f) * 10f / 0.8f;
                }
                else
                {
                    return x * 4 / 0.2f;
                }
            });
    }
}
