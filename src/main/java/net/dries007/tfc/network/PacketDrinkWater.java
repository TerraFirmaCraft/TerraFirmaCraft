/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.player.CapabilityPlayer;
import net.dries007.tfc.api.capability.player.IPlayerData;
import net.dries007.tfc.client.gui.overlay.PlayerDataOverlay;
import net.dries007.tfc.util.agriculture.Nutrient;

/**
 * Sent from client to server.
 * Fill
 */
public class PacketDrinkWater implements IMessage
{
    private float amount;
    public PacketDrinkWater() { this.amount = 15f; }

    public PacketDrinkWater(float amount) { this.amount = amount; }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        amount = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) { buf.writeFloat(amount); }

    public static final class Handler implements IMessageHandler<PacketDrinkWater, IMessage>
    {
        @Override
        public IMessage onMessage(PacketDrinkWater message, MessageContext ctx)
        {
            TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
                if (player != null)
                {
                    IPlayerData cap = player.getCapability(CapabilityPlayer.CAPABILITY_PLAYER_DATA, null);
                    if (cap != null)
                    {
                        cap.drink(message.amount);
                        player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 1.0f, 1.0f);
                        //Send update to client
                        TerraFirmaCraft.getNetwork().sendTo(new PacketPlayerDataUpdate(cap), (EntityPlayerMP)player);
                    }
                }
            });
            return null;
        }
    }
}
