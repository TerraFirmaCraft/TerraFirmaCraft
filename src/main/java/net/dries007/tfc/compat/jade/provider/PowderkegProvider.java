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
import net.dries007.tfc.common.blockentities.PowderkegBlockEntity;
import net.dries007.tfc.util.Helpers;

public enum PowderkegProvider implements IComponentProvider
{
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig iPluginConfig)
    {
        if (access.getBlockEntity() instanceof PowderkegBlockEntity keg)
        {
            tooltip.add(Helpers.translatable("tfc.jade.explosion_strength").append(Helpers.translatable(String.valueOf(PowderkegBlockEntity.getStrength(keg)))));
        }
    }
}
