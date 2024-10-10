/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.network;

import java.util.Arrays;
import java.util.EnumSet;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public final class NetworkHelpers
{
    /**
     * Conversion factor from radians/tick to rotations/minute:
     * {@code radians/tick x 20 ticks/second x 60 seconds/minute div 2pi radians/rotation}
     */
    public static final float SPEED_TO_RPM = (20 * 60 / Mth.TWO_PI);

    /**
     * @return The set of connections in a given axis
     */
    public static EnumSet<Direction> ofAxis(Direction.Axis axis)
    {
        return EnumSet.of(
            Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE),
            Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE)
        );
    }

    /**
     * @return The set of connections (possibly empty) of {@code directions}.
     */
    public static EnumSet<Direction> of(Direction... directions)
    {
        return directions.length == 0 ? EnumSet.noneOf(Direction.class) : EnumSet.copyOf(Arrays.asList(directions));
    }

    public static Direction readAxis(CompoundTag tag, String key, Direction.Axis axis)
    {
        return Direction.fromAxisAndDirection(axis, tag.getByte(key) > 0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
    }

    public static void saveAxis(CompoundTag tag, String key, Direction direction)
    {
        tag.putByte(key, (byte) direction.getAxisDirection().getStep());
    }

    /**
     * @return The direction in {@code axis}, if {@code positive} then in the positive axis direction, otherwise negative.
     */
    public static Direction getDirection(Direction.Axis axis, boolean positive)
    {
        return Direction.fromAxisAndDirection(axis, positive ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
    }

    /**
     * Interpolate {@code currentSpeed} towards {@code targetSpeed} at a rate proportional to the inverse of {@code requiredTorque}.
     */
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

    public static float clampToTwoPi(float angle)
    {
        return angle < 0 ? Mth.TWO_PI + angle : angle;
    }

    public static float wrapToTwoPi(float angle)
    {
        return angle > Mth.TWO_PI ? angle - Mth.TWO_PI : angle;
    }
}
