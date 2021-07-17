/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
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
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(AXIS));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!worldIn.isClientSide() && worldIn instanceof World)
        {
            if (facingState.is(BlockTags.FIRE))
            {
                BurningLogPileBlock.tryLightLogPile((World) worldIn, currentPos);
            }
        }
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
    {
        if (!player.isShiftKeyDown())
        {
            final ItemStack stack = player.getItemInHand(hand);
            final LogPileTileEntity te = Helpers.getTileEntity(world, pos, LogPileTileEntity.class);
            if (stack.getItem().is(TFCTags.Items.LOG_PILE_LOGS))
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
                        return ActionResultType.SUCCESS;
                    }
                    return ActionResultType.FAIL;
                }).orElse(ActionResultType.PASS);
            }
            else
            {
                if (player instanceof ServerPlayerEntity)
                {
                    NetworkHooks.openGui((ServerPlayerEntity) player, te, pos);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
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
