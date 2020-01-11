/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.calendar;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

/**
 * This is implemented on TileEntities that need to receive updates whenever the calendar changes drastically
 * Note: the default {@code update()} casts the implementor to {@link TileEntity}
 *
 * @see CalendarTFC#runTransaction(long, long, Runnable)
 */
public interface ICalendarTickable extends ITickable
{
    /**
     * Here we check every tick for a calendar discrepancy. This only checks for differences in player time, and calls {@link ICalendarTickable#onCalendarUpdate(long playerTickDelta)} as necessary
     *
     * Implementations MUST call {@code ICalendarTickable.super.update()} in their implementation
     */
    @Override
    default void update()
    {
        TileEntity te = (TileEntity) this;
        //noinspection ConstantConditions
        if (te.getWorld() != null && !te.getWorld().isRemote)
        {
            long playerTick = CalendarTFC.PLAYER_TIME.getTicks();
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
