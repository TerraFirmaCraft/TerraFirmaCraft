/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.container.IButtonHandler;

/**
 * This is a generic packet that sends a button notification to the players open container, which can delegate to the tile entity if needed
 * See {@link net.dries007.tfc.client.gui.GuiAnvilTFC} for an example of its usage, and {@link net.dries007.tfc.objects.container.ContainerAnvilTFC} for an example of the message handling
 *
 * @author AlcatrazEscapee
 */
public class PacketGuiButton implements IMessage
{
    private int buttonID;
    private NBTTagCompound extraNBT;

    @SuppressWarnings("unused")
    @Deprecated
    public PacketGuiButton() {}

    public PacketGuiButton(int buttonID, @Nullable NBTTagCompound extraNBT)
    {
        this.buttonID = buttonID;
        this.extraNBT = extraNBT;
    }

    public PacketGuiButton(int buttonID)
    {
        this(buttonID, null);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        buttonID = buf.readInt();
        if (buf.readBoolean())
        {
            extraNBT = ByteBufUtils.readTag(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(buttonID);
        buf.writeBoolean(extraNBT != null);
        if (extraNBT != null)
        {
            ByteBufUtils.writeTag(buf, extraNBT);
        }
    }

    public static class Handler implements IMessageHandler<PacketGuiButton, IMessage>
    {
        @Override
        public IMessage onMessage(PacketGuiButton message, MessageContext ctx)
        {
            EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
            if (player != null)
            {
                TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                    if (player.openContainer instanceof IButtonHandler)
                    {
                        ((IButtonHandler) player.openContainer).onButtonPress(message.buttonID, message.extraNBT);
                    }
                });
            }
            return null;
        }
    }
}
