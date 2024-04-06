/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.dries007.tfc.util.calendar.Calendar;

public final class ClientCalendar extends Calendar
{
    /**
     * On client, simulates the ticking of the server calendar between synchronization packets from the server (every 20 ticks = 1 second).
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