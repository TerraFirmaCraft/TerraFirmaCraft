/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.capabilities.size.IItemSize;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.common.capabilities.size.Weight;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class BarrelBlock extends DeviceBlock implements IItemSize
{
    public static final BooleanProperty SEALED = TFCBlockStateProperties.SEALED;

    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 16, 14);

    public static void toggleSeal(Level level, BlockPos pos, BlockState state)
    {
        level.getBlockEntity(pos, TFCBlockEntities.BARREL.get()).ifPresent(barrel -> {
            final boolean previousSealed = state.getValue(SEALED);
            level.setBlockAndUpdate(pos, state.setValue(SEALED, !previousSealed));
            if (previousSealed)
            {
                barrel.onUnseal();
            }
            else
            {
                barrel.onSeal();
            }
        });
    }

    public BarrelBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.SAVE);

        registerDefaultState(getStateDefinition().any().setValue(SEALED, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        final BarrelBlockEntity barrel = level.getBlockEntity(pos, TFCBlockEntities.BARREL.get()).orElse(null);
        if (barrel != null)
        {
            final ItemStack stack = player.getItemInHand(hand);
            if (stack.isEmpty() && player.isShiftKeyDown())
            {
                toggleSeal(level, pos, state);
                level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 0.85f);
                return InteractionResult.SUCCESS;
            }
            else if (barrel.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                .map(cap -> FluidUtil.interactWithFluidHandler(player, hand, cap))
                .orElse(false))
            {
                return InteractionResult.SUCCESS;
            }
            else if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer)
            {
                NetworkHooks.openGui(serverPlayer, barrel, barrel.getBlockPos());
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return context.getItemInHand().getTag() != null ? defaultBlockState().setValue(SEALED, true) : defaultBlockState();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag)
    {
        final CompoundTag tag = stack.getTagElement(Helpers.BLOCK_ENTITY_TAG);
        if (tag != null)
        {
            // Decode the contents of the barrel
            final CompoundTag inventoryTag = tag.getCompound("inventory");
            final CompoundTag tankTag = tag.getCompound("tank");
            final ItemStackHandler inventory = new ItemStackHandler();
            final FluidTank tank = new FluidTank(TFCConfig.SERVER.barrelCapacity.get());

            inventory.deserializeNBT(inventoryTag.getCompound("inventory"));
            tank.readFromNBT(tankTag);

            tooltip.add(new TranslatableComponent("tfc.tooltip.contents").withStyle(ChatFormatting.DARK_GREEN));
            Helpers.addInventoryTooltipInfo(inventory, tooltip);
            Helpers.addFluidStackTooltipInfo(tank.getFluid(), tooltip);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(SEALED));
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return stack.getTag() == null ? Size.VERY_LARGE : Size.HUGE;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.VERY_HEAVY;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        final ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        if (state.getValue(SEALED))
        {
            final BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof InventoryBlockEntity<?> inv)
            {
                inv.saveToItem(stack);
            }
        }
        return stack;
    }

    @Override
    protected void beforeRemove(InventoryBlockEntity<?> entity)
    {
        if (!entity.getBlockState().getValue(SEALED))
        {
            entity.ejectInventory();
        }
        entity.invalidateCapabilities();
    }
}
