/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer.traits;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.area.LazyArea;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;

/**
 * Like {@link net.minecraft.world.gen.area.LazyArea} but with a generic return type.
 * There isn't an interface type like {@link net.minecraft.world.gen.area.IArea} as there's no need.
 */
public class LazyTypedArea<A>
{
    private final Long2ObjectLinkedOpenHashMap<A> cache;
    private final int maxCache;
    private final ITypedPixelTransformer<A> factory;
    private LazyArea lazyLazyArea;

    LazyTypedArea(Long2ObjectLinkedOpenHashMap<A> cache, int maxCache, ITypedPixelTransformer<A> factory)
    {
        this.cache = cache;
        this.maxCache = maxCache;
        this.factory = factory;
    }

    public A get(int x, int z)
    {
        long positionHash = ChunkPos.asLong(x, z);
        synchronized (cache)
        {
            A area = cache.get(positionHash);
            if (area == null)
            {
                area = factory.apply(x, z);
                cache.put(positionHash, area);
                if (cache.size() > maxCache)
                {
                    for (int i = 0; i < maxCache / 16; ++i)
                    {
                        cache.removeFirst();
                    }
                }
            }
            return area;
        }
    }

    public int getMaxCache()
    {
        return maxCache;
    }

    /**
     * Only used for vanilla methods in {@link net.minecraft.world.gen.IExtendedNoiseRandom} which require the max cache size, unused otherwise.
     */
    public LazyArea asLazyArea()
    {
        if (lazyLazyArea == null)
        {
            lazyLazyArea = new LazyArea(new Long2IntLinkedOpenHashMap(), maxCache, (x, z) -> 0);
        }
        return lazyLazyArea;
    }
}
