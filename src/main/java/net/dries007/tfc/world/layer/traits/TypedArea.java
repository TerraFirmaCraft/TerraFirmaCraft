/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.traits;

import java.util.Arrays;
import java.util.Objects;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;

/**
 * Like {  net.minecraft.world.gen.area.LazyArea} but with a generic return type.
 * There isn't an interface type like {  net.minecraft.world.gen.area.IArea} as there's no need.
 */
public class TypedArea<A>
{
    private final ITypedPixelTransformer<A> factory;
    private final long[] keys;
    private final Object[] values;
    private final int mask;

    TypedArea(ITypedPixelTransformer<A> factory, int maxCacheSize)
    {
        maxCacheSize = MathHelper.smallestEncompassingPowerOfTwo(maxCacheSize);

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
