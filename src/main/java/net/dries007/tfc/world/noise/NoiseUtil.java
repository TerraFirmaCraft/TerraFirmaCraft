/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.noise;

public final class NoiseUtil
{
    public static float lerp(float start, float end, float t)
    {
        return start * (1 - t) + end * t;
    }

    public static int fastFloor(float f)
    {
        return f < 0 ? (int) f - 1 : (int) f;
    }

    public static int fastRound(float f)
    {
        return f >= 0 ? (int) (f + 0.5f) : (int) (f - 0.5f);
    }

    public static int hash(long seed, int x, int y)
    {
        seed ^= 1619 * x;
        seed ^= 31337 * y;
        seed = seed * seed * seed * 60493;
        seed = (seed >> 13) ^ seed;
        return (int) seed;
    }

    public static int hash(long seed, int x, int y, int z)
    {
        seed ^= 1619 * x;
        seed ^= 31337 * y;
        seed ^= 6971 * z;
        seed = seed * seed * seed * 60493;
        seed = (seed >> 13) ^ seed;
        return (int) seed;
    }
}
