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
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.glass.GlassOperation;
import net.dries007.tfc.common.capabilities.glass.IGlassworkingTool;

public class GemSawItem extends ToolItem implements IGlassworkingTool
{
    public GemSawItem(Tier tier, Properties properties)
    {
        super(tier, calculateVanillaAttackDamage(-0.2f, tier), -2.0F, TFCTags.Blocks.MINEABLE_WITH_GLASS_SAW, properties);
    }

    @Override
    public GlassOperation getOperation()
    {
        return GlassOperation.SAW;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        super.appendHoverText(stack, level, tooltip, flag);
        addToolTooltip(tooltip);
    }
}
