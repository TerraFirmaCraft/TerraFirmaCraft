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
import net.dries007.tfc.common.blockentities.BloomBlockEntity;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.util.Helpers;

public enum BloomProvider implements IComponentProvider
{
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig iPluginConfig)
    {
        if (access.getBlockEntity() instanceof BloomBlockEntity bloom)
        {
            final ItemStack item = bloom.getItem();
            tooltip.add(Helpers.literal(String.valueOf(bloom.getCount())).append("x ").append(item.getHoverName()));

            final List<Component> text = new ArrayList<>();
            item.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> cap.addTooltipInfo(item, text));
            tooltip.addAll(text);
        }
    }
}
