/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jade.common;

import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.util.Helpers;

@FunctionalInterface
public interface RegisterCallback<T, C>
{
    default void register(String name, T tooltip, Class<? extends C> thing)
    {
        register(Helpers.identifier(name), tooltip, thing);
    }

    void register(ResourceLocation name, T tooltip, Class<? extends C> thing);
}
