/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.util.calendar.Calendar;
import net.dries007.tfc.util.calendar.Calendars;
import net.minecraft.network.FriendlyByteBuf;

public record CalendarUpdatePacket(Calendar calendar)
{
    public CalendarUpdatePacket(FriendlyByteBuf buffer)
    {
        this(new Calendar(buffer));
    }

    void encode(FriendlyByteBuf buffer)
    {
        calendar.write(buffer);
    }

    void handle()
    {
        Calendars.CLIENT.resetTo(calendar);
    }
}