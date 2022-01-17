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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MultiBlock implements BiPredicate<LevelAccessor, BlockPos>
{
    protected final List<BiPredicate<LevelAccessor, BlockPos>> conditions;

    public MultiBlock()
    {
        this.conditions = new ArrayList<>();
    }

    public MultiBlock match(BlockPos posOffset, BiPredicate<LevelAccessor, BlockPos> condition)
    {
        conditions.add((world, pos) -> condition.test(world, pos.offset(posOffset)));
        return this;
    }

    public MultiBlock match(BlockPos posOffset, Predicate<BlockState> stateMatcher)
    {
        conditions.add((world, pos) -> stateMatcher.test(world.getBlockState(pos.offset(posOffset))));
        return this;
    }

    public MultiBlock matchEachDirection(BlockPos posOffset, BiPredicate<LevelAccessor, BlockPos> condition, Direction[] directions, int relativeAmount)
    {
        for (Direction d : directions)
        {
            conditions.add((world, pos) -> condition.test(world, pos.offset(posOffset).relative(d, relativeAmount)));
        }
        return this;
    }

    public MultiBlock matchHorizontal(BlockPos posOffset, BiPredicate<LevelAccessor, BlockPos> condition, int relativeAmount)
    {
        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            conditions.add((world, pos) -> condition.test(world, pos.offset(posOffset).relative(d, relativeAmount)));
        }
        return this;
    }

    public <T extends BlockEntity> MultiBlock match(BlockPos posOffset, Predicate<T> tileEntityPredicate, BlockEntityType<T> type)
    {
        conditions.add((world, pos) -> world.getBlockEntity(pos.offset(posOffset), type).map(tileEntityPredicate::test).orElse(false));
        return this;
    }

    public MultiBlock matchOneOf(BlockPos baseOffset, MultiBlock subMultiBlock)
    {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        conditions.add((world, pos) -> {
            for (BiPredicate<LevelAccessor, BlockPos> condition : subMultiBlock.conditions)
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
    public boolean test(LevelAccessor world, BlockPos pos)
    {
        for (BiPredicate<LevelAccessor, BlockPos> condition : conditions)
        {
            if (!condition.test(world, pos))
            {
                return false;
            }
        }
        return true;
    }
}
