/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BuoyBlockEntity;
import net.dries007.tfc.common.blockentities.LobsterTrapBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class BuoyBlock extends ExtendedBlock implements EntityBlockExtension
{
    private static final VoxelShape SHAPE = box(6, 0, 6, 10, 10, 10);

    public BuoyBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final FluidState below = level.getFluidState(pos.below());
        return below.isSource() && below.is(TFCTags.Fluids.INFINITE_WATER) && level.getFluidState(pos).isEmpty();
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return facing == Direction.DOWN && !canSurvive(state, level, currentPos)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        final BuoyBlockEntity buoy = level.getBlockEntity(pos, TFCBlockEntities.BUOY.get()).orElse(null);
        if (buoy == null)
        {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (buoy.isTrapDisconnected())
        {
            if (!level.isClientSide)
            {
                buoy.releaseLine(player);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!buoy.hasTrap() && Helpers.isItem(stack, TFCBlocks.LOBSTER_TRAP.get().asItem()) && BuoyBlockEntity.canTrapOccupy(level, pos.below()))
        {
            if (!level.isClientSide)
            {
                final BlockPos trapPos = pos.below();
                final BlockState trap = FluidHelpers.fillWithFluid(TFCBlocks.LOBSTER_TRAP.get().defaultBlockState().setValue(LobsterTrapBlock.FACING, player.getDirection().getOpposite()), level.getFluidState(trapPos).getType());
                if (trap != null)
                {
                    level.setBlockAndUpdate(trapPos, trap);
                    if (level.getBlockEntity(trapPos) instanceof LobsterTrapBlockEntity entity)
                    {
                        entity.applyComponentsFromItemStack(stack);
                    }

                    Helpers.playPlaceSound(null, level, trapPos, trap);
                    stack.consume(1, player);
                    buoy.attachTrap();
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (Helpers.isItem(stack, TFCItems.ROPE.get()) && buoy.isTrapConnected())
        {
            if (!level.isClientSide)
            {
                stack.consume(buoy.sinkTrap(stack.getCount()), player);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return stack.isEmpty() ? ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION : ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        final BuoyBlockEntity buoy = level.getBlockEntity(pos, TFCBlockEntities.BUOY.get()).orElse(null);
        if (buoy != null && (buoy.isTrapDisconnected() || (buoy.isTrapConnected() && buoy.getRopes() > 0)))
        {
            if (!level.isClientSide)
            {
                if (buoy.isTrapDisconnected())
                {
                    buoy.releaseLine(player);
                }
                else
                {
                    buoy.recallTrap(player);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!Helpers.isBlock(state, newState.getBlock()) && level.getBlockEntity(pos) instanceof BuoyBlockEntity buoy)
        {
            buoy.dropContents();
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }
}
