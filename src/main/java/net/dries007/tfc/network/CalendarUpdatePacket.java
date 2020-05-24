/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import net.dries007.tfc.api.calendar.CalendarTFC;

public class CalendarUpdatePacket
{
    private final CalendarTFC instance;

    public CalendarUpdatePacket(CalendarTFC instance)
    {
        this.instance = instance;
    }

    CalendarUpdatePacket(PacketBuffer buffer)
    {
        instance = new CalendarTFC();
        instance.read(buffer);
    }

    void encode(PacketBuffer buffer)
    {
        instance.write(buffer);
    }

    void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> CalendarTFC.INSTANCE.resetTo(instance));
        context.get().setPacketHandled(true);
    }
}
