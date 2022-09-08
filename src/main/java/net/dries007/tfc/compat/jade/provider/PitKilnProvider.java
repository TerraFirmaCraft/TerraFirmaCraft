/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade.provider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.dries007.tfc.common.blockentities.PitKilnBlockEntity;
import net.dries007.tfc.common.blocks.devices.PitKilnBlock;
import net.dries007.tfc.util.Helpers;

public enum PitKilnProvider implements IComponentProvider
{
    INTERNAL(0),
    ABOVE(-1);

    private final int offset;

    PitKilnProvider(int offset)
    {
        this.offset = offset;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig iPluginConfig)
    {
        final Level level = access.getLevel();
        final BlockPos pos = access.getPosition().relative(Direction.UP, offset);
        final BlockState state = level.getBlockState(pos);

        if (level.getBlockEntity(pos) instanceof PitKilnBlockEntity kiln && state.getBlock() instanceof PitKilnBlock)
        {
            if (state.getValue(PitKilnBlock.STAGE) == PitKilnBlock.LIT)
            {
                tooltip.add(Helpers.translatable("tfc.jade.progress").append(Helpers.formatPercentage(kiln.getProgress() * 100)));
            }
            else
            {
                tooltip.add(Helpers.translatable("tfc.jade.straws", String.valueOf(kiln.getStraws().size())));
                tooltip.add(Helpers.translatable("tfc.jade.logs", String.valueOf(kiln.getLogs().size())));
            }
        }
    }
}
