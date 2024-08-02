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
 *         tr.add(8000); // Adds 8000 ticks to the calendar.
 *         foo(); // Do an action as if it was performed in the future (or past)
 *     }
 * </pre>
 * Do not implement this, prefer getting one by calling {@link ServerCalendar#transaction()} on {@link Calendars#SERVER}.
 */
public sealed interface CalendarTransaction extends AutoCloseable permits Calendar.Transaction
{
    /**
     * Add a certain amount of ticks to the calendar, within the transaction.
     * @param ticks A number of ticks. <strong>May</strong> be negative, but be careful about accidentally pushing the
     *              calendar into negative values, there is no protection against that.
     */
    void add(long ticks);

    /**
     * @return The current number of total ticks that have been added from the baseline.
     */
    long ticks();

    @Override
    void close();
}
