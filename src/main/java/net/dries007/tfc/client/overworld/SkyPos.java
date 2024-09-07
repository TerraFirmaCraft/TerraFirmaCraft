/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.overworld;

import net.minecraft.util.Mth;

/**
 * A pair of angles which is sufficient to determine the position of an object in the sky. Both angles
 * are assumed to be in radians.
 *
 * @param zenith  The angle between the vertical (positive Y) and the position of the object. Always in the range {@code [0, pi]}
 * @param azimuth The angle from the horizontal and the position of the object. This is using a clockwise-from-due-north convention, so north = 0°, east = 90°, etc. Always in the range {@code [0, 2pi]}
 */
public record SkyPos(float zenith, float azimuth)
{
    /**
     * @return A new position consisting of the two provided angles
     */
    public static SkyPos of(float zenith, float azimuth)
    {
        return new SkyPos(zenith, azimuth);
    }

    @Override
    public String toString()
    {
        return "Position[zenith=%.1f°, azimuth=%.1f°]".formatted(Mth.RAD_TO_DEG * zenith, Mth.RAD_TO_DEG * azimuth);
    }
}
