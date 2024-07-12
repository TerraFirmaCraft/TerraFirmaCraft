/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;


import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.player.IPlayerInfo;

/**
 * A packet that signals to the client it needs to replace the client player's food stats object
 */
public enum FoodDataReplacePacket implements CustomPacketPayload
{
    PACKET;

    public static final CustomPacketPayload.Type<FoodDataReplacePacket> TYPE = PacketHandler.type("replace_food_data");
    public static final StreamCodec<ByteBuf, FoodDataReplacePacket> CODEC = StreamCodec.unit(PACKET);

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
            IPlayerInfo.setupForPlayer(player);
        }
    }
}
