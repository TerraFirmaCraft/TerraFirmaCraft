/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

public final class Vec4
{
    public final float x, y, z, w;

    public Vec4(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public final float dot(float x, float y, float z, float w)
    {
        return this.x * x + this.y * y + this.z * z + this.z * z;
    }
}