/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Tooltips;

public class FluidContainerItem extends Item
{
    protected final TagKey<Fluid> whitelist;
    protected final Supplier<Integer> capacity;

    private final boolean canPlaceLiquidsInWorld;
    private final boolean canPlaceSourceBlocks;

    public FluidContainerItem(Properties properties, Supplier<Integer> capacity, TagKey<Fluid> whitelist, boolean canPlaceLiquidsInWorld, boolean canPlaceSourceBlocks)
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
        final BlockHitResult hit = Helpers.rayTracePlayer(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (FluidHelpers.transferBetweenWorldAndItem(stack, level, hit, player, hand, canPlaceLiquidsInWorld, canPlaceSourceBlocks(), false))
        {
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        // Fallback behavior
        final @Nullable IFluidHandler handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler == null)
        {
            return InteractionResultHolder.pass(stack);
        }
        if (handler.getFluidInTank(0).isEmpty())
        {
            return afterFillFailed(handler, level, player, stack, hand);
        }
        else
        {
            return afterEmptyFailed(handler, level, player, stack, hand);
        }
    }

    @Override
    public Component getName(ItemStack stack)
    {
        final FluidStack fluid = FluidHelpers.getContainedFluid(stack);
        if (!fluid.isEmpty())
        {
            return Component.translatable(getDescriptionId(stack) + ".filled", fluid.getHoverName());
        }
        return super.getName(stack);
    }

    @Override
    public int getMaxStackSize(ItemStack stack)
    {
        // We cannot just query the stack size to see if it has a contained fluid, as that would be self-referential
        // So we have to query a handler that *would* return a capability here, which means copying with stack size = 1
        return FluidHelpers.getContainedFluid(stack.copyWithCount(1)).isEmpty() ? super.getMaxStackSize(stack) : 1;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag)
    {
        final FluidStack fluid = FluidHelpers.getContainedFluid(stack);
        if (!fluid.isEmpty())
        {
            tooltip.add(Tooltips.fluidUnitsAndCapacityOf(fluid, capacity.get()));
        }
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack)
    {
        return new ItemStack(this);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack)
    {
        return !FluidHelpers.getContainedFluid(stack).isEmpty();
    }

    public boolean canPlaceSourceBlocks()
    {
        return canPlaceSourceBlocks && TFCConfig.SERVER.enableBucketsPlacingSources.get();
    }

    public boolean canPlaceLiquidsInWorld()
    {
        return canPlaceLiquidsInWorld;
    }

    protected InteractionResultHolder<ItemStack> afterFillFailed(IFluidHandler handler, Level level, Player player, ItemStack stack, InteractionHand hand)
    {
        return InteractionResultHolder.pass(stack);
    }

    protected InteractionResultHolder<ItemStack> afterEmptyFailed(IFluidHandler handler, Level level, Player player, ItemStack stack, InteractionHand hand)
    {
        return InteractionResultHolder.pass(stack);
    }
}
