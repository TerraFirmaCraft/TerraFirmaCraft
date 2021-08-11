/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * This is implemented on TileEntities that need to receive updates whenever the calendar changes drastically
 * Note: the default {@code update()} casts the implementor to {@link TileEntity}
 */
public interface ICalendarTickable
{
    /**
     * Here we check every tick for a calendar discrepancy. This only checks for differences in player time, and calls {@link ICalendarTickable#onCalendarUpdate(long playerTickDelta)} as necessary
     *
     * Implementations MUST call {@code ICalendarTickable.super.update()} in their implementation
     */
    default void checkForCalendarUpdate()
    {
        BlockEntity te = ((BlockEntity) this).getTileEntity();
        if (te.getLevel() != null && !te.getLevel().isClientSide())
        {
            long playerTick = Calendars.SERVER.getTicks();
            long tickDelta = playerTick - getLastUpdateTick();
            if (tickDelta != 1) // Expect 1 tick
            {
                onCalendarUpdate(tickDelta - 1);
            }
            setLastUpdateTick(playerTick);
        }
    }

    /**
     * Called when the calendar updates (either player or calendar time)
     *
     * @param playerTickDelta the difference in player ticks observed between last tick and this tick
     */
    void onCalendarUpdate(long playerTickDelta);

    /**
     * Gets the last update tick.
     * This should use a locally cached value. No need for serialization
     */
    long getLastUpdateTick();

    /**
     * Sets the last update tick
     * This should cache the value locally. No need for serialization
     */
    void setLastUpdateTick(long tick);
}
