package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.chunkdata.ChunkData;

/**
 * Modified from {@link net.minecraft.world.gen.surfacebuilders.FrozenOceanSurfaceBuilder}
 */
public class FrozenUnderwaterSurfaceBuilder extends SeededSurfaceBuilder<SurfaceBuilderConfig> implements IContextSurfaceBuilder<SurfaceBuilderConfig>
{
    private PerlinNoiseGenerator icebergNoise;
    private PerlinNoiseGenerator icebergRoofNoise;

    public FrozenUnderwaterSurfaceBuilder(Codec<SurfaceBuilderConfig> codec)
    {
        super(codec);
    }

    @Override
    public void apply(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config)
    {
        throw new UnsupportedOperationException("GlacierSurfaceBuilder must be used with a chunk generator which supports IContextSurfaceBuilder!");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void applyWithContext(IWorld worldIn, ChunkData chunkData, Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double surfaceNoise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config)
    {
        final BlockState packedIce = Blocks.PACKED_ICE.defaultBlockState();
        final BlockState snowBlock = Blocks.SNOW_BLOCK.defaultBlockState();

        double icebergMaxY = 0.0D;
        double icebergMinY = 0.0D;

        final BlockPos.Mutable mutablePos = new BlockPos.Mutable().set(x, startHeight, z);
        final float maxAnnualTemperature = Climate.calculateMonthlyAverageTemperature(z, TFCChunkGenerator.SEA_LEVEL, chunkData.getAverageTemp(mutablePos), 1);

        double thresholdTemperature = -1f;
        double cutoffTemperature = 3f;
        double icebergValue = Math.min(Math.abs(surfaceNoise), icebergNoise.getValue(x * 0.1D, z * 0.1D, false) * 15.0D);
        icebergValue += (thresholdTemperature - maxAnnualTemperature) * 0.2f;
        if (maxAnnualTemperature > thresholdTemperature)
        {
            icebergValue *= Math.max(0, (cutoffTemperature - maxAnnualTemperature) / (cutoffTemperature - thresholdTemperature));
        }
        if (icebergValue > 1.8D)
        {
            final double icebergRoofValue = Math.abs(icebergRoofNoise.getValue(x * 0.09765625D, z * 0.09765625D, false));
            final double maxIcebergRoofValue = Math.ceil(icebergRoofValue * 40.0D) + 14.0D;

            icebergMaxY = icebergValue * icebergValue * 1.2D;
            if (icebergMaxY > maxIcebergRoofValue)
            {
                icebergMaxY = maxIcebergRoofValue;
            }

            if (icebergMaxY > 2.0D)
            {
                icebergMinY = (double) seaLevel - icebergMaxY - 7.0D;
                icebergMaxY = icebergMaxY + (double) seaLevel;
            }
            else
            {
                icebergMaxY = 0.0D;
            }
        }

        final int localX = x & 15;
        final int localZ = z & 15;
        final SurfaceBuilderConfig underwaterConfig = TFCSurfaceBuilders.UNDERWATER.get().getUnderwaterConfig(x, z, seed);

        BlockState underState = underwaterConfig.getUnderMaterial();
        BlockState topState = underwaterConfig.getTopMaterial();
        int normalSurfaceDepth = (int) (surfaceNoise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        int surfaceFlag = -1;
        int currentSnowLayers = 0;
        int maximumSnowLayers = 2 + random.nextInt(4);
        int minimumSnowY = seaLevel + 18 + random.nextInt(10);

        for (int y = Math.max(startHeight, (int) icebergMaxY + 1); y >= 0; --y)
        {
            mutablePos.set(localX, y, localZ);
            if (chunkIn.getBlockState(mutablePos).isAir() && y < (int) icebergMaxY && random.nextDouble() > 0.01D)
            {
                chunkIn.setBlockState(mutablePos, packedIce, false);
            }
            else if (chunkIn.getBlockState(mutablePos).getMaterial() == Material.WATER && y > (int) icebergMinY && y < seaLevel && icebergMinY != 0.0D && random.nextDouble() > 0.15D)
            {
                chunkIn.setBlockState(mutablePos, packedIce, false);
            }

            BlockState stateAt = chunkIn.getBlockState(mutablePos);
            if (stateAt.isAir())
            {
                surfaceFlag = -1;
            }
            else if (!stateAt.is(defaultBlock.getBlock()))
            {
                if (stateAt.is(Blocks.PACKED_ICE) && currentSnowLayers <= maximumSnowLayers && y > minimumSnowY)
                {
                    chunkIn.setBlockState(mutablePos, snowBlock, false);
                    ++currentSnowLayers;
                }
            }
            else if (surfaceFlag == -1)
            {
                if (normalSurfaceDepth <= 0)
                {
                    topState = Blocks.AIR.defaultBlockState();
                    underState = defaultBlock;
                }
                else if (y >= seaLevel - 4 && y <= seaLevel + 1)
                {
                    topState = underwaterConfig.getTopMaterial();
                    underState = underwaterConfig.getUnderMaterial();
                }

                if (y < seaLevel && topState.isAir())
                {
                    topState = defaultFluid;
                }

                surfaceFlag = normalSurfaceDepth;
                if (y >= seaLevel - 1)
                {
                    chunkIn.setBlockState(mutablePos, topState, false);
                }
                else
                {
                    chunkIn.setBlockState(mutablePos, underState, false);
                }
            }
            else if (surfaceFlag > 0)
            {
                --surfaceFlag;
                chunkIn.setBlockState(mutablePos, underState, false);
            }
        }
    }

    @Override
    protected void initSeed(long seed)
    {
        SharedSeedRandom random = new SharedSeedRandom(seed);
        this.icebergNoise = new PerlinNoiseGenerator(random, IntStream.rangeClosed(-3, 0));
        this.icebergRoofNoise = new PerlinNoiseGenerator(random, ImmutableList.of(0));
    }
}
