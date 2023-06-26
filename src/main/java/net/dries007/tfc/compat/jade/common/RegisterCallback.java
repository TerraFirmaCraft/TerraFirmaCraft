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
