/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer.traits;

/**
 * This is a variant of {@link net.minecraft.world.gen.layer.traits.IPixelTransformer} with a generic return type, NOT a variant of {@link net.minecraft.world.gen.area.IArea}. For typed areas everything should reference {@link LazyTypedArea<A>} directly.
 */
public interface ITypedPixelTransformer<A>
{
    A apply(int x, int z);
}
