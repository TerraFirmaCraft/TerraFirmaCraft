/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer.traits;

import net.minecraft.world.gen.layer.traits.IDimTransformer;

/**
 * Like {@link net.minecraft.world.gen.layer.traits.IAreaTransformer1} but with {@link TypedArea}
 */
public interface ITypedAreaTransformer1<A> extends IDimTransformer
{
    default ITypedAreaFactory<A> run(ITypedNoiseRandom<A> context, ITypedAreaFactory<A> areaFactory)
    {
        return () -> {
            TypedArea<A> area = areaFactory.make();
            return context.createTypedResult((x, z) -> {
                context.initRandom(x, z);
                return apply(context, area, x, z);
            });
        };
    }

    A apply(ITypedNoiseRandom<A> context, TypedArea<A> area, int x, int z);
}
