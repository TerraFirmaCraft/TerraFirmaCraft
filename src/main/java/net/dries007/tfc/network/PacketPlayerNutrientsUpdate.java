/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.nutrient.CapabilityFood;
import net.dries007.tfc.api.capability.nutrient.IPlayerNutrients;
import net.dries007.tfc.util.agriculture.Nutrient;

public class PacketPlayerNutrientsUpdate implements IMessage
{
    private float[] nutrients;

    @SuppressWarnings("unused")
    public PacketPlayerNutrientsUpdate()
    {
        nutrients = new float[Nutrient.TOTAL];
    }

    public PacketPlayerNutrientsUpdate(IPlayerNutrients cap)
    {
        nutrients = cap.getNutrients();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        for (int i = 0; i < nutrients.length; i++)
        {
            nutrients[i] = buf.readFloat();
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        for (float nutrient : nutrients)
        {
            buf.writeFloat(nutrient);
        }
    }

    public static final class Handler implements IMessageHandler<PacketPlayerNutrientsUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(PacketPlayerNutrientsUpdate message, MessageContext ctx)
        {
            TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
                if (player != null)
                {
                    IPlayerNutrients cap = player.getCapability(CapabilityFood.CAPABILITY_PLAYER_NUTRIENTS, null);
                    if (cap != null)
                    {
                        cap.setNutrients(message.nutrients);
                    }
                }
            });
            return null;
        }
    }
}
