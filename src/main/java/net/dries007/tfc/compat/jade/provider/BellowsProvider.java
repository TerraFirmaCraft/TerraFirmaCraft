/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade.provider;

import net.minecraft.network.chat.MutableComponent;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.dries007.tfc.common.blockentities.BellowsBlockEntity;
import net.dries007.tfc.util.Helpers;

public enum BellowsProvider implements IComponentProvider
{
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig iPluginConfig)
    {
        if (access.getBlockEntity() instanceof BellowsBlockEntity bellows)
        {
            int pushTicks = bellows.getTicksSincePushed();
            if (pushTicks < 20)
            {
                pushTicks *= 2;
                if (pushTicks > 20)
                {
                    pushTicks = 40 - pushTicks;
                }

                MutableComponent component = Helpers.translatable("tfc.jade.bellows_start");
                String middle = Helpers.translatable("tfc.jade.bellows_middle").getString(); // probably better to translate this once.
                for (int i = 0; i < pushTicks; i++)
                {
                    component.append(Helpers.literal(middle));
                }
                component.append(Helpers.translatable("tfc.jade.bellows_end"));

                tooltip.add(component);
            }

        }
    }
}
