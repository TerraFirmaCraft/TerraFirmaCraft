/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.registry;

import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * A wrapper that provides access to the {@link Holder} of the registry type while avoiding the double generic
 * when accessing a {@link RegistryHolder}
 *
 * @param <T> The type of the registry
 * @see RegistryHolder
 */
public interface HolderHolder<T>
{
    DeferredHolder<T, ? extends T> holder();

    default ResourceKey<T> key()
    {
        return Objects.requireNonNull(holder().getKey());
    }
}
