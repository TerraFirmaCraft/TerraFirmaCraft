/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;

public class TFCWeepingVinesFeature extends Feature<TallPlantConfig>
{
    public TFCWeepingVinesFeature(Codec<TallPlantConfig> codec)
    {
        super(codec);
    }

    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, TallPlantConfig config)
    {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        boolean placedAny = false;
        int radius = config.getRadius();
        for (int i = 0; i < config.getTries(); i++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(radius) - rand.nextInt(radius), rand.nextInt(14) - rand.nextInt(6), rand.nextInt(radius) - rand.nextInt(radius));
            mutablePos.move(Direction.UP);
            BlockState aboveState = world.getBlockState(mutablePos);
            mutablePos.move(Direction.DOWN);
            if ((aboveState.isIn(BlockTags.LEAVES) || aboveState.isIn(BlockTags.LOGS) || aboveState.isIn(BlockTags.BASE_STONE_OVERWORLD)) && world.isEmptyBlock(mutablePos))
            {
                placeColumn(world, rand, mutablePos, rand.nextInt(config.getMaxHeight() - config.getMinHeight()) + config.getMinHeight(), 17, 25, config.getBodyState(), config.getHeadState());
                placedAny = true;
            }
        }
        return placedAny;
    }

    // This code is copied from WeepingVineFeature
    private static void placeColumn(IWorld world, Random rand, BlockPos.Mutable mutablePos, int height, int minAge, int maxAge, BlockState bodyState, BlockState headState)
    {
        for (int i = 0; i <= height; ++i)//this assumes that we found a valid place to attach
        {
            if (world.isEmptyBlock(mutablePos))//if it's empty, we can grow
            {
                if (i == height || !world.isEmptyBlock(mutablePos.down()))//if we guarantee the next iteration will fail, set the end block
                {
                    world.setBlockState(mutablePos, headState.with(AbstractTopPlantBlock.AGE, MathHelper.nextInt(rand, minAge, maxAge)), 2);
                    break;
                }
                world.setBlockState(mutablePos, bodyState, 2);
            }
            mutablePos.move(Direction.DOWN);
        }
    }
}
