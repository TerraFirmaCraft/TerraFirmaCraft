/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.SnowPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class IceAndSnowFeature extends Feature<NoneFeatureConfiguration>
{
    private long cachedSeed;
    private boolean initialized;

    private Noise2D temperatureNoise;
    private Noise2D seaIceNoise;

    public IceAndSnowFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();

        initSeed(level.getSeed());
        final ChunkPos chunkPos = new ChunkPos(pos);
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        // Since this feature may be run *both* during world generation, and after during climate updates, we need to query both the existing data, and fallback to the world gen data if empty.
        ChunkData chunkData = ChunkData.get(level, chunkPos);
        if (chunkData.getStatus() == ChunkData.Status.EMPTY)
        {
            chunkData = ChunkDataProvider.get(context.chunkGenerator()).get(level, chunkPos);
        }

        final BlockState snowState = Blocks.SNOW.defaultBlockState();

        for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); x++)
        {
            for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); z++)
            {
                mutablePos.set(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), z);

                final float noise = temperatureNoise.noise(x, z);
                final float temperature = Climate.calculateTemperature(mutablePos, chunkData.getAverageTemp(mutablePos), Calendars.SERVER);
                final Biome biome = level.getBiome(mutablePos);

                BlockState stateAt = level.getBlockState(mutablePos);
                FluidState fluidAt;
                if (stateAt.isAir() && snowState.canSurvive(level, mutablePos))
                {
                    if (temperature + noise < 0)
                    {
                        level.setBlock(mutablePos, Blocks.SNOW.defaultBlockState(), 2);

                        mutablePos.move(0, -1, 0);
                        BlockState blockstate = level.getBlockState(mutablePos);
                        if (blockstate.hasProperty(SnowyDirtBlock.SNOWY))
                        {
                            level.setBlock(mutablePos, blockstate.setValue(SnowyDirtBlock.SNOWY, true), 2);
                        }
                        mutablePos.move(0, 1, 0);
                    }
                }
                else if (stateAt.is(TFCTags.Blocks.CAN_BE_SNOW_PILED))
                {
                    if (temperature + noise < 0)
                    {
                        SnowPileBlock.convertToPile(level, mutablePos, stateAt);
                    }
                }

                mutablePos.move(0, -1, 0);
                stateAt = level.getBlockState(mutablePos);
                fluidAt = stateAt.getFluidState();

                if (biome.shouldFreeze(level, mutablePos, true))
                {
                    level.setBlock(mutablePos, Blocks.ICE.defaultBlockState(), 2);
                }
                else if (fluidAt.getType() == TFCFluids.SALT_WATER.getSource())
                {
                    final float threshold = seaIceNoise.noise(x * 0.2f, z * 0.2f) + Mth.clamp(temperature * 0.1f, -0.2f, 0.2f);
                    if (temperature < Climate.SEA_ICE_FREEZE_TEMPERATURE && threshold < -0.4f)
                    {
                        level.setBlock(mutablePos, TFCBlocks.SEA_ICE.get().defaultBlockState(), 2);
                    }
                }
            }
        }
        return false;
    }

    @VisibleForTesting
    public void initSeed(long seed)
    {
        if (seed != cachedSeed || !initialized)
        {
            temperatureNoise = new OpenSimplex2D(seed).octaves(2).spread(0.3f).scaled(-2, 2);
            seaIceNoise = new OpenSimplex2D(seed + 1).octaves(3).spread(0.6f);

            cachedSeed = seed;
            initialized = true;
        }
    }
}
