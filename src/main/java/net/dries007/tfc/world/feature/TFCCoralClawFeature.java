package net.dries007.tfc.world.feature;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.serialization.Codec;

public class TFCCoralClawFeature extends TFCCoralFeature
{
    public TFCCoralClawFeature(Codec<NoFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    protected boolean placeFeature(IWorld world, Random rand, BlockPos blockPos_, BlockState state)
    {
        if (!placeCoralBlock(world, rand, blockPos_, state))
        {
            return false;
        }
        else
        {
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(rand);
            int directionTries = rand.nextInt(2) + 2;
            List<Direction> dirs = Lists.newArrayList(direction, direction.getClockWise(), direction.getCounterClockWise());
            Collections.shuffle(dirs, rand);

            for (Direction d : dirs.subList(0, directionTries))
            {
                BlockPos.Mutable mutablePos = blockPos_.mutable();
                int j = rand.nextInt(2) + 1;
                mutablePos.move(d);
                int k;
                Direction direction2;
                if (d == direction)
                {
                    direction2 = direction;
                    k = rand.nextInt(3) + 2;
                }
                else
                {
                    mutablePos.move(Direction.UP);
                    Direction[] upOrSide = new Direction[] {d, Direction.UP};
                    direction2 = Util.getRandom(upOrSide, rand);
                    k = rand.nextInt(3) + 3;
                }

                for (int l = 0; l < j && placeCoralBlock(world, rand, mutablePos, state); ++l)
                {
                    mutablePos.move(direction2);
                }

                mutablePos.move(direction2.getOpposite());
                mutablePos.move(Direction.UP);

                for (int i1 = 0; i1 < k; ++i1)
                {
                    mutablePos.move(direction);
                    if (!placeCoralBlock(world, rand, mutablePos, state))
                    {
                        break;
                    }

                    if (rand.nextFloat() < 0.25F)
                    {
                        mutablePos.move(Direction.UP);
                    }
                }
            }

            return true;
        }
    }
}
