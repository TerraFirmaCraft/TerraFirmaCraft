/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.player.CapabilityPlayer;
import net.dries007.tfc.api.capability.player.IPlayerData;
import net.dries007.tfc.client.gui.overlay.PlayerDataOverlay;
import net.dries007.tfc.util.agriculture.Nutrient;

public class PacketPlayerDataUpdate implements IMessage
{
    private float[] nutrients;
    private float thirst;

    @SuppressWarnings("unused")
    public PacketPlayerDataUpdate()
    {
        nutrients = new float[Nutrient.TOTAL];
        thirst = 0;
    }

    public PacketPlayerDataUpdate(IPlayerData cap)
    {
        nutrients = cap.getNutrients();
        thirst = cap.getThirst();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        for (int i = 0; i < nutrients.length; i++)
        {
            nutrients[i] = buf.readFloat();
        }
        thirst = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        for (float nutrient : nutrients)
        {
            buf.writeFloat(nutrient);
        }
        buf.writeFloat(thirst);
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
                    IPlayerData cap = player.getCapability(CapabilityPlayer.CAPABILITY_PLAYER_DATA, null);
                    if (cap != null)
                    {
                        cap.setNutrients(message.nutrients);
                        cap.setThirst(message.thirst);
                        //Update HUD
                        PlayerDataOverlay.getInstance().setMaxHealth(cap.getMaxHealth());
                        PlayerDataOverlay.getInstance().setCurThirst(cap.getThirst());
                    }
                }
            });
            return null;
        }
    }
}
