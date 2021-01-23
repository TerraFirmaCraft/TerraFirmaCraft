/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer.traits;

/**
 * Like {@link net.minecraft.world.gen.layer.traits.IAreaTransformer0}, but produces an {@link TypedArea}
 */
public interface ITypedAreaTransformer0<A>
{
    default ITypedAreaFactory<A> apply(ITypedNoiseRandom<A> context)
    {
        return () -> context.createTypedResult((x, z) -> {
            context.initRandom(x, z);
            return apply(context, x, z);
        });
    }

    A apply(ITypedNoiseRandom<A> context, int x, int z);
}
