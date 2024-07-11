/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.glass;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.util.Helpers;

public final class GlassWorking
{
    public static void addTooltipInfo(ItemStack stack, List<Component> tooltips)
    {
        final GlassOperations data = get(stack);
        if (!data.isEmpty())
        {
            tooltips.add(data.batch().getHoverName());
            if (!data.steps().isEmpty())
            {
                tooltips.add(Component.translatable("tfc.tooltip.glass.title").withStyle(ChatFormatting.AQUA));
                for (GlassOperation operation : data.steps())
                {
                    tooltips.add(Component.literal("- ").append(Helpers.translateEnum(operation)));
                }
            }
        }
    }

    public static GlassOperations get(ItemStack stack)
    {
        return stack.getOrDefault(TFCComponents.GLASS, GlassOperations.DEFAULT);
    }

    public static void clear(ItemStack stack)
    {
        stack.set(TFCComponents.GLASS, GlassOperations.DEFAULT);
    }

    public static void apply(ItemStack stack, GlassOperation operation)
    {
        if (!stack.isEmpty()) stack.set(TFCComponents.GLASS, get(stack).with(operation));
    }

    public static void createNewBatch(ItemStack stack, ItemStack glass)
    {
        if (!stack.isEmpty()) stack.set(TFCComponents.GLASS, get(stack).with(glass));
    }
}
