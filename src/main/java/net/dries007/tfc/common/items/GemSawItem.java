/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.glass.GlassOperation;
import net.dries007.tfc.common.component.glass.IGlassworkingTool;

public class GemSawItem extends ToolItem implements IGlassworkingTool
{
    public GemSawItem(Tier tier, Properties properties)
    {
        super(tier, TFCTags.Blocks.MINEABLE_WITH_GLASS_SAW, properties.attributes(ToolItem.productAttributes(tier, -0.2f, -2.0f)));
    }

    @Override
    public GlassOperation getOperation()
    {
        return GlassOperation.SAW;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag)
    {
        super.appendHoverText(stack, context, tooltip, tooltipFlag);
        addToolTooltip(tooltip);
    }
}
