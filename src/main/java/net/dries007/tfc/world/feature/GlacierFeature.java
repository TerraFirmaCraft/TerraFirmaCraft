package net.dries007.tfc.world.feature;

import java.util.Random;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

public class GlacierFeature extends Feature<NoFeatureConfig>
{
    private long cachedSeed;
    private boolean initialized;

    private INoise2D glacierNoise;

    public GlacierFeature(Codec<NoFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean place(ISeedReader worldIn, ChunkGenerator chunkGenerator, Random random, BlockPos pos, NoFeatureConfig config)
    {
        initSeed(worldIn.getSeed());

        final ChunkPos chunkPos = new ChunkPos(pos);
        final ChunkDataProvider provider = ChunkDataProvider.getOrThrow(chunkGenerator);
        final ChunkData data = provider.get(chunkPos, ChunkData.Status.CLIMATE);
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        final BlockState packedIce = Blocks.PACKED_ICE.defaultBlockState();
        final BlockState snowBlock = Blocks.SNOW_BLOCK.defaultBlockState();
        final BlockState snow = Blocks.SNOW.defaultBlockState();

        for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); x++)
        {
            for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); z++)
            {
                mutablePos.set(x, worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z) - 1, z);

                // Find the world surface
                BlockState stateAt = worldIn.getBlockState(mutablePos);
                while (stateAt.isAir() || stateAt.getBlock() instanceof ILeavesBlock)
                {
                    mutablePos.move(0, -1, 0);
                    stateAt = worldIn.getBlockState(mutablePos);
                }

                boolean inOcean = stateAt.getFluidState().getType() == Fluids.WATER && mutablePos.getY() <= chunkGenerator.getSeaLevel();
                mutablePos.move(0, 1, 0); // Start at the top of the world surface

                float maxTemperature = Climate.calculateMonthlyTemperature(z, mutablePos.getY(), data.getAverageTemp(mutablePos), 1);
                if (maxTemperature > -4)
                {
                    // Summers are too warm to generate glaciers
                    continue;
                }

                float glacierHeight = glacierNoise.noise(x, z) + MathHelper.clamp(-0.2f * maxTemperature, 0, 4);
                if (glacierHeight < 0)
                {
                    continue; // No glacier here
                }

                // Place the full glacier
                int snowHeight = MathHelper.ceil(0.2f * glacierHeight);
                int packedIceHeight = MathHelper.floor(glacierHeight - snowHeight);
                int snowLayers = MathHelper.clamp((int) (7 * MathHelper.frac(glacierHeight) - 1), 1, 7);

                for (int i = 0; i < packedIceHeight; i++)
                {
                    worldIn.setBlock(mutablePos, packedIce, 3);
                    mutablePos.move(0, 1, 0);
                }
                for (int i = 0; i < snowHeight; i++)
                {
                    worldIn.setBlock(mutablePos, snowBlock, 3);
                    mutablePos.move(0, 1, 0);
                }
                if (snowLayers > 0)
                {
                    worldIn.setBlock(mutablePos, snow.setValue(SnowBlock.LAYERS, snowLayers), 3);
                }
                if (inOcean)
                {
                    int totalHeight = packedIceHeight + snowHeight;
                    mutablePos.move(0, -totalHeight, 0);
                    for (int i = 0; i < totalHeight * 0.8f; i++)
                    {
                        mutablePos.move(0, -1, 0);
                        if (!worldIn.isWaterAt(mutablePos))
                        {
                            break;
                        }
                        worldIn.setBlock(mutablePos, packedIce, 3);
                    }
                }
            }
        }
        return true;
    }

    @VisibleForTesting
    public void initSeed(long seed)
    {
        if (seed != cachedSeed || !initialized)
        {
            glacierNoise = new SimplexNoise2D(seed + 1)
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

            cachedSeed = seed;
            initialized = true;
        }
    }

    @VisibleForTesting
    public INoise2D getGlacierNoise()
    {
        return glacierNoise;
    }
}
