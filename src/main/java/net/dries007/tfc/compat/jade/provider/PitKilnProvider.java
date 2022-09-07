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
import net.dries007.tfc.common.blockentities.PitKilnBlockEntity;
import net.dries007.tfc.common.blocks.devices.PitKilnBlock;
import net.dries007.tfc.util.Helpers;

public enum PitKilnProvider implements IComponentProvider
{
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig iPluginConfig)
    {
        if (access.getBlockEntity() instanceof PitKilnBlockEntity kiln && access.getBlock() instanceof PitKilnBlock)
        {
            if (access.getBlockState().getValue(PitKilnBlock.STAGE) == PitKilnBlock.LIT)
            {
                tooltip.add(Helpers.translatable("tfc.jade.progress", Helpers.formatPercentage(kiln.getProgress() * 100)));
            }
            else
            {
                tooltip.add(Helpers.translatable("tfc.jade.straws", String.valueOf(kiln.getStraws().size())));
                tooltip.add(Helpers.translatable("tfc.jade.logs", String.valueOf(kiln.getLogs().size())));
            }
        }
    }
}
