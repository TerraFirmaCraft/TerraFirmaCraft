/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.coral;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.serialization.Codec;

public class TFCCoralTreeFeature extends TFCCoralFeature
{
    public TFCCoralTreeFeature(Codec<NoFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    protected boolean placeFeature(IWorld world, Random rand, BlockPos pos, BlockState state)
    {
        BlockPos.Mutable mutablePos = pos.toMutable();
        int i = rand.nextInt(3) + 1;

        for (int j = 0; j < i; ++j)
        {
            if (!placeCoralBlock(world, rand, mutablePos, state))
            {
                return true;
            }
            mutablePos.move(Direction.UP);
        }

        BlockPos blockpos = mutablePos.toImmutable();
        int directionTries = rand.nextInt(3) + 2;
        List<Direction> dirs = Lists.newArrayList(Direction.Plane.HORIZONTAL);
        Collections.shuffle(dirs, rand);

        for (Direction d : dirs.subList(0, directionTries))
        {
            mutablePos.setPos(blockpos);
            mutablePos.move(d);
            int tries = rand.nextInt(5) + 2;
            int placedCoralBlocks = 0;

            for (int j = 0; j < tries && placeCoralBlock(world, rand, mutablePos, state); ++j)
            {
                ++placedCoralBlocks;
                mutablePos.move(Direction.UP);
                if (j == 0 || placedCoralBlocks >= 2 && rand.nextFloat() < 0.25F)
                {
                    mutablePos.move(d);
                    placedCoralBlocks = 0;
                }
            }
        }

        return true;
    }
}
