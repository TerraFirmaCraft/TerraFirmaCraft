/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.dries007.tfc.util.calendar.Calendar;
import net.dries007.tfc.util.calendar.Calendars;

public record CalendarUpdatePacket(Calendar calendar) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<CalendarUpdatePacket> TYPE = PacketHandler.type("calendar_update");
    public static final StreamCodec<ByteBuf, CalendarUpdatePacket> CODEC = Calendar.STREAM_CODEC.map(CalendarUpdatePacket::new, CalendarUpdatePacket::calendar);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle()
    {
        Calendars.CLIENT.resetTo(calendar);
    }
}