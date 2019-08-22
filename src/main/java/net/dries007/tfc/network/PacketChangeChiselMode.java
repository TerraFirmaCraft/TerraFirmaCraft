package net.dries007.tfc.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.skill.CapabilityPlayerSkills;
import net.dries007.tfc.api.capability.skill.IPlayerSkills;

public class PacketChangeChiselMode implements IMessageEmpty
{
    public static final class Handler implements IMessageHandler<PacketChangeChiselMode, IMessage>
    {
        @Override
        public IMessage onMessage(PacketChangeChiselMode message, MessageContext ctx)
        {
            TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
                if (player != null)
                {
                    IPlayerSkills capability = player.getCapability(CapabilityPlayerSkills.CAPABILITY, null);

                    if (capability != null)
                    {
                        IPlayerSkills.ChiselMode mode = capability.getChiselMode();
                        capability.setChiselMode(mode.getNextMode());
                    }
                }
            });
            return null;
        }
    }
}
