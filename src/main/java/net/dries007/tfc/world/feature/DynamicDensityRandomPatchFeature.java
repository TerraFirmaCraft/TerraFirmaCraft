/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;

import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ForestType;

public class DynamicDensityRandomPatchFeature extends Feature<RandomPatchConfiguration>
{
    public DynamicDensityRandomPatchFeature(Codec<RandomPatchConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<RandomPatchConfiguration> context)
    {
        final RandomPatchConfiguration config = context.config();
        final RandomSource random = context.random();
        final BlockPos pos = context.origin();
        final WorldGenLevel level = context.level();
        int placed = 0;
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final int width = config.xzSpread() + 1;
        final int height = config.ySpread() + 1;

        final int tries = getTries(context, level, pos, config.tries());
        for (int i = 0; i < tries; ++i)
        {
            mutablePos.setWithOffset(pos, random.nextInt(width) - random.nextInt(width), random.nextInt(height) - random.nextInt(height), random.nextInt(width) - random.nextInt(width));
            if (config.feature().value().place(level, context.chunkGenerator(), random, mutablePos))
            {
                ++placed;
            }
        }

        return placed > 0;
    }

    private int getTries(FeaturePlaceContext<RandomPatchConfiguration> context, WorldGenLevel level, BlockPos pos, int tries)
    {
        final ChunkData data = ChunkData.get(level, pos);
        final ForestType forestType = data.getForestType();

        final int seaLevel = context.chunkGenerator().getSeaLevel();
        if (pos.getY() > seaLevel + 25)
        {
            tries *= 1f - Mth.clampedMap(pos.getY(), seaLevel + 25, seaLevel + 100, 0f, 0.8f);
        }
        if (forestType == ForestType.OLD_GROWTH)
        {
            tries = Math.min(tries, 8);
        }
        else if (forestType == ForestType.NORMAL)
        {
            tries = Math.min(tries, 14);
        }
        else if (forestType == ForestType.EDGE)
        {
            tries = Math.min(tries, 40);
        }
        if (context.chunkGenerator() instanceof TFCChunkGenerator generator)
        {
            tries *= generator.settings().grassDensity() * 2f;
        }

        return tries;
    }
}
