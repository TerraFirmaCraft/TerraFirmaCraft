/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.function.Supplier;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.DiscreteItemStackFluidHandler;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class DiscreteFluidContainerItem extends Item
{
    private final TagKey<Fluid> whitelist;
    private final Supplier<Integer> capacity;
    private final boolean canPlaceLiquidsInWorld;
    private final boolean canPlaceSourceBlocks;

    public DiscreteFluidContainerItem(Properties properties, Supplier<Integer> capacity, TagKey<Fluid> whitelist, boolean canPlaceSourceBlocks)
    {
        this(properties, capacity, whitelist, true, canPlaceSourceBlocks);
    }

    protected DiscreteFluidContainerItem(Properties properties, Supplier<Integer> capacity, TagKey<Fluid> whitelist, boolean canPlaceLiquidsInWorld, boolean canPlaceSourceBlocks)
    {
        super(properties);

        this.capacity = capacity;
        this.whitelist = whitelist;
        this.canPlaceLiquidsInWorld = canPlaceLiquidsInWorld;
        this.canPlaceSourceBlocks = canPlaceSourceBlocks;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        final ItemStack stack = player.getItemInHand(hand);
        final IFluidHandler handler = Helpers.getCapability(stack, Capabilities.FLUID_ITEM);
        if (handler == null)
        {
            return InteractionResultHolder.pass(stack);
        }

        final BlockHitResult hit = Helpers.rayTracePlayer(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (FluidHelpers.transferBetweenWorldAndItem(stack, level, hit, player, hand, canPlaceLiquidsInWorld, canPlaceSourceBlocks, false))
        {
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        // Fallback behavior
        if (handler.getFluidInTank(0).isEmpty())
        {
            return afterFillFailed(handler, level, player, stack, hand);
        }
        else
        {
            return afterEmptyFailed(handler, level, player, stack, hand);
        }
    }

    protected InteractionResultHolder<ItemStack> afterFillFailed(IFluidHandler handler, Level level, Player player, ItemStack stack, InteractionHand hand)
    {
        return InteractionResultHolder.pass(stack);
    }

    protected InteractionResultHolder<ItemStack> afterEmptyFailed(IFluidHandler handler, Level level, Player player, ItemStack stack, InteractionHand hand)
    {
        return InteractionResultHolder.pass(stack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new DiscreteItemStackFluidHandler(stack, whitelist, capacity.get());
    }

    @Override
    public Component getName(ItemStack stack)
    {
        final FluidStack fluid = stack.getCapability(Capabilities.FLUID_ITEM)
            .map(cap -> cap.getFluidInTank(0))
            .orElse(FluidStack.EMPTY);
        if (!fluid.isEmpty())
        {
            return fluid.getDisplayName().copy().append(" ").append(super.getName(stack));
        }
        return super.getName(stack);
    }

    @Override
    public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items)
    {
        if (allowdedIn(category))
        {
            items.add(new ItemStack(this)); // Empty
            for (Fluid fluid : Helpers.getAllTagValues(whitelist, ForgeRegistries.FLUIDS))
            {
                if (fluid instanceof FlowingFluid flowing && flowing.getSource() != flowing)
                {
                    // Don't create buckets filled with flowing versions of fluids
                    continue;
                }

                final ItemStack stack = new ItemStack(this);
                stack.getCapability(Capabilities.FLUID_ITEM).ifPresent(c -> c.fill(new FluidStack(fluid, FluidHelpers.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE));
                items.add(stack);
            }
        }
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return stack.getCapability(Capabilities.FLUID_ITEM).map(cap -> !cap.getFluidInTank(0).isEmpty()).orElse(false);
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack)
    {
        return new ItemStack(this);
    }
}
