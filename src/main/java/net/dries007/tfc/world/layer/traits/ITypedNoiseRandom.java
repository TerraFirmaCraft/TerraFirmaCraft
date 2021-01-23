/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.LazyArea;

/**
 * Like {@link IExtendedNoiseRandom}
 */
public interface ITypedNoiseRandom<A> extends IExtendedNoiseRandom<FastArea>
{
    TypedArea<A> createTypedResult(ITypedPixelTransformer<A> factory);
}
