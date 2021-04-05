/*
 * This was modified from the <a href="https://github.com/Gegy/overworld-two">Overworld Two</a> Mod
 * It is included here under the library clause of the LGPL v3.0
 *
 * Copyright (c) Gegy, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package net.dries007.tfc.world.biome;

import java.util.Arrays;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;

import it.unimi.dsi.fastutil.HashCommon;

public class BiomeCache
{
    private final long[] keys;
    private final Biome[] values;

    private final int mask;
    private final BiomeManager.IBiomeReader source;

    public BiomeCache(int size, BiomeManager.IBiomeReader source)
    {
        size = MathHelper.smallestEncompassingPowerOfTwo(size);

        this.source = source;
        this.mask = size - 1;
        this.keys = new long[size];
        this.values = new Biome[size];

        Arrays.fill(this.keys, Long.MIN_VALUE);
    }

    public Biome get(int x, int z)
    {
        final long key = ChunkPos.asLong(x, z);
        final int index = (int) HashCommon.mix(key) & mask;
        if (keys[index] == key)
        {
            return values[index];
        }
        else
        {
            final Biome value = source.getNoiseBiome(x, 0, z);
            values[index] = value;
            keys[index] = key;
            return value;
        }
    }
}
