/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * This is implemented on {@link BlockEntity}s that need to receive special updates when the calendar skips/jumps ahead.
 * In order to implement this, a field `lastUpdateTick` should be added and serialized. Nothing else should access this field, or the provided accessors. Doing so is almost certainly a bug, as this field just represents the last tick the block entity has been actively ticked.
 * The default value of this field should be initialized to {@link Integer#MIN_VALUE}.
 * <p>
 * Note: the default {@link #checkForCalendarUpdate()} casts the implementor to {@link BlockEntity}.
 */
public interface ICalendarTickable
{
    /**
     * Here we check every tick for a calendar discrepancy. This only checks for differences in player time, and calls {@link ICalendarTickable#onCalendarUpdate(long playerTickDelta)} as necessary.
     * <p>
     * Implementations MUST call {@code checkForCalendarUpdate()} in their {@code serverTick} method.
     */
    default void checkForCalendarUpdate()
    {
        final BlockEntity entity = ((BlockEntity) this);
        if (entity.getLevel() != null && !entity.getLevel().isClientSide())
        {
            final long thisTick = Calendars.SERVER.getTicks();
            final long lastTick = getLastUpdateTick();
            final long tickDelta = thisTick - lastTick;
            if (lastTick != Integer.MIN_VALUE && tickDelta != 1)
            {
                onCalendarUpdate(tickDelta - 1);
            }
            setLastUpdateTick(thisTick);
            markDirty();
        }
    }

    /**
     * Called when the calendar jumps forward by a tick amount > 1.
     *
     * @param ticks the difference in player ticks observed between last tick and this tick.
     */
    void onCalendarUpdate(long ticks);

    /**
     * @return The last tick this {@code BlockEntity} was ticked.
     * @deprecated Do not call.
     */
    @Deprecated
    long getLastUpdateTick();

    /**
     * Set the last tick this {@code BlockEntity} was ticked.
     * @deprecated Do not call.
     */
    @Deprecated
    void setLastUpdateTick(long tick);


    default void markDirty()
    {
        ((BlockEntity) this).setChanged();
    }
}
