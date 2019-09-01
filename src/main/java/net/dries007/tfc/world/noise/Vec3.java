/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.noise;

class Vec3
{
    final float x, y, z;

    Vec3(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    float dot(float x, float y, float z)
    {
        return this.x * x + this.y * y + this.z * z;
    }
}
