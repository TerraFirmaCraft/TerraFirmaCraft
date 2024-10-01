/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.network;

import it.unimi.dsi.fastutil.floats.Float2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2FloatMaps;
import it.unimi.dsi.fastutil.floats.Float2FloatSortedMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import org.jetbrains.annotations.Nullable;

public class RotationNetwork extends Network<RotationNode>
{
    float currentAngle = 0;
    float currentSpeed = 0;
    float targetSpeed = 0;
    float requiredTorque = 0;

    private boolean active = false;

    RotationNetwork(@Nullable RotationNetwork parent, long networkId)
    {
        super(networkId);
        if (parent != null)
        {
            // This is a new network, with no nodes, which has been split off from the parent
            // Preserve the active state (as we're copying fields), along with the angle and current speed, in order to make
            // animations as seamless as possible. Don't preserve torque or target speed, since those are derived from this own
            // network's state
            active = parent.active;
            currentAngle = parent.currentAngle;
            currentSpeed = parent.currentSpeed;
        }
    }

    boolean isActive()
    {
        return active;
    }

    void tick()
    {
        currentSpeed = NetworkHelpers.lerpTowardsTarget(currentSpeed, targetSpeed, requiredTorque);
        currentAngle = NetworkHelpers.wrapToTwoPi(currentAngle + currentSpeed);
    }

    void addNodeToNetwork(RotationNode node)
    {
        requiredTorque += node.requiredTorque();
        updateTargetSpeed();
    }

    void removeNodeFromNetwork(RotationNode node)
    {
        requiredTorque -= node.requiredTorque();
        updateTargetSpeed();
    }

    void updateTargetSpeed()
    {
        final Float2FloatSortedMap providedTorqueBySpeed = new Float2FloatLinkedOpenHashMap(size() >> 2);
        float availableTorque = 0;
        for (var entry : Long2ObjectMaps.fastIterable(nodes))
        {
            final RotationNode node = entry.getValue();
            if (node.providedSpeed() != 0)
            {
                providedTorqueBySpeed.put(node.providedSpeed(), node.providedTorque());
                availableTorque += node.providedTorque();
            }
        }

        // No provided torque, so set speed to zero
        if (providedTorqueBySpeed.isEmpty())
        {
            targetSpeed = 0;
            return;
        }

        float minimumAchievableSpeed = 0;
        for (var entry : Float2FloatMaps.fastIterable(providedTorqueBySpeed))
        {
            final float nextAchievableSpeed = entry.getFloatKey();
            final float nextTorque = entry.getFloatValue();

            // If available torque is less than required torque, we cannot reach this speed
            // Interpolate down to the minimum achievable speed
            if (availableTorque < requiredTorque)
            {
                targetSpeed = (nextAchievableSpeed - minimumAchievableSpeed) * (1.0f / (1.0f + 0.3f * (requiredTorque - availableTorque))) + minimumAchievableSpeed;
                break;
            }

            // Otherwise, we decrease the torque we have available (this simulates that sources that are spinning
            // faster than the source provides are basically spinning free - they don't provide, or consume torque)
            //
            // It also means we definitely can achieve at least this speed, so adjust for next iteration
            availableTorque -= nextTorque;
            targetSpeed = minimumAchievableSpeed = nextAchievableSpeed;
        }

        // The first time we record a non-zero target speed, we set the networks' "active" flag. This ensures that the network is synced,
        // as any future possible updates need to consider this an active network, as it may have speed or leftover rotation.
        if (targetSpeed > 0)
        {
            active = true;
        }
    }
}
