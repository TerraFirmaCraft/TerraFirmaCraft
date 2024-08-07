/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.Optional;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import net.dries007.tfc.common.blocks.TooltipBlock;

public class TooltipBlockItem extends BlockItem
{
    public TooltipBlockItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
    {
        if (getBlock() instanceof TooltipBlock tooltip)
        {
            return tooltip.getTooltipImage(stack);
        }
        return super.getTooltipImage(stack);
    }
}
