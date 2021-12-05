/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import it.unimi.dsi.fastutil.HashCommon;

public class AreaContext
{
    private final long seed;
    private final RandomSource random;

    public AreaContext(long seed)
    {
        this.seed = HashCommon.murmurHash3(seed);
        this.random = new XoroshiroRandomSource(seed);
    }

    public RandomSource random()
    {
        return random;
    }

    public void setSeed(long x, long z)
    {
        random.setSeed(((x * 501125321L) ^ (z * 1136930381L) ^ seed) * 0x27d4eb2d);
    }

    public int choose(int first, int second)
    {
        return random.nextBoolean() ? first : second;
    }

    public int choose(int first, int second, int third, int fourth)
    {
        return switch (random.nextInt(4))
            {
                case 0 -> first;
                case 1 -> second;
                case 2 -> third;
                default -> fourth;
            };
    }

    public int choose(int[] choices)
    {
        return choices[random.nextInt(choices.length)];
    }

    public <A> A choose(A first, A second)
    {
        return random.nextBoolean() ? first : second;
    }

    public <A> A choose(A first, A second, A third, A fourth)
    {
        return switch (random.nextInt(4))
            {
                case 0 -> first;
                case 1 -> second;
                case 2 -> third;
                default -> fourth;
            };
    }

    public <A> A choose(A[] choices)
    {
        return choices[random.nextInt(choices.length)];
    }
}
