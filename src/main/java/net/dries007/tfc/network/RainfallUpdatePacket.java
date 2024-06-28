/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.util.tracker.WorldTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

public record RainfallUpdatePacket(
    long rainStartTick,
    long rainEndTick,
    float rainIntensity
)
{
    RainfallUpdatePacket(FriendlyByteBuf buffer)
    {
        this(
            buffer.readVarLong(),
            buffer.readVarLong(),
            buffer.readFloat()
        );
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeVarLong(rainStartTick);
        buffer.writeVarLong(rainEndTick);
        buffer.writeFloat(rainIntensity);
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
