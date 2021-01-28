/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

public final class Vec2
{
    public final float x, y;

    public Vec2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public final float dot(float x, float y)
    {
        return this.x * x + this.y * y;
    }
}