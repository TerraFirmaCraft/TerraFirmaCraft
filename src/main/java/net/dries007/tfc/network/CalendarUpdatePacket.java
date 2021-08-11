/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.fmllegacy.network.NetworkEvent;

import net.dries007.tfc.util.calendar.Calendar;
import net.dries007.tfc.util.calendar.Calendars;

public class CalendarUpdatePacket
{
    private final Calendar instance;

    public CalendarUpdatePacket(Calendar instance)
    {
        this.instance = instance;
    }

    public CalendarUpdatePacket(FriendlyByteBuf buffer)
    {
        instance = new Calendar();
        instance.read(buffer);
    }

    void encode(FriendlyByteBuf buffer)
    {
        instance.write(buffer);
    }

    void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> Calendars.CLIENT.reset(instance));
        context.get().setPacketHandled(true);
    }
}