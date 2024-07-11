/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.util.tracker.WorldTracker;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;

public record RainfallUpdatePacket(
    long rainStartTick,
    long rainEndTick,
    float rainIntensity
) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<RainfallUpdatePacket> TYPE = PacketHandler.type("rainfall_update");
    public static final StreamCodec<ByteBuf, RainfallUpdatePacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_LONG, c -> c.rainStartTick,
        ByteBufCodecs.VAR_LONG, c -> c.rainEndTick,
        ByteBufCodecs.FLOAT, c -> c.rainIntensity,
        RainfallUpdatePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle()
    {
        final Level level = ClientHelpers.getLevel();
        if (level != null)
        {
            WorldTracker.get(level).setWeatherData(rainStartTick, rainEndTick, rainIntensity);
        }
    }
}
