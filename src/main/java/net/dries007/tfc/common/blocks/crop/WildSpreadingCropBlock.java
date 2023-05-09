/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.HorizontalPipeBlock;
import net.dries007.tfc.util.Helpers;

public class WildSpreadingCropBlock extends WildCropBlock implements HorizontalPipeBlock
{
    @Nullable
    public static BlockState updateBlockState(Level level, BlockPos pos, @Nullable BlockState state, Block fruit)
    {
        if (state != null)
        {
            for (Direction dir : Direction.Plane.HORIZONTAL)
            {
                state = state.setValue(PROPERTY_BY_DIRECTION.get(dir), level.getBlockState(pos.relative(dir)).getBlock() == fruit);
            }
        }
        return state;
    }

    private final Supplier<Supplier<? extends Block>> fruit;

    public WildSpreadingCropBlock(ExtendedProperties properties, Supplier<Supplier<? extends Block>> fruit)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(NORTH, false).setValue(WEST, false).setValue(EAST, false).setValue(SOUTH, false));
        this.fruit = fruit;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return updateBlockState(context.getLevel(), context.getClickedPos(), super.getStateForPlacement(context), fruit.get().get());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        state = super.updateShape(state, facing, facingState, level, pos, facingPos);
        return Helpers.setProperty(state, PROPERTY_BY_DIRECTION.get(facing), facingState.getBlock() == fruit.get().get());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(NORTH, SOUTH, EAST, WEST));
    }

    public Block getFruit()
    {
        return fruit.get().get();
    }
}
