/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.util.Helpers;

public class LogPileBlock extends DeviceBlock implements IForgeBlockExtension, EntityBlockExtension
{
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public LogPileBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        BurningLogPileBlock.lightLogPile(level, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(AXIS));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor levelAccess, BlockPos currentPos, BlockPos facingPos)
    {
        if (!levelAccess.isClientSide() && levelAccess instanceof Level level)
        {
            if (Helpers.isBlock(facingState, BlockTags.FIRE))
            {
                BurningLogPileBlock.lightLogPile(level, currentPos);
            }
        }
        return super.updateShape(state, facing, facingState, levelAccess, currentPos, facingPos);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (!player.isShiftKeyDown())
        {
            level.getBlockEntity(pos, TFCBlockEntities.LOG_PILE.get()).ifPresent(logPile -> {
                if (Helpers.isItem(stack.getItem(), TFCTags.Items.LOG_PILE_LOGS))
                {
                    if (!level.isClientSide)
                    {
                        if (Helpers.insertOne(logPile, stack))
                        {
                            Helpers.playPlaceSound(level, pos, state);
                            stack.shrink(1);
                        }
                    }
                }
                else
                {
                    if (player instanceof ServerPlayer serverPlayer)
                    {
                        serverPlayer.openMenu(logPile, pos);
                    }
                }
            });
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        return level.getBlockEntity(pos, TFCBlockEntities.LOG_PILE.get())
            .map(pile -> {
                for (int i = 0; i < pile.getInventory().getSlots(); i++)
                {
                    final ItemStack stack = pile.getInventory().getStackInSlot(i);
                    if (!stack.isEmpty())
                    {
                        return stack.copy();
                    }
                }
                return ItemStack.EMPTY;
            }).orElse(ItemStack.EMPTY);
    }
}
