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

import net.dries007.tfc.common.component.glass.GlassOperation;
import net.dries007.tfc.common.component.glass.IGlassworkingTool;

public class GlassworkingItem extends Item implements IGlassworkingTool
{
    private final GlassOperation operation;

    public GlassworkingItem(Properties properties, GlassOperation operation)
    {
        super(properties);
        this.operation = operation;
    }

    @Override
    public GlassOperation getOperation()
    {
        return operation;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag)
    {
        super.appendHoverText(stack, context, tooltip, tooltipFlag);
        addToolTooltip(tooltip);
    }
}
