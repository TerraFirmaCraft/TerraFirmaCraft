/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.player.ChiselMode;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.common.recipes.ChiselRecipe;

public record PlayerInfoPacket(
    long lastDrinkTick,
    float thirst,
    ChiselMode chiselMode,
    long intoxication,
    float[] nutrients
) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PlayerInfoPacket> TYPE = PacketHandler.type("player_info");
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerInfoPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_LONG, c -> c.lastDrinkTick,
        ByteBufCodecs.FLOAT, c -> c.thirst,
        ByteBufCodecs.registry(ChiselMode.KEY), c -> c.chiselMode,
        ByteBufCodecs.VAR_LONG, c -> c.intoxication,
        FoodData.NUTRITION_STREAM_CODEC, c -> c.nutrients,
        PlayerInfoPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle()
    {
        final Player player = ClientHelpers.getPlayer();
        if (player != null)
        {
            IPlayerInfo.get(player).onClientUpdate(this);
        }
    }
}
