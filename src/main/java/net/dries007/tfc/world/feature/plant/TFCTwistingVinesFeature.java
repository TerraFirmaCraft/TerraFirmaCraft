/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;

public class TFCTwistingVinesFeature extends Feature<ColumnPlantConfig>
{
    public static void placeColumn(LevelAccessor level, RandomSource rand, BlockPos.MutableBlockPos mutablePos, int height, int minAge, int maxAge, BlockState body, BlockState head)
    {
        for (int i = 1; i <= height; ++i)
        {
            if (EnvironmentHelpers.isWorldgenReplaceable((WorldGenLevel) level, mutablePos))
            {
                mutablePos.move(0, 1, 0);
                if (i == height || !level.isEmptyBlock(mutablePos))
                {
                    mutablePos.move(0, -1, 0);
                    level.setBlock(mutablePos, head.setValue(GrowingPlantHeadBlock.AGE, Mth.nextInt(rand, minAge, maxAge)), 2);
                    break;
                }
                mutablePos.move(0, -1, 0);
                level.setBlock(mutablePos, body, 2);
            }
            mutablePos.move(0, 1, 0);
        }
    }

    public TFCTwistingVinesFeature(Codec<ColumnPlantConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<ColumnPlantConfig> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final RandomSource rand = context.random();
        final ColumnPlantConfig config = context.config();

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        boolean placedAny = false;
        int radius = config.radius();
        for (int i = 0; i < config.tries(); i++)
        {
            mutablePos.setWithOffset(pos, Helpers.triangle(rand, radius), 0, Helpers.triangle(rand, radius));

            mutablePos.move(0, -1, 0);
            if (!Helpers.isBlock(level.getBlockState(mutablePos), TFCTags.Blocks.GRASS_PLANTABLE_ON)) return false;
            mutablePos.move(0, 1, 0);

            if (EnvironmentHelpers.isWorldgenReplaceable(level, mutablePos))
            {
                placeColumn(level, rand, mutablePos, rand.nextInt(config.maxHeight() - config.minHeight()) + config.minHeight(), 17, 25, config.bodyState(), config.headState());
                placedAny = true;
            }
        }
        return placedAny;
    }
}
