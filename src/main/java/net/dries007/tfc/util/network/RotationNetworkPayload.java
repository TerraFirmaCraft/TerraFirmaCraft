/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RotationNetworkPayload(
    long networkId,
    float torqueFlag,
    float currentAngle,
    float currentSpeed,
    float targetSpeed
)
{
    // Optimized to save network bytes in two special cases - removing networks (invalidating client-side cache), and
    // syncing empty networks (non-rotating ones), which we just don't sync at all.
    private static final float REMOVE_NETWORK = -1;

    public static final StreamCodec<FriendlyByteBuf, RotationNetworkPayload> CODEC = StreamCodec.of((buffer, value) -> {
        buffer.writeVarLong(value.networkId);
        buffer.writeFloat(value.torqueFlag);
        if (value.torqueFlag > 0)
        {
            buffer.writeFloat(value.currentAngle);
            buffer.writeFloat(value.currentSpeed);
            buffer.writeFloat(value.targetSpeed);
        }
    }, buffer -> {
        final long networkId = buffer.readVarLong();
        final float torqueFlag = buffer.readFloat();
        return torqueFlag > 0 ?
            new RotationNetworkPayload(networkId, torqueFlag, buffer.readFloat(), buffer.readFloat(), buffer.readFloat())
            : new RotationNetworkPayload(networkId, torqueFlag, 0, 0, 0);
    });

    public static RotationNetworkPayload remove(long networkId)
    {
        return new RotationNetworkPayload(networkId, REMOVE_NETWORK, 0, 0, 0);
    }

    public RotationNetworkPayload(RotationNetwork network)
    {
        this(network.networkId, network.requiredTorque, network.currentAngle, network.currentSpeed, network.targetSpeed);
    }

    public boolean isRemoving()
    {
        return torqueFlag == REMOVE_NETWORK;
    }
}
