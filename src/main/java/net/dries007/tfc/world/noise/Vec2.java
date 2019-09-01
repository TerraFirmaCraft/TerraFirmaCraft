/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.noise;

class Vec2
{
    final float x, y;

    Vec2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    float dot(float x, float y)
    {
        return this.x * x + this.y * y;
    }
}
