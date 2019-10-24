/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.calendar;

import net.minecraft.util.ITickable;

/**
 * This is implemented on TileEntities that need to receive updates whenever the calendar changes drastically
 *
 * It extends {@link ITickable} so we can use the world's list of tickable tile entities to scan through (better performance), and since anything that implements this very likely also wants to implement {@link ITickable} as well
 */
public interface ICalendarTickable extends ITickable
{
    /**
     * Called when the calendar updates (either player or calendar time)
     * To find the delta time, in {@link ITickable#update()}, store a cached value of {@code CalendarTFC.PLAYER_TIME.getTicks()}, then compare that cached value with the value obtained in this method
     */
    void onCalendarUpdate();
}
