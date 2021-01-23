/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer.traits;

import net.minecraft.world.gen.LazyAreaLayerContext;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;

public class TypedAreaContext<A> extends FastAreaContext implements ITypedNoiseRandom<A>
{
    public TypedAreaContext(long seedIn, long seedModifierIn)
    {
        super(seedIn, seedModifierIn);
    }

    @Override
    public TypedArea<A> createTypedResult(ITypedPixelTransformer<A> factory)
    {
        return new TypedArea<>(factory, 128);
    }
}
