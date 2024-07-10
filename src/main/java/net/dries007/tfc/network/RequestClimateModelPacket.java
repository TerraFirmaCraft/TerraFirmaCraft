/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;

public enum RequestClimateModelPacket implements CustomPacketPayload
{
    PACKET;

    public static final CustomPacketPayload.Type<RequestClimateModelPacket> TYPE = PacketHandler.type("request_climate_model");
    public static final StreamCodec<?, RequestClimateModelPacket> STREAM = StreamCodec.unit(PACKET);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null)
        {
            final ServerLevel level = player.serverLevel();
            final ClimateModel model = Climate.model(level);
            PacketHandler.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClimateModelPacket(model));
        }
    }
}
