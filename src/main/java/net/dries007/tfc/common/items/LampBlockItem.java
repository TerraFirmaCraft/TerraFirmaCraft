/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.ItemStackFluidHandler;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.LampFuel;
import net.dries007.tfc.util.loot.CopyFluidFunction;
import org.jetbrains.annotations.Nullable;

public class LampBlockItem extends BlockItem
{
    public LampBlockItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new ItemStackFluidHandler(stack, fluid -> LampFuel.get(fluid, getBlock().defaultBlockState()) != null, TFCConfig.SERVER.lampCapacity.get());
    }

    @Override
    public String getDescriptionId(ItemStack stack)
    {
        return getFluidInside(stack).isEmpty() ? super.getDescriptionId(stack) : super.getDescriptionId(stack) + ".filled";
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state)
    {
        boolean flag = super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        CopyFluidFunction.copyFromItem(stack, level.getBlockEntity(pos, TFCBlockEntities.LAMP.get()).orElse(null));
        return flag;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        FluidStack fluid = getFluidInside(stack);
        if (!fluid.isEmpty())
        {
            Helpers.addFluidStackTooltipInfo(fluid, tooltip);
        }
    }

    private FluidStack getFluidInside(ItemStack stack)
    {
        return stack.getCapability(Capabilities.FLUID_ITEM).map(cap -> cap.getFluidInTank(0)).orElse(FluidStack.EMPTY);
    }
}
