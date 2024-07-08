/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record FoodDataUpdatePacket(
    float[] nutrients,
    float thirst
)
{
    private static float[] readNutrients(FriendlyByteBuf buffer)
    {
        float[] nutrients = new float[Nutrient.TOTAL];
        for (int i = 0; i < nutrients.length; i++)
        {
            nutrients[i] = buffer.readFloat();
        }
        return nutrients;
    }

    FoodDataUpdatePacket(FriendlyByteBuf buffer)
    {
        this(
            readNutrients(buffer),
            buffer.readFloat()
        );
    }

    void encode(FriendlyByteBuf buffer)
    {
        for (float nutrient : nutrients)
        {
            buffer.writeFloat(nutrient);
        }
        buffer.writeFloat(thirst);
    }

    void handle()
    {
        final Player player = ClientHelpers.getPlayer();
        if (player != null && player.getFoodData() instanceof TFCFoodData data)
        {
            data.onClientUpdate(nutrients, thirst);
        }
    }
}
