/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.util.Helpers;

public class CharcoalPileBlock extends Block
{
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
    public static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[] {
        Shapes.empty(),
        box(0, 0, 0, 16, 2, 16),
        box(0, 0, 0, 16, 4, 16),
        box(0, 0, 0, 16, 6, 16),
        box(0, 0, 0, 16, 8, 16),
        box(0, 0, 0, 16, 10, 16),
        box(0, 0, 0, 16, 12, 16),
        box(0, 0, 0, 16, 14, 16),
        Shapes.block()
    };

    public CharcoalPileBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        playerWillDestroy(level, pos, state, player);

        if (player.isCreative())
        {
            return level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }

        int prevLayers = state.getValue(LAYERS);
        if (prevLayers == 1)
        {
            return level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
        return level.setBlock(pos, state.setValue(LAYERS, prevLayers - 1), level.isClientSide ? 11 : 3);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        return new ItemStack(Items.CHARCOAL);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type)
    {
        return type == PathComputationType.LAND && state.getValue(LAYERS) < 5;
    }

    @Override
    protected BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (!level.isClientSide() && facing == Direction.DOWN)
        {
            if (Helpers.isBlock(facingState, this))
            {
                int layersAt = stateIn.getValue(LAYERS);
                int layersUnder = facingState.getValue(LAYERS);
                if (layersUnder < 8)
                {
                    if (layersUnder + layersAt <= 8)
                    {
                        level.setBlock(facingPos, facingState.setValue(LAYERS, layersAt + layersUnder), 3);
                        level.destroyBlock(currentPos, false); // Have to destroy the block to prevent it from dropping an additional charcoal
                        return Blocks.AIR.defaultBlockState();
                    }
                    else
                    {
                        level.setBlock(facingPos, facingState.setValue(LAYERS, 8), 3);
                        return stateIn.setValue(LAYERS, layersAt + layersUnder - 8);
                    }
                }
            }
        }
        return canSurvive(stateIn, level, currentPos) ? stateIn : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state)
    {
        return true;
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter reader, BlockPos pos)
    {
        return SHAPE_BY_LAYER[state.getValue(LAYERS)];
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockState blockstate = level.getBlockState(pos.below());
        return Block.isFaceFull(blockstate.getCollisionShape(level, pos.below()), Direction.UP) || (blockstate.getBlock() == this && blockstate.getValue(LAYERS) == 8);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE_BY_LAYER[state.getValue(LAYERS)];
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE_BY_LAYER[state.getValue(LAYERS) - 1];
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context)
    {
        return SHAPE_BY_LAYER[state.getValue(LAYERS)];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(LAYERS));
    }
}
