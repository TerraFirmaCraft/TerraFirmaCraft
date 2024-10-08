/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.util.TriState;

import net.dries007.tfc.common.TFCTags;

public class TFCBambooStalkBlock extends BambooStalkBlock
{
    private final Supplier<? extends Block> sapling;

    public TFCBambooStalkBlock(Properties properties, Supplier<? extends Block> sapling)
    {
        super(properties);
        this.sapling = sapling;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        if (!fluidstate.isEmpty())
        {
            return null;
        }
        else
        {
            final BlockState state = context.getLevel().getBlockState(context.getClickedPos().below());
            final TriState soilDecision = state.canSustainPlant(context.getLevel(), context.getClickedPos().below(), Direction.UP, this.defaultBlockState());
            if (soilDecision.isDefault())
            {
                if (!state.is(BlockTags.BAMBOO_PLANTABLE_ON))
                {
                    return null;
                }
            }
            else if (!soilDecision.isTrue())
            {
                return null;
            }

            if (state.is(TFCTags.Blocks.BAMBOO_SAPLING))
            {
                return this.defaultBlockState().setValue(AGE, 0);
            }
            else if (state.is(TFCTags.Blocks.BAMBOO))
            {
                int i = state.getValue(AGE) > 0 ? 1 : 0;
                return this.defaultBlockState().setValue(AGE, i);
            }
            else
            {
                final BlockState aboveState = context.getLevel().getBlockState(context.getClickedPos().above());
                return aboveState.is(TFCTags.Blocks.BAMBOO) ? this.defaultBlockState().setValue(AGE, aboveState.getValue(AGE)) : sapling.get().defaultBlockState();
            }
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        if (!state.canSurvive(level, pos))
        {
            level.scheduleTick(pos, this, 1);
        }
        if (direction == Direction.UP && neighborState.is(TFCTags.Blocks.BAMBOO) && neighborState.getValue(AGE) > state.getValue(AGE))
        {
            level.setBlock(pos, state.cycle(AGE), 2);
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void growBamboo(BlockState state, Level level, BlockPos pos, RandomSource random, int age)
    {
        final BlockState belowState = level.getBlockState(pos.below());
        final BlockPos belowPos2 = pos.below(2);
        final BlockState belowState2 = level.getBlockState(belowPos2);
        BambooLeaves leafState = BambooLeaves.NONE;
        if (age >= 1)
        {
            if (belowState.is(TFCTags.Blocks.BAMBOO) && belowState.getValue(LEAVES) != BambooLeaves.NONE)
            {
                if (belowState.is(TFCTags.Blocks.BAMBOO) && belowState.getValue(LEAVES) != BambooLeaves.NONE)
                {
                    leafState = BambooLeaves.LARGE;
                    if (belowState2.is(TFCTags.Blocks.BAMBOO))
                    {
                        level.setBlock(pos.below(), belowState.setValue(LEAVES, BambooLeaves.SMALL), 3);
                        level.setBlock(belowPos2, belowState2.setValue(LEAVES, BambooLeaves.NONE), 3);
                    }
                }
            }
            else
            {
                leafState = BambooLeaves.SMALL;
            }
        }

        final int newAge = state.getValue(AGE) != 1 && !belowState2.is(TFCTags.Blocks.BAMBOO) ? 0 : 1;
        final int newStage = (age < 11 || !(random.nextFloat() < 0.25F)) && age != 15 ? 0 : 1;
        level.setBlock(pos.above(), this.defaultBlockState().setValue(AGE, newAge).setValue(LEAVES, leafState).setValue(STAGE, newStage), 3);
    }

    @Override
    protected int getHeightAboveUpToMax(BlockGetter level, BlockPos pos)
    {
        int i;
        for (i = 0; i < 16 && level.getBlockState(pos.above(i + 1)).is(TFCTags.Blocks.BAMBOO); ++i) { }
        return i;
    }

    @Override
    protected int getHeightBelowUpToMax(BlockGetter level, BlockPos pos)
    {
        int i;
        for (i = 0; i < 16 && level.getBlockState(pos.below(i + 1)).is(TFCTags.Blocks.BAMBOO); ++i) { }
        return i;
    }
}
