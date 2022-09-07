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
import net.dries007.tfc.common.blockentities.NestBoxBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.egg.EggCapability;
import net.dries007.tfc.common.capabilities.egg.IEgg;

public enum NestBoxProvider implements IComponentProvider
{
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig iPluginConfig)
    {
        if (access.getBlockEntity() instanceof NestBoxBlockEntity nest)
        {
            final List<Component> text = new ArrayList<>();
            nest.getCapability(Capabilities.ITEM).ifPresent(inventory -> {
                for (int i = 0; i < inventory.getSlots(); i++)
                {
                    ItemStack stack = inventory.getStackInSlot(i);
                    stack.getCapability(EggCapability.CAPABILITY).filter(IEgg::isFertilized).ifPresent(egg -> {
                        text.add(stack.getHoverName());
                        egg.addTooltipInfo(text);
                    });
                }
            });
            tooltip.addAll(text);
        }
    }
}
