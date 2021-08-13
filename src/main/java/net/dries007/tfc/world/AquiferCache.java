package net.dries007.tfc.world;

import java.util.Arrays;
import javax.annotation.Nullable;

import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Aquifer;

import it.unimi.dsi.fastutil.HashCommon;

/**
 * A cache of aquifers, so we can share them between stages if they already exist, as the computation of repeated aquifers is expensive
 */
public class AquiferCache
{
    private final Object lock = new Object();

    private final long[] keys;
    private final Aquifer[] values;
    private final int mask;

    public AquiferCache(int size)
    {
        size = Mth.smallestEncompassingPowerOfTwo(size);

        this.mask = size - 1;
        this.keys = new long[size];
        this.values = new Aquifer[size];

        Arrays.fill(this.keys, Long.MIN_VALUE);
    }

    @Nullable
    public Aquifer getIfPresent(int x, int z)
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

    public void set(int x, int z, Aquifer aquifer)
    {
        final long key = ChunkPos.asLong(x, z);
        final int index = (int) HashCommon.mix(key) & mask;
        synchronized (lock)
        {
            keys[index] = key;
            values[index] = aquifer;
        }
    }
}
