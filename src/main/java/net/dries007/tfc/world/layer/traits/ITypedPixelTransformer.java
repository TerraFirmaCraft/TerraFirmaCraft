/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.traits;

/**
 * This is a variant of {@link net.minecraft.world.gen.layer.traits.IPixelTransformer} with a generic return type, NOT a variant of {@link net.minecraft.world.gen.area.IArea}. For typed areas everything should reference {@link TypedArea <A>} directly.
 */
public interface ITypedPixelTransformer<A>
{
    A apply(int x, int z);
}
