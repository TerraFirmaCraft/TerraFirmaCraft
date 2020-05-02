/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.api.capability.food.Nutrient;

public class PacketFoodStatsUpdate implements IMessage
{
    private final float[] nutrients;
    private float thirst;

    @SuppressWarnings("unused")
    @Deprecated
    public PacketFoodStatsUpdate()
    {
        this.nutrients = new float[Nutrient.TOTAL];
    }

    public PacketFoodStatsUpdate(float[] nutrients, float thirst)
    {
        this.nutrients = nutrients;
        this.thirst = thirst;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        thirst = buf.readFloat();
        for (int i = 0; i < nutrients.length; i++)
        {
            nutrients[i] = buf.readFloat();
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeFloat(thirst);
        for (float f : nutrients)
        {
            buf.writeFloat(f);
        }
    }

    public static final class Handler implements IMessageHandler<PacketFoodStatsUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(PacketFoodStatsUpdate message, MessageContext ctx)
        {
            TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                final EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
                if (player != null)
                {
                    FoodStats foodStats = player.getFoodStats();
                    if (foodStats instanceof FoodStatsTFC)
                    {
                        ((FoodStatsTFC) foodStats).onReceivePacket(message.nutrients, message.thirst);
                    }
                }
            });
            return null;
        }
    }
}
