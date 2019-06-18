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
import net.dries007.tfc.objects.te.TECrucible;
import net.dries007.tfc.util.Helpers;

/**
 * Used to send the alloy contents of a crucible to client for display purposes
 */
public class PacketCrucibleUpdate implements IMessage
{
    private NBTTagCompound alloyNBT;
    private BlockPos pos;

    @SuppressWarnings("unused")
    public PacketCrucibleUpdate() {}

    public PacketCrucibleUpdate(TECrucible tile)
    {
        this.pos = tile.getPos();
        this.alloyNBT = tile.getAlloy().serializeNBT();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = BlockPos.fromLong(buf.readLong());
        alloyNBT = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeTag(buf, alloyNBT);
    }

    public static class Handler implements IMessageHandler<PacketCrucibleUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(PacketCrucibleUpdate message, MessageContext ctx)
        {
            EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
            if (player != null)
            {
                World world = player.getEntityWorld();
                TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                    TECrucible te = Helpers.getTE(world, message.pos, TECrucible.class);
                    if (te != null)
                    {
                        te.setAlloy(message.alloyNBT);
                    }
                });
            }
            return null;
        }
    }
}
