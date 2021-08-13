/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

import java.util.Arrays;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

import it.unimi.dsi.fastutil.HashCommon;

/**
 * A caching wrapper around a {@link AreaSource}. Created from the result of a stack of layers.
 */
public class Area
{
    private final AreaSource source;
    private final long[] keys;
    private final int[] values;
    private final int mask;

    public Area(AreaSource source, int maxCacheSize)
    {
        maxCacheSize = MathHelper.smallestEncompassingPowerOfTwo(maxCacheSize);

        this.source = source;
        this.keys = new long[maxCacheSize];
        this.values = new int[maxCacheSize];
        this.mask = maxCacheSize - 1;

        Arrays.fill(this.keys, Long.MIN_VALUE);
    }

    public int get(int x, int z)
    {
        final long key = ChunkPos.asLong(x, z);
        final int index = (int) HashCommon.mix(key) & mask;
        if (keys[index] == key)
        {
            return values[index];
        }
        else
        {
            final int value = source.apply(x, z);
            values[index] = value;
            keys[index] = key;
            return value;
        }
    }
}
