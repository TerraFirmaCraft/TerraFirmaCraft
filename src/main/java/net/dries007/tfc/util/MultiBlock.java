/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class MultiBlock implements BiPredicate<IWorld, BlockPos>
{
    private final List<BiPredicate<IWorld, BlockPos>> conditions;

    public MultiBlock()
    {
        this.conditions = new ArrayList<>();
    }

    public MultiBlock match(BlockPos posOffset, BiPredicate<IWorld, BlockPos> condition)
    {
        conditions.add((world, pos) -> condition.test(world, pos.offset(posOffset)));
        return this;
    }

    public MultiBlock match(BlockPos posOffset, Predicate<BlockState> stateMatcher)
    {
        conditions.add((world, pos) -> stateMatcher.test(world.getBlockState(pos.offset(posOffset))));
        return this;
    }

    public MultiBlock matchEachDirection(BlockPos posOffset, BiPredicate<IWorld, BlockPos> condition, Direction[] directions, int relativeAmount)
    {
        for (Direction d : directions)
        {
            conditions.add((world, pos) -> condition.test(world, pos.offset(posOffset).relative(d, relativeAmount)));
        }
        return this;
    }

    public MultiBlock matchHorizontal(BlockPos posOffset, BiPredicate<IWorld, BlockPos> condition, int relativeAmount)
    {
        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            conditions.add((world, pos) -> condition.test(world, pos.offset(posOffset).relative(d, relativeAmount)));
        }
        return this;
    }

    public <T extends TileEntity> MultiBlock match(BlockPos posOffset, Predicate<T> tileEntityPredicate, Class<T> teClass)
    {
        conditions.add((world, pos) -> {
            T tile = Helpers.getTileEntity(world, pos.offset(posOffset), teClass);
            if (tile != null)
            {
                return tileEntityPredicate.test(tile);
            }
            return false;
        });
        return this;
    }

    public MultiBlock matchOneOf(BlockPos baseOffset, MultiBlock subMultiBlock)
    {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        conditions.add((world, pos) -> {
            for (BiPredicate<IWorld, BlockPos> condition : subMultiBlock.conditions)
            {
                if (condition.test(world, mutable.set(pos).move(baseOffset)))
                {
                    return true;
                }
            }
            return false;
        });
        return this;
    }

    @Override
    public boolean test(IWorld world, BlockPos pos)
    {
        for (BiPredicate<IWorld, BlockPos> condition : conditions)
        {
            if (!condition.test(world, pos))
            {
                return false;
            }
        }
        return true;
    }
}
