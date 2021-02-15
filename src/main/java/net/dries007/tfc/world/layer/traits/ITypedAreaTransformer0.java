/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.traits;

/**
 * Like {  net.minecraft.world.gen.layer.traits.IAreaTransformer0}, but produces an {  TypedArea}
 */
public interface ITypedAreaTransformer0<A>
{
    default ITypedAreaFactory<A> apply(ITypedNoiseRandom<A> context)
    {
        return () -> context.createTypedResult((x, z) -> {
            context.pickRandom(x, z);
            return apply(context, x, z);
        });
    }

    A apply(ITypedNoiseRandom<A> context, int x, int z);
}
