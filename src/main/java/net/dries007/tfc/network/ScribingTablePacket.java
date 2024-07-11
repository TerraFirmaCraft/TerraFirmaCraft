/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.common.container.ScribingTableContainer;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public record ScribingTablePacket(String name) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ScribingTablePacket> TYPE = PacketHandler.type("scribing_table");
    public static final StreamCodec<ByteBuf, ScribingTablePacket> CODEC = ByteBufCodecs.STRING_UTF8.map(ScribingTablePacket::new, c -> c.name);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null && player.containerMenu instanceof ScribingTableContainer container)
        {
            container.setItemName(name);
        }
    }
}
