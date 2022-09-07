/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade.provider;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.dries007.tfc.common.blockentities.BloomeryBlockEntity;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.recipes.BloomeryRecipe;
import net.dries007.tfc.util.Helpers;

public enum BloomeryProvider implements IComponentProvider
{
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig iPluginConfig)
    {
        if (access.getBlockEntity() instanceof BloomeryBlockEntity bloomery && access.getBlock() instanceof BloomeryBlock)
        {
            tooltip.add(Helpers.translatable("tfc.jade.input_stacks", bloomery.getInputStacks().size()));
            tooltip.add(Helpers.translatable("tfc.jade.catalyst_stacks", bloomery.getCatalystStacks().size()));
            if (access.getBlockState().getValue(BloomeryBlock.LIT))
            {
                final BloomeryRecipe recipe = bloomery.getCachedRecipe();
                if (recipe != null)
                {
                    final String percent = Helpers.formatPercentage((float) bloomery.getTicksSinceLit(access.getLevel()) / recipe.getDuration() * 100);
                    tooltip.add(Helpers.translatable("tfc.jade.progress").append(percent));
                    tooltip.add(Helpers.translatable("tfc.jade.creating", recipe.getResultItem().getHoverName()));
                }
            }
        }
    }
}
