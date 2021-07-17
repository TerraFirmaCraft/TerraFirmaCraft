/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.traits;

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
