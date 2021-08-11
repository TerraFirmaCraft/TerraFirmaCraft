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
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

import com.mojang.serialization.Codec;

public class TFCWeepingVinesFeature extends Feature<TallPlantConfig>
{
    // This code is copied from WeepingVineFeature
    private static void placeColumn(LevelAccessor world, Random rand, BlockPos.MutableBlockPos mutablePos, int height, int minAge, int maxAge, BlockState bodyState, BlockState headState)
    {
        for (int i = 0; i <= height; ++i)//this assumes that we found a valid place to attach
        {
            if (world.isEmptyBlock(mutablePos))//if it's empty, we can grow
            {
                if (i == height || !world.isEmptyBlock(mutablePos.below()))//if we guarantee the next iteration will fail, set the end block
                {
                    world.setBlock(mutablePos, headState.setValue(GrowingPlantHeadBlock.AGE, Mth.nextInt(rand, minAge, maxAge)), 2);
                    break;
                }
                world.setBlock(mutablePos, bodyState, 2);
            }
            mutablePos.move(Direction.DOWN);
        }
    }

    public TFCWeepingVinesFeature(Codec<TallPlantConfig> codec)
    {
        super(codec);
    }

    public boolean place(WorldGenLevel world, ChunkGenerator generator, Random rand, BlockPos pos, TallPlantConfig config)
    {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        boolean placedAny = false;
        int radius = config.getRadius();
        for (int i = 0; i < config.getTries(); i++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(radius) - rand.nextInt(radius), rand.nextInt(14) - rand.nextInt(6), rand.nextInt(radius) - rand.nextInt(radius));
            mutablePos.move(Direction.UP);
            BlockState aboveState = world.getBlockState(mutablePos);
            mutablePos.move(Direction.DOWN);
            if ((aboveState.is(BlockTags.LEAVES) || aboveState.is(BlockTags.LOGS) || aboveState.is(BlockTags.BASE_STONE_OVERWORLD)) && world.isEmptyBlock(mutablePos))
            {
                placeColumn(world, rand, mutablePos, rand.nextInt(config.getMaxHeight() - config.getMinHeight()) + config.getMinHeight(), 17, 25, config.getBodyState(), config.getHeadState());
                placedAny = true;
            }
        }
        return placedAny;
    }
}
