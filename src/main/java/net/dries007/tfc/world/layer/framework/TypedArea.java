/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

import java.util.Arrays;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.util.Mth;

import it.unimi.dsi.fastutil.HashCommon;

/**
 * @see Area
 */
public class TypedArea<A>
{
    private final TypedAreaSource<A> factory;
    private final long[] keys;
    private final Object[] values;
    private final int mask;

    public TypedArea(TypedAreaSource<A> factory, int maxCacheSize)
    {
        maxCacheSize = Mth.smallestEncompassingPowerOfTwo(maxCacheSize);

        this.factory = factory;
        this.keys = new long[maxCacheSize];
        this.values = new Object[maxCacheSize];
        this.mask = maxCacheSize - 1;

        Arrays.fill(this.keys, Long.MIN_VALUE);
    }

    @SuppressWarnings("unchecked")
    public A get(int x, int z)
    {
        final long key = ChunkPos.asLong(x, z);
        final int index = (int) HashCommon.mix(key) & mask;
        if (keys[index] == key)
        {
            return (A) values[index];
        }
        else
        {
            final A value = factory.apply(x, z);
            values[index] = value;
            keys[index] = key;
            return value;
        }
    }
}
