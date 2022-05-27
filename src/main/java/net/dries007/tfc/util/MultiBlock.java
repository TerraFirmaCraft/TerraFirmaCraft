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
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * An API for programmatically checking a list of positions, returning true if all the predicates succeed.
 */
public class MultiBlock implements BiPredicate<LevelAccessor, BlockPos>
{
    protected final List<BiPredicate<LevelAccessor, BlockPos>> conditions;

    public MultiBlock()
    {
        this.conditions = new ArrayList<>();
    }

    MultiBlock(List<BiPredicate<LevelAccessor, BlockPos>> conditions)
    {
        this.conditions = conditions;
    }

    /**
     * @return A fresh MultiBlock instance with all the conditions of the original
     */
    public MultiBlock copy()
    {
        return new MultiBlock(new ArrayList<>(conditions));
    }

    public MultiBlock match(BlockPos posOffset, TagKey<Block> tagMatch)
    {
        return match(posOffset, (level, pos) -> Helpers.isBlock(level.getBlockState(pos), tagMatch));
    }

    public MultiBlock match(BlockPos posOffset, Predicate<BlockState> stateMatcher)
    {
        return match(posOffset, (level, pos) -> stateMatcher.test(level.getBlockState(pos)));
    }

    public <T extends BlockEntity> MultiBlock match(BlockPos posOffset, Predicate<T> blockEntityMatcher, BlockEntityType<T> type)
    {
        return match(posOffset, (level, pos) -> level.getBlockEntity(pos, type).map(blockEntityMatcher::test).orElse(false));
    }

    public MultiBlock match(BlockPos posOffset, BiPredicate<LevelAccessor, BlockPos> condition)
    {
        conditions.add((level, pos) -> condition.test(level, pos.offset(posOffset)));
        return this;
    }

    public MultiBlock matchEachDirection(BlockPos posOffset, BiPredicate<LevelAccessor, BlockPos> condition, Direction[] directions, int relativeAmount)
    {
        for (Direction d : directions)
        {
            conditions.add((level, pos) -> condition.test(level, pos.offset(posOffset).relative(d, relativeAmount)));
        }
        return this;
    }

    public MultiBlock matchHorizontal(BlockPos posOffset, BiPredicate<LevelAccessor, BlockPos> condition, int relativeAmount)
    {
        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            conditions.add((level, pos) -> condition.test(level, pos.offset(posOffset).relative(d, relativeAmount)));
        }
        return this;
    }

    public MultiBlock matchOneOf(BlockPos baseOffset, MultiBlock subMultiBlock)
    {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        conditions.add((level, pos) -> {
            for (BiPredicate<LevelAccessor, BlockPos> condition : subMultiBlock.conditions)
            {
                if (condition.test(level, mutable.set(pos).move(baseOffset)))
                {
                    return true;
                }
            }
            return false;
        });
        return this;
    }

    @Override
    public boolean test(LevelAccessor level, BlockPos pos)
    {
        for (BiPredicate<LevelAccessor, BlockPos> condition : conditions)
        {
            if (!condition.test(level, pos))
            {
                return false;
            }
        }
        return true;
    }
}
