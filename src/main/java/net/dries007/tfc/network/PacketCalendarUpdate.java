/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.classic.CalenderTFC;

public class PacketCalendarUpdate implements IMessage
{
    private long calendarOffset;
    private int daysInMonth;

    @SuppressWarnings("unused")
    public PacketCalendarUpdate() {}

    public PacketCalendarUpdate(long calendarOffset, int daysInMonth)
    {
        this.calendarOffset = calendarOffset;
        this.daysInMonth = daysInMonth;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        calendarOffset = buf.readLong();
        daysInMonth = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(calendarOffset);
        buf.writeInt(daysInMonth);
    }

    public static class Handler implements IMessageHandler<PacketCalendarUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(PacketCalendarUpdate message, MessageContext ctx)
        {
            World world = TerraFirmaCraft.getProxy().getWorld(ctx);
            if (world != null)
            {
                TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                    CalenderTFC.CalendarWorldData.update(world, message.calendarOffset, message.daysInMonth);
                });
            }
            return null;
        }
    }
}
