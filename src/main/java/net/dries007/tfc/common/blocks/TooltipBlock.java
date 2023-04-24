/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Optional;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public interface TooltipBlock
{
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
}
