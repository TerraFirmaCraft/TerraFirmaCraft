/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer.traits;

import net.minecraft.world.gen.LazyAreaLayerContext;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;

public class LazyTypedAreaLayerContext<A> extends LazyAreaLayerContext implements ITypedNoiseRandom<A>
{
    private final int maxCacheSizeIn;
    private final Long2ObjectLinkedOpenHashMap<A> cache;

    public LazyTypedAreaLayerContext(int maxCacheSizeIn, long seedIn, long seedModifierIn)
    {
        super(maxCacheSizeIn, seedIn, seedModifierIn);
        this.maxCacheSizeIn = maxCacheSizeIn;
        this.cache = new Long2ObjectLinkedOpenHashMap<>();
    }

    @Override
    public LazyTypedArea<A> createTypedResult(ITypedPixelTransformer<A> factory)
    {
        return new LazyTypedArea<>(cache, maxCacheSizeIn, factory);
    }

    @Override
    public LazyTypedArea<A> createTypedResult(ITypedPixelTransformer<A> factory, LazyTypedArea<?> area)
    {
        return new LazyTypedArea<>(cache, Math.min(1024, area.getMaxCache() * 4), factory);
    }
}
