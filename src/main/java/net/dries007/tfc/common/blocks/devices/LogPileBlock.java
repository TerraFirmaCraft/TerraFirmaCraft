/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.tileentity.LogPileTileEntity;
import net.dries007.tfc.util.Helpers;

public class LogPileBlock extends DeviceBlock implements IForgeBlockProperties
{
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public LogPileBlock(ForgeBlockProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(AXIS, Direction.Axis.X));
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
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!worldIn.isClientSide() && worldIn instanceof Level)
        {
            if (facingState.is(BlockTags.FIRE))
            {
                BurningLogPileBlock.tryLightLogPile((Level) worldIn, currentPos);
            }
        }
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (!player.isShiftKeyDown())
        {
            final ItemStack stack = player.getItemInHand(hand);
            final LogPileTileEntity te = Helpers.getTileEntity(world, pos, LogPileTileEntity.class);
            if (TFCTags.Items.LOG_PILE_LOGS.contains(stack.getItem()))
            {
                return Helpers.getCapability(te, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(cap -> {
                    ItemStack insertStack = stack.copy();
                    insertStack.setCount(1);
                    insertStack = Helpers.insertAllSlots(cap, insertStack);
                    if (insertStack.isEmpty())
                    {
                        if (!world.isClientSide)
                        {
                            Helpers.playSound(world, pos, SoundEvents.WOOD_PLACE);
                            stack.shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.FAIL;
                }).orElse(InteractionResult.PASS);
            }
            else
            {
                if (player instanceof ServerPlayer)
                {
                    NetworkHooks.openGui((ServerPlayer) player, te, pos);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player)
    {
        LogPileTileEntity te = Helpers.getTileEntity(world, pos, LogPileTileEntity.class);
        if (te != null)
        {
            return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .map(cap -> {
                    for (int i = 0; i < cap.getSlots(); i++)
                    {
                        final ItemStack stack = cap.getStackInSlot(i);
                        if (!stack.isEmpty())
                        {
                            return stack.copy();
                        }
                    }
                    return ItemStack.EMPTY;
                })
                .orElse(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }
}
