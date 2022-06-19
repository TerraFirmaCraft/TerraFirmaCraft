/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.util.tracker.WorldTrackerCapability;

public class RainfallUpdatePacket
{
    private final long rainStartTick, rainEndTick;
    private final float rainIntensity;

    public RainfallUpdatePacket(long rainStartTick, long rainEndTick, float rainIntensity)
    {
        this.rainStartTick = rainStartTick;
        this.rainEndTick = rainEndTick;
        this.rainIntensity = rainIntensity;
    }

    RainfallUpdatePacket(FriendlyByteBuf buffer)
    {
        this.rainStartTick = buffer.readLong();
        this.rainEndTick = buffer.readLong();
        this.rainIntensity = buffer.readFloat();
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeLong(rainStartTick);
        buffer.writeLong(rainEndTick);
        buffer.writeFloat(rainIntensity);
    }

    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            final Level level = ClientHelpers.getLevel();
            if (level != null)
            {
                level.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.setWeatherData(rainStartTick, rainEndTick, rainIntensity));
            }
        });
    }
}
