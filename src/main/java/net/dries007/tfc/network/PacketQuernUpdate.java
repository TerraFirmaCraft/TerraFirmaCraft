/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.te.TEQuern;
import net.dries007.tfc.util.Helpers;

public class PacketQuernUpdate implements IMessage
{
    private NBTTagCompound inventoryNBT;
    private BlockPos pos;

    @SuppressWarnings("unused")
    public PacketQuernUpdate() {}

    public PacketQuernUpdate(TEQuern tile)
    {
        this.pos = tile.getPos();
        this.inventoryNBT = tile.getInventory().serializeNBT();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = BlockPos.fromLong(buf.readLong());
        inventoryNBT = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeTag(buf, inventoryNBT);
    }

    public static class Handler implements IMessageHandler<PacketQuernUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(PacketQuernUpdate message, MessageContext ctx)
        {
            EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
            if (player != null)
            {
                World world = player.getEntityWorld();
                TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                    TEQuern te = Helpers.getTE(world, message.pos, TEQuern.class);
                    if (te != null)
                    {
                        te.setInventory(message.inventoryNBT);
                    }
                });
            }
            return null;
        }
    }
}
