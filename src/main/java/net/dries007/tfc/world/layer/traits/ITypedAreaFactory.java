/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.traits;

/**
 * Like {@link net.minecraft.world.gen.area.IAreaFactory} but using {@link TypedArea}
 */
public interface ITypedAreaFactory<A>
{
    TypedArea<A> make();
}
