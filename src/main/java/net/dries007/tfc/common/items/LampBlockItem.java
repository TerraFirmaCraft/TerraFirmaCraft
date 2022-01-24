/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.ItemStackFluidHandler;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.LampFuel;

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
        return getTankInfo(stack).isEmpty() ? super.getDescriptionId(stack) : super.getDescriptionId(stack) + ".filled";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        FluidTank tank = getTankInfo(stack);
        if (!tank.isEmpty()) Helpers.addFluidStackTooltipInfo(tank.getFluid(), tooltip);
    }

    private FluidTank getTankInfo(ItemStack stack)
    {
        FluidTank tank = new FluidTank(TFCConfig.SERVER.lampCapacity.get());
        final CompoundTag tag = stack.getTagElement(Helpers.BLOCK_ENTITY_TAG);
        if (tag != null && tag.contains("tank"))
        {
            tank.readFromNBT(tag.getCompound("tank"));
            return tank;
        }
        return tank;
    }
}
