/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.traits;

import java.util.Arrays;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

import it.unimi.dsi.fastutil.HashCommon;

/**
 * A variant of {  LazyArea} which implements a non-synchronized, lossy cache
 */
public class FastArea implements IArea
{
    private final IPixelTransformer factory;
    private final long[] keys;
    private final int[] values;
    private final int mask;

    public FastArea(IPixelTransformer factory, int maxCacheSize)
    {
        maxCacheSize = MathHelper.smallestEncompassingPowerOfTwo(maxCacheSize);

        this.factory = factory;
        this.keys = new long[maxCacheSize];
        this.values = new int[maxCacheSize];
        this.mask = maxCacheSize - 1;

        Arrays.fill(this.keys, Long.MIN_VALUE);
    }

    @Override
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
            final int value = factory.apply(x, z);
            values[index] = value;
            keys[index] = key;
            return value;
        }
    }
}
