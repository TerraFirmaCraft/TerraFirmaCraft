/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class HangingPlantBlock extends PlantBlock
{
    protected static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    protected static final VoxelShape NOT_HANGING_SHAPE = box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    public static HangingPlantBlock create(IPlant plant, ExtendedProperties properties)
    {
        return new HangingPlantBlock(properties)
        {
            @Override
            public IPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected HangingPlantBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        for (Direction direction : Direction.Plane.VERTICAL)
        {
            BlockState attach = level.getBlockState(currentPos.relative(direction));
            if (attach.getMaterial() == Material.LEAVES)
            {
                return state.setValue(HANGING, direction == Direction.UP);
            }
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        for (Direction direction : Direction.Plane.VERTICAL)
        {
            if (level.getBlockState(pos.relative(direction)).getMaterial() == Material.LEAVES)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        if (state.getValue(HANGING))
        {
            return super.getShape(state, level, pos, context);
        }
        else
        {
            return NOT_HANGING_SHAPE;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        if (context.getLevel().getBlockState(context.getClickedPos().relative(Direction.UP)).getMaterial() == Material.LEAVES)
        {
            return defaultBlockState().setValue(HANGING, true);
        }
        if (context.getLevel().getBlockState(context.getClickedPos().relative(Direction.DOWN)).getMaterial() == Material.LEAVES)
        {
            return defaultBlockState().setValue(HANGING, false);
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(HANGING);
    }
}
