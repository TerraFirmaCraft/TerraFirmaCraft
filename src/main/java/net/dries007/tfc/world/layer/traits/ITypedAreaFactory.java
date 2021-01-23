/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer.traits;

/**
 * Like {@link net.minecraft.world.gen.area.IAreaFactory} but using {@link TypedArea}
 */
public interface ITypedAreaFactory<A>
{
    TypedArea<A> make();
}
