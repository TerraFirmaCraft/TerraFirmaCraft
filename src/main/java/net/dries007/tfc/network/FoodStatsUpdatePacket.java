package net.dries007.tfc.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.capabilities.food.TFCFoodStats;

public class FoodStatsUpdatePacket
{
    private final float[] nutrients;
    private final float thirst;

    public FoodStatsUpdatePacket(float[] nutrients, float thirst)
    {
        this.nutrients = nutrients;
        this.thirst = thirst;
    }

    public FoodStatsUpdatePacket(PacketBuffer buffer)
    {
        this.nutrients = new float[Nutrient.TOTAL];
        for (int i = 0; i < nutrients.length; i++)
        {
            nutrients[i] = buffer.readFloat();
        }
        this.thirst = buffer.readFloat();
    }

    void encode(PacketBuffer buffer)
    {
        for (float nutrient : nutrients)
        {
            buffer.writeFloat(nutrient);
        }
        buffer.writeFloat(thirst);
    }

    void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().setPacketHandled(true);
        context.get().enqueueWork(() -> {
            final PlayerEntity player = ClientHelpers.getPlayer();
            if (player != null && player.getFoodData() instanceof TFCFoodStats)
            {
                ((TFCFoodStats) player.getFoodData()).onClientUpdate(nutrients, thirst);
            }
        });
    }
}
