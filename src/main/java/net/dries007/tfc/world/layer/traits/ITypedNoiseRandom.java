/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;

/**
 * Like {@link IExtendedNoiseRandom}
 */
public interface ITypedNoiseRandom<A> extends IExtendedNoiseRandom<FastArea>
{
    TypedArea<A> createTypedResult(ITypedPixelTransformer<A> factory);
}
