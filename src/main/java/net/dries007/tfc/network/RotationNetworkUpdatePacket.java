/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.List;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public record RotationNetworkUpdatePacket(List<Network> networks) implements CustomPacketPayload
{
    public static final Type<RotationNetworkUpdatePacket> TYPE = PacketHandler.type("rotation_update");
    public static final StreamCodec<ByteBuf, RotationNetworkUpdatePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, c -> c.networkId,
            ByteBufCodecs.FLOAT, c -> c.torque,
            ByteBufCodecs.FLOAT, c -> c.currentSpeed,
            ByteBufCodecs.FLOAT, c -> c.targetSpeed,
            Network::new
        )
        .apply(ByteBufCodecs.list())
        .map(RotationNetworkUpdatePacket::new, RotationNetworkUpdatePacket::networks);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public record Network(
        long networkId,
        float torque,
        float currentSpeed,
        float targetSpeed
    ) {}
}
