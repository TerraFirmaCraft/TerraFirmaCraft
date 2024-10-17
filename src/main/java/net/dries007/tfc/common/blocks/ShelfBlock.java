/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.PlacedItemBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.devices.PlacedItemBlock;

public class ShelfBlock extends PlacedItemBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final VoxelShape TOP_SHAPE = box(0, 15, 0, 16, 16, 16);

    public ShelfBlock(ExtendedProperties properties)
    {
        super(properties, TOP_SHAPE, true);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        // Query for a shelf block entity, and don't allow conversion into pit kilns
        // Additionally, if we interact with the top of this block, then we actually want to simulate placing a placed item above the block,
        // not below.
        final boolean hitIsOnLowerHalf = hitResult.getLocation().y() - hitResult.getBlockPos().getY() < 0.5f;
        if (!hitIsOnLowerHalf && hitResult.getDirection() == Direction.UP)
        {
            // We hit the top of the block, so place a placed item above this block, if empty
            // N.B. We don't have to handle interacting with shelf or placed item, because if so, the model makes it impossible
            // for us to hit the top of a shelf below, without interacting with the shelf above
            final BlockPos above = pos.above();
            if (level.isEmptyBlock(above))
            {
                final BlockState toPlace = PlacedItemBlock.updateStateValues(level, pos, TFCBlocks.PLACED_ITEM.get().defaultBlockState());
                if (!PlacedItemBlock.isEmptyContents(toPlace))
                {
                    level.setBlockAndUpdate(above, toPlace);
                    level.getBlockEntity(above, TFCBlockEntities.PLACED_ITEM.get()).ifPresent(e -> e.insertItem(player, stack, hitResult));
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }

        // Interacting with the placed item in the bottom half of the block
        if (hitIsOnLowerHalf)
        {
            final PlacedItemBlockEntity placedItem = level.getBlockEntity(pos, TFCBlockEntities.SHELF.get()).orElse(null);
            if (placedItem != null && placedItem.onRightClick(player, player.getItemInHand(hand), hitResult))
            {
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return TOP_SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final Direction facing = state.getValue(FACING);
        final BlockPos facePos = pos.relative(facing);
        final BlockState faceState = level.getBlockState(facePos);
        return faceState.isFaceSturdy(level, facePos, facing.getOpposite());
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final BlockState state = defaultBlockState().setValue(FACING, context.getHorizontalDirection());
        return canSurvive(state, context.getLevel(), context.getClickedPos())
            ? PlacedItemBlock.updateStateValues(context.getLevel(), context.getClickedPos().below(), state)
            : null;
    }
}
