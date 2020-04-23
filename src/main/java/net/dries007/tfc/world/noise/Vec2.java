/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
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
