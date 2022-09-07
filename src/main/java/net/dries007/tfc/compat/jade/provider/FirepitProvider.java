/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade.provider;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.recipes.SoupPotRecipe;
import net.dries007.tfc.compat.jade.JadeIntegration;
import net.dries007.tfc.util.Helpers;

public enum FirepitProvider implements IComponentProvider
{
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig iPluginConfig)
    {
        if (access.getBlockEntity() instanceof AbstractFirepitBlockEntity<?> firepit)
        {
            JadeIntegration.displayHeat(tooltip, firepit.getTemperature());

            if (firepit instanceof PotBlockEntity pot)
            {
                if (pot.shouldRenderAsBoiling())
                {
                    tooltip.add(Helpers.translatable("tfc.tooltip.pot_boiling"));
                }
                else if (pot.getOutput() != null && !pot.getOutput().isEmpty())
                {
                    tooltip.add(Helpers.translatable("tfc.tooltip.pot_finished"));

                    if (pot.getOutput() instanceof SoupPotRecipe.SoupOutput soup)
                    {
                        final ItemStack stack = soup.stack();
                        JadeIntegration.displayCountedItemName(tooltip, stack);

                        final List<Component> text = new ArrayList<>();
                        stack.getCapability(FoodCapability.CAPABILITY).ifPresent(cap -> cap.addTooltipInfo(stack, text));
                        tooltip.addAll(text);
                    }
                }
            }
        }
    }
}
