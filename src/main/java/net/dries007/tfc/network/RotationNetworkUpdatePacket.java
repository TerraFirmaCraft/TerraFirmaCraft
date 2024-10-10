/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.dries007.tfc.util.network.RotationNetworkPayload;


public record RotationNetworkUpdatePacket(List<RotationNetworkPayload> networks) implements CustomPacketPayload
{
    public static final Type<RotationNetworkUpdatePacket> TYPE = PacketHandler.type("rotation_update");
    public static final StreamCodec<FriendlyByteBuf, RotationNetworkUpdatePacket> CODEC = RotationNetworkPayload.CODEC
        .apply(ByteBufCodecs.list())
        .map(RotationNetworkUpdatePacket::new, RotationNetworkUpdatePacket::networks);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
