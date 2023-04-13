/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fires when a tree is about to be felled by an axe. Cancelling the event causes the block to be broken normally, dropping itself without any side effects.
 */
@Cancelable
public final class LoggingEvent extends Event
{
    private final LevelAccessor level;
    private final BlockPos pos;
    private final BlockState state;
    private final ItemStack axe;

    public LoggingEvent(LevelAccessor level, BlockPos pos, BlockState state, ItemStack axe)
    {
        this.level = level;
        this.pos = pos;
        this.state = state;
        this.axe = axe;
    }

    public LevelAccessor getLevel()
    {
        return level;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public BlockState getState()
    {
        return state;
    }

    public ItemStack getAxe()
    {
        return axe;
    }
}
