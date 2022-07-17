/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.ClimateRange;

public class SpreadingBushBlock extends StationaryBerryBushBlock implements IForgeBlockExtension, IBushBlock, HoeOverlayBlock
{
    protected final Supplier<? extends Block> companion;
    protected final int maxHeight;

    public SpreadingBushBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages, Supplier<? extends Block> companion, int maxHeight, Supplier<ClimateRange> climateRange)
    {
        super(properties, productItem, stages, climateRange);
        this.companion = companion;
        this.maxHeight = maxHeight;
        registerDefaultState(getStateDefinition().any().setValue(STAGE, 0));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(STAGE) == 2 ? Shapes.block() : PLANT_SHAPE;
    }

    @Override
    protected boolean mayPropagate(BlockState newState, Level level, BlockPos pos)
    {
        return newState.getValue(LIFECYCLE).active() && level.getRandom().nextInt(3) == 0;
    }

    @Override
    protected BlockState getDeadState(BlockState state)
    {
        return TFCBlocks.DEAD_BERRY_BUSH.get().defaultBlockState().setValue(STAGE, state.getValue(STAGE));
    }

    @Override
    protected void propagate(Level level, BlockPos pos, Random random, BlockState state)
    {
        final int stage = state.getValue(STAGE);
        final BlockPos abovePos = pos.above();
        if ((stage == 1 || (stage == 2 && level.random.nextInt(3) == 0)) && level.isEmptyBlock(abovePos) && distanceToGround(level, pos, maxHeight) < maxHeight)
        {
            level.setBlockAndUpdate(abovePos, state.setValue(STAGE, 1).setValue(LIFECYCLE, state.getValue(LIFECYCLE)));
        }
        else if (stage == 2)
        {
            final int count = Mth.nextInt(random, 1, 3);
            for (int i = 0; i < count; i++)
            {
                final Direction offset = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                final BlockPos offsetPos = pos.relative(offset);
                if (level.isEmptyBlock(offsetPos))
                {
                    level.setBlockAndUpdate(offsetPos, companion.get().defaultBlockState().setValue(SpreadingCaneBlock.FACING, offset).setValue(LIFECYCLE, state.getValue(LIFECYCLE)));
                }
            }
        }
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, List<Component> text, boolean isDebug)
    {
        final BlockPos sourcePos = pos.below();
        final ClimateRange range = climateRange.get();

        text.add(FarmlandBlock.getHydrationTooltip(level, sourcePos, range, false));
        text.add(FarmlandBlock.getTemperatureTooltip(level, sourcePos, range, false));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        return Helpers.isBlock(belowState, this) || this.mayPlaceOn(level.getBlockState(belowPos), level, belowPos);
    }
}
