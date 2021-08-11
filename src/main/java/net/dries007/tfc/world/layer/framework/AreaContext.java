/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

/**
 * A context object used in layer creation.
 * Provides randomness seeded at each position for use in layers.
 *
 * Parts of this class are modified from {@link java.util.Random}
 */
public class AreaContext
{
    private static final long multiplier = 0x5DEECE66DL;
    private static final long addend = 0xBL;
    private static final long mask = (1L << 48) - 1;

    private final long seed;
    private long value;

    public AreaContext(long seed)
    {
        this.seed = seed;
    }

    public Area createArea(AreaSource source)
    {
        return new Area(source, 1024);
    }

    public <T> TypedArea<T> createTypedArea(TypedAreaSource<T> source)
    {
        return new TypedArea<>(source, 1024);
    }

    public void initSeed(long x, long z)
    {
        this.value = (x * 26394813L) ^ (z * 8236491231L) ^ seed;
    }

    public int choose(int first, int second)
    {
        return nextInt(2) == 0 ? first : second;
    }

    public int choose(int first, int second, int third, int fourth)
    {
        return switch (nextInt(4))
            {
                case 0 -> first;
                case 1 -> second;
                case 2 -> third;
                default -> fourth;
            };
    }

    public <A> A choose(A first, A second)
    {
        return nextInt(2) == 0 ? first : second;
    }

    public <A> A choose(A first, A second, A third, A fourth)
    {
        return switch (nextInt(4))
            {
                case 0 -> first;
                case 1 -> second;
                case 2 -> third;
                default -> fourth;
            };
    }

    public int nextInt()
    {
        return next(32);
    }

    public int nextInt(int bound)
    {
        int r = next(31);
        int m = bound - 1;
        if ((bound & m) == 0)  // i.e., bound is a power of 2
        {
            return (int) ((bound * (long) r) >> 31);
        }
        for (int u = r; u - (r = u % bound) + m < 0; u = next(31)) ;
        return r;
    }

    private int next(int bits)
    {
        value = (value * multiplier + addend) & mask;
        return (int) (value >>> (48 - bits));
    }
}
