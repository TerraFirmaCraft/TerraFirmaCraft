/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blockentities.LoomBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.util.Helpers;

public class TFCLoomBlock extends DeviceBlock
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final VoxelShape SHAPE_EAST = box(2, 0, 1, 9, 16, 15);
    private static final VoxelShape SHAPE_WEST = box(7, 0, 1, 14, 16, 15);
    private static final VoxelShape SHAPE_SOUTH = box(1, 0, 2, 15, 16, 9);
    private static final VoxelShape SHAPE_NORTH = box(1, 0, 7, 15, 16, 14);

    private final ResourceLocation woodTexture;

    public TFCLoomBlock(ExtendedProperties properties, ResourceLocation woodTexture)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        this.woodTexture = woodTexture;
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    public ResourceLocation getTextureLocation()
    {
        return woodTexture;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        switch (state.getValue(FACING))
        {
            case NORTH:
            default:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case WEST:
                return SHAPE_WEST;
            case EAST:
                return SHAPE_EAST;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        LoomBlockEntity te = Helpers.getBlockEntity(worldIn, pos, LoomBlockEntity.class);
        if (te != null)
        {
            return te.onRightClick(player);
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54794_)
    {
        p_54794_.add(FACING);
    }

}
