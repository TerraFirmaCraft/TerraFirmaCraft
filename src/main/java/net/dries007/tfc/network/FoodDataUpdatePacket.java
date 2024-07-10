/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public record FoodDataUpdatePacket(
    float[] nutrients,
    float thirst
) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<FoodDataUpdatePacket> TYPE = PacketHandler.type("update_food_data");
    public static final StreamCodec<ByteBuf, FoodDataUpdatePacket> STREAM = StreamCodec.composite(
        FoodData.NUTRITION_STREAM_CODEC, c -> c.nutrients,
        ByteBufCodecs.FLOAT, c -> c.thirst,
        FoodDataUpdatePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
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
