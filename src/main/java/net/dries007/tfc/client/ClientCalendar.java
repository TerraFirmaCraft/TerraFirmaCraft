/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.minecraft.util.Mth;

import net.dries007.tfc.util.calendar.Calendar;

public final class ClientCalendar extends Calendar
{
    /**
     * On client, simulates the ticking of the server calendar between synchronization packets from the server (every 20 ticks = 1 second).
     * Note that we know for a fact that clients have to be logged on because this client is.
     */
    void onClientTick()
    {
        playerTicks++;
        calendarPartialTick += calendarTickRate;
        calendarTicks += Mth.floor(calendarPartialTick);
        calendarPartialTick = Mth.frac(calendarPartialTick);
    }
}