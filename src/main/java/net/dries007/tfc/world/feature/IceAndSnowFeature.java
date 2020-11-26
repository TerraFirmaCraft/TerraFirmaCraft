package net.dries007.tfc.world.feature;

import java.util.Random;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyDirtBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public class IceAndSnowFeature extends Feature<NoFeatureConfig>
{
    private long cachedSeed;
    private boolean initialized;

    private INoise2D temperatureNoise;

    public IceAndSnowFeature(Codec<NoFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean place(ISeedReader worldIn, ChunkGenerator chunkGenerator, Random random, BlockPos pos, NoFeatureConfig config)
    {
        initSeed(worldIn.getSeed());

        // Since this feature may be run *both* during world generation, and after during climate updates, we need to query both the existing data, and fallback to the world gen data if empty.
        final ChunkPos chunkPos = new ChunkPos(pos);
        final ChunkData chunkData = ChunkData.get(worldIn, chunkPos).ifEmptyGet(() -> ChunkDataProvider.getOrThrow(chunkGenerator).get(chunkPos, ChunkData.Status.CLIMATE));
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        final BlockState snowState = Blocks.SNOW.defaultBlockState();

        for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); x++)
        {
            for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); z++)
            {
                mutablePos.set(x, worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, x, z), z);

                final float temperature = Climate.calculateTemperature(mutablePos, chunkData.getAverageTemp(mutablePos), Calendars.SERVER) + temperatureNoise.noise(x, z);
                final Biome biome = worldIn.getBiome(mutablePos);

                final BlockState stateAt = worldIn.getBlockState(mutablePos);
                if (stateAt.isAir() && snowState.canSurvive(worldIn, mutablePos))
                {
                    if (temperature < 0)
                    {
                        worldIn.setBlock(mutablePos, Blocks.SNOW.defaultBlockState(), 2);

                        mutablePos.move(0, -1, 0);
                        BlockState blockstate = worldIn.getBlockState(mutablePos);
                        if (blockstate.hasProperty(SnowyDirtBlock.SNOWY))
                        {
                            worldIn.setBlock(mutablePos, blockstate.setValue(SnowyDirtBlock.SNOWY, true), 2);
                        }
                    }
                }
                // todo: avoid calling biome.shouldFreeze
                // todo: handle not completely freezing oceans
                //else if (biome.shouldFreeze(worldIn, mutablePos, true))
                //{
                //    worldIn.setBlock(mutablePos, Blocks.ICE.defaultBlockState(), 2);
                //}
            }
        }

        return false;
    }

    @VisibleForTesting
    public void initSeed(long seed)
    {
        if (seed != cachedSeed || !initialized)
        {
            temperatureNoise = new OpenSimplex2D(seed)
                .octaves(2)
                .spread(0.3f)
                .scaled(-2, 2);

            cachedSeed = seed;
            initialized = true;
        }
    }
}
