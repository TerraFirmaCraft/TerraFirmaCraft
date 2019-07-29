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
import net.dries007.tfc.objects.te.TEBellows;
import net.dries007.tfc.util.Helpers;

public class PacketBellowsUpdate implements IMessage
{
    private BlockPos pos;
    private long lastPushed;

    @SuppressWarnings("unused")
    @Deprecated
    public PacketBellowsUpdate() {}

    public PacketBellowsUpdate(@Nonnull TEBellows tile, long lastPushed)
    {
        this.pos = tile.getPos();
        this.lastPushed = lastPushed;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = BlockPos.fromLong(buf.readLong());
        lastPushed = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(pos.toLong());
        buf.writeLong(lastPushed);
    }

    public static final class Handler implements IMessageHandler<PacketBellowsUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(PacketBellowsUpdate message, MessageContext ctx)
        {
            EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
            if (player != null)
            {
                World world = player.getEntityWorld();
                TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                    TEBellows te = Helpers.getTE(world, message.pos, TEBellows.class);
                    if (te != null)
                    {
                        te.onReceivePacket(message.lastPushed);
                    }
                });
            }
            return null;
        }
    }
}
