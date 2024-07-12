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
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.player.IPlayerInfo;

public enum CycleChiselModePacket implements CustomPacketPayload
{
    PACKET;

    public static final CustomPacketPayload.Type<CycleChiselModePacket> TYPE = PacketHandler.type("cycle_chisel_mode");
    public static final StreamCodec<ByteBuf, CycleChiselModePacket> CODEC = StreamCodec.unit(PACKET);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null)
        {
            IPlayerInfo.get(player).cycleChiselMode();
        }
    }
}
