/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import net.dries007.tfc.network.RotationNetworkUpdatePacket;
import net.dries007.tfc.util.network.NetworkHelpers;
import net.dries007.tfc.util.network.RotationNetworkPayload;
import net.dries007.tfc.util.network.RotationOwner;

public class ClientRotationNetworkHandler
{
    private static final Long2ObjectMap<Rotation> NETWORKS = new Long2ObjectOpenHashMap<>();

    public static void handlePacket(RotationNetworkUpdatePacket packet)
    {
        for (RotationNetworkPayload payload : packet.networks())
        {
            if (payload.isRemoving())
            {
                NETWORKS.remove(payload.networkId());
            }
            else
            {
                final Rotation rotation = NETWORKS.computeIfAbsent(payload.networkId(), key -> new Rotation());
                rotation.requiredTorque = payload.torqueFlag();
                rotation.currentAngle = payload.currentAngle();
                rotation.currentSpeed = payload.currentSpeed();
                rotation.targetSpeed = payload.targetSpeed();
            }
        }
    }

    public static void onClientTick()
    {
        for (Rotation rotation : NETWORKS.values())
        {
            rotation.currentSpeed = NetworkHelpers.lerpTowardsTarget(rotation.currentSpeed, rotation.targetSpeed, rotation.requiredTorque);
            rotation.currentAngle = NetworkHelpers.wrapToTwoPi(rotation.currentAngle + rotation.currentSpeed);
        }
    }

    /**
     * @return The rotation speed of the network, in {@code radians/tick}
     */
    public static float getRotationSpeed(RotationOwner owner)
    {
        final var network = NETWORKS.get(owner.getRotationNode().networkId());
        return network != null ? network.currentSpeed : 0;
    }

    public static float getRotationAngle(RotationOwner owner, float partialTick)
    {
        return getRotationAngle(owner.getRotationNode().networkId(), partialTick);
    }

    private static float getRotationAngle(long networkId, float partialTick)
    {
        final var network = NETWORKS.get(networkId);
        return network != null
            ? NetworkHelpers.clampToTwoPi(network.currentAngle + network.currentSpeed * partialTick)
            : 0;
    }

    static class Rotation
    {
        float requiredTorque = 0;
        float currentAngle = 0;
        float currentSpeed = 0;
        float targetSpeed = 0;
    }
}
