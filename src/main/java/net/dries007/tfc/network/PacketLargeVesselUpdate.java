/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.te.TELargeVessel;
import net.dries007.tfc.util.Helpers;

public class PacketLargeVesselUpdate implements IMessage
{
    private BlockPos pos;
    private long calendarTick;
    private boolean sealed;

    @SuppressWarnings("unused")
    @Deprecated
    public PacketLargeVesselUpdate() {}

    public PacketLargeVesselUpdate(@Nonnull TELargeVessel tile, long calendarTick, boolean sealed)
    {
        this.pos = tile.getPos();
        this.calendarTick = calendarTick;
        this.sealed = sealed;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = BlockPos.fromLong(buf.readLong());
        calendarTick = buf.readLong();
        sealed = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(pos.toLong());
        buf.writeLong(calendarTick);
        buf.writeBoolean(sealed);
    }

    public static final class Handler implements IMessageHandler<PacketLargeVesselUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(PacketLargeVesselUpdate message, MessageContext ctx)
        {
            EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
            if (player != null)
            {
                World world = player.getEntityWorld();
                TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                    TELargeVessel te = Helpers.getTE(world, message.pos, TELargeVessel.class);
                    if (te != null)
                    {
                        te.onReceivePacket(message.calendarTick, message.sealed);
                    }
                });
            }
            return null;
        }
    }
}
