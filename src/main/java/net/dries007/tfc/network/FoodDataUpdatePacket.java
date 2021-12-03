/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;

public class FoodDataUpdatePacket
{
    private final float[] nutrients;
    private final float thirst;

    public FoodDataUpdatePacket(float[] nutrients, float thirst)
    {
        this.nutrients = nutrients;
        this.thirst = thirst;
    }

    FoodDataUpdatePacket(FriendlyByteBuf buffer)
    {
        this.nutrients = new float[Nutrient.TOTAL];
        for (int i = 0; i < nutrients.length; i++)
        {
            nutrients[i] = buffer.readFloat();
        }
        this.thirst = buffer.readFloat();
    }

    void encode(FriendlyByteBuf buffer)
    {
        for (float nutrient : nutrients)
        {
            buffer.writeFloat(nutrient);
        }
        buffer.writeFloat(thirst);
    }

    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            final Player player = ClientHelpers.getPlayer();
            if (player != null && player.getFoodData() instanceof TFCFoodData data)
            {
                data.onClientUpdate(nutrients, thirst);
            }
        });
    }
}
