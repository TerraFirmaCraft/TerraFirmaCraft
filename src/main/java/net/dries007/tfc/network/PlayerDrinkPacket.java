/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.data.Drinkable;

/**
 * Sent to the server when the player clicks a location that might trigger a drinking.
 * Since the server doesn't know about these naturally, we have to check and sync them ourselves.
 */
public enum PlayerDrinkPacket implements CustomPacketPayload
{
    PACKET;

    public static final CustomPacketPayload.Type<PlayerDrinkPacket> TYPE = PacketHandler.type("player_drink");
    public static final StreamCodec<ByteBuf, PlayerDrinkPacket> CODEC = StreamCodec.unit(PACKET);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null)
        {
            final InteractionResult result = Drinkable.attemptDrink(player.level(), player, true);
            if (result.shouldSwing())
            {
                player.swing(InteractionHand.MAIN_HAND);
            }
        }
    }
}
