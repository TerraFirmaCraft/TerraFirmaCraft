/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.item.ItemComponent;

public class FlowerCuttingItem extends Item
{
    public static ItemStack of(ItemStack plant)
    {
        final ItemStack cutting = new ItemStack(TFCItems.FLOWER_CUTTING);
        cutting.set(TFCComponents.PLANT, new ItemComponent(plant.copyWithCount(1)));
        return cutting;
    }

    public FlowerCuttingItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        final ItemStack plant = stack.getOrDefault(TFCComponents.PLANT, ItemComponent.EMPTY).stack();
        if (!plant.isEmpty())
        {
            tooltip.add(Component.translatable("tfc.tooltip.flower_cutting", plant.getHoverName()));
        }
    }
}
