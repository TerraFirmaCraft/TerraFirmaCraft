/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.Arrays;
import javax.annotation.Nullable;

import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;

import it.unimi.dsi.fastutil.HashCommon;

/**
 * A concurrent (safe to read and write between multiple threads) positional based, lossy, cache.
 */
public class FastConcurrentCache<T>
{
    private final Object lock = new Object();

    private final long[] keys;
    private final T[] values;
    private final int mask;

    @SuppressWarnings("unchecked")
    public FastConcurrentCache(int size)
    {
        size = Mth.smallestEncompassingPowerOfTwo(size);

        this.mask = size - 1;
        this.keys = new long[size];
        this.values = (T[]) new Object[size];

        Arrays.fill(this.keys, Long.MIN_VALUE);
    }

    @Nullable
    public T getIfPresent(int x, int z)
    {
        final long key = ChunkPos.asLong(x, z);
        final int index = (int) HashCommon.mix(key) & mask;
        synchronized (lock)
        {
            if (keys[index] == key)
            {
                return values[index];
            }
        }
        return null;
    }

    public void set(int x, int z, T value)
    {
        final long key = ChunkPos.asLong(x, z);
        final int index = (int) HashCommon.mix(key) & mask;
        synchronized (lock)
        {
            keys[index] = key;
            values[index] = value;
        }
    }
}
