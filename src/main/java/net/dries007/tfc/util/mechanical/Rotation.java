/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.mechanical;

import net.minecraft.core.Direction;

/**
 * @param direction A direction specifying the rotation at a given position, using a <strong>right-hand</strong> rule. For example, {@code Direction.UP} indicates a rotation on Y-axis axle, of counter-clockwise, when looking down from above.
 * @param speed The speed of rotation, measured in {@code radians / tick}.
 */
public record Rotation(Direction direction, float speed)
{
    public Rotation inDirection(Direction direction)
    {
        return new Rotation(direction, speed);
    }
}
