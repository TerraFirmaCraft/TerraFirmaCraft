/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.util.Helpers;

public interface TooltipBlock
{
    /**
     * Returns the tooltip for an inventory consisting of the given {@code slots}, arranged in a grid of {@code width} by {@code height}.
     * The {@code slots} are presumed to be immutable, i.e. from an item stack tooltip, and no copy is performed here.
     */
    static Optional<TooltipComponent> buildInventoryTooltip(List<ItemStack> inventory, int width, int height)
    {
        if (inventory.isEmpty()) return Optional.empty(); // Only called generally on empty, so special case not returning any tooltip here
        assert inventory.size() == width * height; // Size should match what was provided by width x height otherwise
        return Helpers.isEmpty(inventory)
            ? Optional.empty()
            : Optional.of(new Instance(inventory, width, height));
    }

    default Optional<TooltipComponent> getTooltipImage(ItemStack stack)
    {
        return Optional.empty();
    }

    default int getBarWidth(ItemStack stack)
    {
        return 0;
    }

    default int getBarColor(ItemStack stack)
    {
        return 0;
    }

    default boolean isBarVisible(ItemStack stack)
    {
        return false;
    }

    record Instance(List<ItemStack> items, int width, int height) implements TooltipComponent {}
}
