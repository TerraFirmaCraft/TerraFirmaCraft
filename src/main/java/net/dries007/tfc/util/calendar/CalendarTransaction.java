/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

/**
 * A calendar transaction object. When used in a {@code try-with-resources} construct, allows you to modify the calendar state to run a sequence of actions, without mutating the global calendar state.
 * Example usage:
 * <pre>
 *     try (CalendarTransaction tr = Calendars.SERVER.transaction()) {
 *         tr.add(); // Adds 8000 ticks to the calendar.
 *         foo(); // Do an action as if it was performed in the future (or past)
 *     }
 * </pre>
 * Do not implement this, prefer getting one by calling {@link ServerCalendar#transaction()} on {@link Calendars#SERVER}.
 */
public interface CalendarTransaction extends AutoCloseable
{
    /**
     * Add a certain amount of ticks to the calendar, within the transaction.
     * @param ticks A number of ticks. <strong>May</strong> be negative, but be careful about accidentally pushing the calendar into negative values, there is no protection against that.
     */
    default void add(long ticks)
    {
        add(ticks, ticks);
    }

    /**
     * Add a certain amount of ticks to the calendar.
     * @param playerTicks The number of player ticks ({@link ICalendar#getTicks()}) to add.
     * @param calendarTicks The number of calendar ticks ({@link ICalendar#getCalendarTicks()} to add.
     */
    void add(long playerTicks, long calendarTicks);

    @Override
    void close();
}
