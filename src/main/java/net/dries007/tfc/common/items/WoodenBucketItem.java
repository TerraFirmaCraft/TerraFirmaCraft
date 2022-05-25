/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.ItemStackFluidHandler;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class WoodenBucketItem extends Item
{
    private final Supplier<Integer> capacity;

    public WoodenBucketItem(Properties properties, Supplier<Integer> capacity)
    {
        super(properties);
        this.capacity = capacity;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        final ItemStack stack = player.getItemInHand(hand);
        final IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve().orElse(null);
        if (handler == null)
        {
            return InteractionResultHolder.pass(stack);
        }
        if (handler.getFluidInTank(0).isEmpty())
        {
            final BlockHitResult hit = Helpers.rayTracePlayer(level, player, ClipContext.Fluid.SOURCE_ONLY);
            if (hit.getType() == HitResult.Type.BLOCK)
            {
                final BlockPos pos = hit.getBlockPos();
                final BlockState state = level.getBlockState(pos);
                if (FluidHelpers.pickupFluidInto(level, pos, state, player, handler))
                {
                    return InteractionResultHolder.success(stack);
                }
                final IFluidHandler blockHandler = FluidHelpers.getBlockEntityFluidHandler(level, pos, state);
                if (blockHandler != null && FluidHelpers.transferExact(blockHandler, handler, getCapacity()))
                {
                    return InteractionResultHolder.success(stack);
                }
            }
            return afterFillFailed(handler, level, player, stack, hand);
        }
        else
        {
            final BlockHitResult hit = Helpers.rayTracePlayer(level, player, ClipContext.Fluid.SOURCE_ONLY);
            if (hit.getType() == HitResult.Type.BLOCK)
            {
                final BlockPos pos = hit.getBlockPos();
                final BlockState state = level.getBlockState(pos);
                final IFluidHandler blockHandler = FluidHelpers.getBlockEntityFluidHandler(level, pos, state);
                if (blockHandler != null && FluidHelpers.transferExact(handler, blockHandler, getCapacity()))
                {
                    return InteractionResultHolder.success(stack);
                }
                if (emptyContents(handler, level, pos, state, hit))
                {
                    return InteractionResultHolder.sidedSuccess(player.isCreative() ? stack : new ItemStack(this), level.isClientSide);
                }
            }
            return afterEmptyFailed(handler, level, player, stack, hand);
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new ItemStackFluidHandler(stack, getWhitelistTag(), getCapacity());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        FluidStack fluid = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(cap -> cap.getFluidInTank(0)).orElse(FluidStack.EMPTY);
        if (!fluid.isEmpty()) Helpers.addFluidStackTooltipInfo(fluid, tooltip);
    }

    protected TagKey<Fluid> getWhitelistTag()
    {
        return TFCTags.Fluids.USABLE_IN_WOODEN_BUCKET;
    }

    private int getCapacity()
    {
        return capacity.get();
    }

    protected InteractionResultHolder<ItemStack> afterFillFailed(IFluidHandler handler, Level level, Player player, ItemStack stack, InteractionHand hand)
    {
        return InteractionResultHolder.pass(stack);
    }

    protected InteractionResultHolder<ItemStack> afterEmptyFailed(IFluidHandler handler, Level level, Player player, ItemStack stack, InteractionHand hand)
    {
        return InteractionResultHolder.pass(stack);
    }

    /**
     * Follows this logic without interacting with fluid logging or containers
     * {@link net.minecraft.world.item.BucketItem#emptyContents(Player, Level, BlockPos, BlockHitResult)}
     */
    protected boolean emptyContents(IFluidHandler handler, Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit)
    {
        Fluid fluid = handler.getFluidInTank(0).getFluid();
        if (state.isAir() || state.canBeReplaced(fluid))
        {
            if (level.dimensionType().ultraWarm() && Helpers.isFluid(fluid, FluidTags.WATER))
            {
                Helpers.playSound(level, pos, SoundEvents.FIRE_EXTINGUISH);
                return true;
            }
            else
            {
                if (!level.isClientSide && !state.getMaterial().isLiquid())
                {
                    level.destroyBlock(pos, true);
                }

                BlockState toPlace = fluid.defaultFluidState().createLegacyBlock();
                if (!TFCConfig.SERVER.enableSourcesFromWoodenBucket.get())
                {
                    toPlace = toPlace.setValue(LiquidBlock.LEVEL, 2);
                }

                if (!level.setBlock(pos, toPlace, 11))
                {
                    return false;
                }
                else
                {
                    Helpers.playSound(level, pos, SoundEvents.BUCKET_EMPTY);
                    return true;
                }
            }
        }
        else if (hit != null)
        {
            BlockPos newPos = pos.relative(hit.getDirection());
            return emptyContents(handler, level, newPos, level.getBlockState(newPos), null);
        }
        return false;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(cap -> !cap.getFluidInTank(0).isEmpty()).orElse(false);
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack)
    {
        return new ItemStack(this);
    }
}
