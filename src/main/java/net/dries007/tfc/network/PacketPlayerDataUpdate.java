/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.capability.player.IPlayerData;

public class PacketPlayerDataUpdate implements IMessage
{
    private NBTTagCompound skillsNbt;

    @SuppressWarnings("unused")
    @Deprecated
    public PacketPlayerDataUpdate() {}

    public PacketPlayerDataUpdate(NBTTagCompound skillsNbt)
    {
        this.skillsNbt = skillsNbt;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        skillsNbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, skillsNbt);
    }

    public static final class Handler implements IMessageHandler<PacketPlayerDataUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(PacketPlayerDataUpdate message, MessageContext ctx)
        {
            TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
                if (player != null)
                {
                    IPlayerData skills = player.getCapability(CapabilityPlayerData.CAPABILITY, null);
                    if (skills != null)
                    {
                        skills.deserializeNBT(message.skillsNbt);
                    }
                }
            });
            return null;
        }
    }
}
