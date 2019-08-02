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
import net.dries007.tfc.objects.te.TEChestTFC;
import net.dries007.tfc.util.Helpers;

public class PacketChestUpdate implements IMessage
{
    private int connectedTo;
    private BlockPos pos;

    @SuppressWarnings("unused")
    public PacketChestUpdate() {}

    public PacketChestUpdate(@Nonnull TEChestTFC teChestTFC)
    {
        connectedTo = teChestTFC.getConnection() == null ? 0 : teChestTFC.getConnection().getIndex() - 1;
        pos = teChestTFC.getPos();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        connectedTo = buf.readInt();
        pos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(connectedTo);
        buf.writeLong(pos.toLong());
    }

    public static class Handler implements IMessageHandler<PacketChestUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(PacketChestUpdate message, MessageContext ctx)
        {
            EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
            if (player != null)
            {
                World world = player.getEntityWorld();
                TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                    TEChestTFC te = Helpers.getTE(world, message.pos, TEChestTFC.class);
                    if (te != null)
                    {
                        te.onReceivePacket(message.connectedTo);
                    }
                });
            }
            return null;
        }
    }
}
