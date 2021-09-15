/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public class BananaSaplingBlock extends FruitTreeSaplingBlock
{
    private final Lifecycle[] stages;

    public BananaSaplingBlock(ExtendedProperties properties, Lifecycle[] stages, Supplier<? extends Block> block, int treeGrowthDays)
    {
        super(properties, block, treeGrowthDays);

        this.stages = stages;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        return InteractionResult.FAIL;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random)
    {
        if (stages[Calendars.SERVER.getCalendarMonthOfYear().ordinal()] == Lifecycle.HEALTHY)
        {
            TickCounterBlockEntity te = Helpers.getBlockEntity(world, pos, TickCounterBlockEntity.class);
            if (te != null)
            {
                if (te.getTicksSinceUpdate() > (long) ICalendar.TICKS_IN_DAY * treeGrowthDays)
                {
                    world.setBlockAndUpdate(pos, block.get().defaultBlockState());
                }
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.below()).is(TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {

    }
}
