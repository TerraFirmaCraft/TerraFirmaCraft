/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Helpers;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class TFCWeepingVinesFeature extends Feature<ColumnPlantConfig>
{
    // This code is copied from WeepingVineFeature
    private static void placeColumn(LevelAccessor level, Random rand, BlockPos.MutableBlockPos mutablePos, int height, int minAge, int maxAge, BlockState bodyState, BlockState headState)
    {
        for (int i = 0; i <= height; ++i)//this assumes that we found a valid place to attach
        {
            if (level.isEmptyBlock(mutablePos))//if it's empty, we can grow
            {
                if (i == height || !level.isEmptyBlock(mutablePos.below()))//if we guarantee the next iteration will fail, set the end block
                {
                    level.setBlock(mutablePos, headState.setValue(GrowingPlantHeadBlock.AGE, Mth.nextInt(rand, minAge, maxAge)), 2);
                    break;
                }
                level.setBlock(mutablePos, bodyState, 2);
            }
            mutablePos.move(Direction.DOWN);
        }
    }

    public TFCWeepingVinesFeature(Codec<ColumnPlantConfig> codec)
    {
        super(codec);
    }

    public boolean place(FeaturePlaceContext<ColumnPlantConfig> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final Random rand = context.random();
        final ColumnPlantConfig config = context.config();

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        boolean placedAny = false;
        int radius = config.radius();
        for (int i = 0; i < config.tries(); i++)
        {
            mutablePos.setWithOffset(pos, Helpers.triangle(rand, radius), Helpers.triangle(rand, 4) + 10, Helpers.triangle(rand, radius));
            mutablePos.move(Direction.UP);
            BlockState aboveState = level.getBlockState(mutablePos);
            mutablePos.move(Direction.DOWN);
            if ((Helpers.isBlock(aboveState, BlockTags.LEAVES) || Helpers.isBlock(aboveState, BlockTags.LOGS) || Helpers.isBlock(aboveState, BlockTags.BASE_STONE_OVERWORLD)) && level.isEmptyBlock(mutablePos))
            {
                placeColumn(level, rand, mutablePos, rand.nextInt(config.maxHeight() - config.minHeight()) + config.minHeight(), 17, 25, config.bodyState(), config.headState());
                placedAny = true;
            }
        }
        return placedAny;
    }
}
