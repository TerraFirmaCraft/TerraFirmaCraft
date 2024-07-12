/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.registry;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * A wrapper that provides both {@link Supplier} of the specific type, and {@link ResourceLocation} id access to the underlying
 * holder object, but while removing the double-generic of {@link DeferredHolder}
 * <p>
 * This is typically either used as the receiver type for a method that does not need access to the underlying holder or registry
 * type, or implemented via {@link RegistryHolder} on a type-specific holder object (thus also removing the double generic)
 *
 * @param <T> The type of the object
 * @see RegistryHolder
 */
public interface IdHolder<T> extends Supplier<T>
{
    DeferredHolder<? super T, T> holder();

    default ResourceLocation getId()
    {
        return holder().getId();
    }
}
