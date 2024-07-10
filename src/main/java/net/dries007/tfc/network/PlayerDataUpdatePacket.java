/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.capabilities.player.PlayerData;
import net.dries007.tfc.common.recipes.ChiselRecipe;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public record PlayerDataUpdatePacket(
    long lastDrinkTick,
    long intoxicationTick,
    ChiselRecipe.Mode mode
) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PlayerDataUpdatePacket> TYPE = PacketHandler.type("player_data");
    public static final StreamCodec<ByteBuf, PlayerDataUpdatePacket> STREAM = StreamCodec.composite(
        ByteBufCodecs.VAR_LONG, c -> c.lastDrinkTick,
        ByteBufCodecs.VAR_LONG, c -> c.intoxicationTick,
        ChiselRecipe.Mode.STREAM, c -> c.mode,
        PlayerDataUpdatePacket::new
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
            PlayerData.get(player).updateFromPacket(lastDrinkTick, intoxicationTick, mode);
        }
    }
}
