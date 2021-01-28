/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.dries007.tfc.util.calendar.Calendar;

public class ClientCalendar extends Calendar
{
    /**
     * Called on client ticks. This does a soft simulation of the server and world tracking. It is soft because it is re-synced to the server values every second, so this is purely a measure to keep the client timers counting with more granularity than one second, without requiring the calendar to sync every tick (as that gets messy)
     */
    void onClientTick()
    {
        if (arePlayersLoggedOn)
        {
            playerTicks++;
            if (doDaylightCycle)
            {
                calendarTicks++;
            }
        }
    }
}