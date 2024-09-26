/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.network;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.doubles.Double2DoubleLinkedOpenHashMap;
import it.unimi.dsi.fastutil.doubles.Double2DoubleMaps;
import it.unimi.dsi.fastutil.doubles.Double2DoubleSortedMap;
import it.unimi.dsi.fastutil.floats.Float2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2FloatMap;
import it.unimi.dsi.fastutil.floats.Float2FloatMaps;
import it.unimi.dsi.fastutil.floats.Float2FloatSortedMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public class RotationNetwork extends Network<RotationNode>
{
    public static float lerpTowardsTarget(float currentSpeed, float targetSpeed, float requiredTorque)
    {
        if (currentSpeed < targetSpeed)
        {
            return Math.min(targetSpeed, currentSpeed + 0.01f * (1.0f / (1.0f + requiredTorque)));
        }
        else if (currentSpeed > targetSpeed)
        {
            return Math.max(targetSpeed, currentSpeed - 0.03f * (1.0f / (1.0f + requiredTorque)));
        }
        return currentSpeed;
    }

    float currentSpeed = 0;
    float targetSpeed = 0;
    float requiredTorque = 0;

    RotationNetwork(@Nullable RotationNetwork parent, long networkId)
    {
        super(networkId);
        if (parent != null)
        {
            currentSpeed = parent.currentSpeed;
            targetSpeed = parent.targetSpeed;
            requiredTorque = parent.requiredTorque;
        }
    }

    void tick()
    {
        currentSpeed = lerpTowardsTarget(currentSpeed, targetSpeed, requiredTorque);
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
        final Float2FloatSortedMap providedTorqueBySpeed = new Float2FloatLinkedOpenHashMap(nodes.size() >> 2);
        float availableTorque = 0;
        for (var entry : Long2ObjectMaps.fastIterable(nodes))
        {
            final RotationNode source = entry.getValue();
            if (source.providedSpeed() != 0)
            {
                providedTorqueBySpeed.put(source.providedSpeed(), source.providedTorque());
                availableTorque += source.providedTorque();
            }
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
    }
}
