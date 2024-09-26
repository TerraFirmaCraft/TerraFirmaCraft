/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.Mth;

import net.dries007.tfc.network.RotationNetworkUpdatePacket;
import net.dries007.tfc.util.network.RotationNetwork;
import net.dries007.tfc.util.network.RotationOwner;

public class ClientRotationNetworkHandler
{
    private static final Long2ObjectMap<Rotation> NETWORKS = new Long2ObjectOpenHashMap<>();
    private static int NETWORK_GENERATION = 0;

    public static void handlePacket(RotationNetworkUpdatePacket packet)
    {
        final int prevGeneration = NETWORK_GENERATION;

        NETWORK_GENERATION++;
        for (RotationNetworkUpdatePacket.Network network : packet.networks())
        {
            final Rotation rotation = NETWORKS.computeIfAbsent(network.networkId(), key -> new Rotation());

            rotation.generation = NETWORK_GENERATION;
            rotation.requiredTorque = network.torque();
            rotation.currentSpeed = network.currentSpeed();
            rotation.targetSpeed = network.targetSpeed();
        }
        if (NETWORKS.size() != packet.networks().size())
        {
            NETWORKS.values().removeIf(r -> r.generation == prevGeneration);
        }
    }

    public static void onClientTick()
    {
        for (Rotation rotation : NETWORKS.values())
        {
            rotation.targetSpeed = RotationNetwork.lerpTowardsTarget(rotation.currentSpeed, rotation.targetSpeed, rotation.requiredTorque);
            rotation.currentAngle = clampToTwoPi(rotation.currentAngle + rotation.currentSpeed);
        }
    }

    public static float getRotationAngle(RotationOwner owner, float partialTick)
    {
        return getRotationAngle(owner.getRotationNode().networkId(), partialTick);
    }

    private static float getRotationAngle(long networkId, float partialTick)
    {
        final var network = NETWORKS.get(networkId);
        return network != null
            ? clampToTwoPi(network.currentAngle + network.currentSpeed * partialTick)
            : 0;
    }

    private static float clampToTwoPi(float angle)
    {
        return angle < 0 ? Mth.TWO_PI + angle : angle;
    }

    static class Rotation
    {
        float requiredTorque = 0;
        float currentAngle = 0;
        float currentSpeed = 0;
        float targetSpeed = 0;
        int generation = 0;
    }
}
