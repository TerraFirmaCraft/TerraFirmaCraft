/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.registry;

import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * An abstraction around an underlying {@link RegistryHolder} which allows implementing lightweight, type-specific holder
 * objects, while avoiding having to type the double generic everywhere. This also provides convenience for type-specific
 * holders that use an underlying {@code Type<T>} object, for example:
 * {@snippet :
 * record Id<T extends BlockEntity>(DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> holder)
 *     implements RegistryHolder<BlockEntityType<?>, BlockEntityType<T>> {}
 * }
 * This then makes it easy to register objects with minimal type declarations, while still having access to both the specific type,
 * and the underlying holder (and thus registry type).
 * <p>
 * When receiving a {@link RegistryHolder}, two interfaces are provided for when access to the underlying {@code getId()} may be wanted,
 * but only one of the specific-typed supplier, or registry-typed holder are necessary
 * <ul>
 *     <li>When accessing the registry-typed holder, receivers can take a {@link HolderHolder}</li>
 *     <li>When accessing the specific-typed supplier, receivers can take a {@link IdHolder}</li>
 * </ul>
 * Finally, via {@link #holder()} this can be used to decay to either supplier, holder, or ID as necessary.
 *
 * @param <R> The type of the registry, also used to retrieve the {@link net.minecraft.core.Holder<R> Holder}
 * @param <T> The type of the object, also used to retrieve the {@link java.util.function.Supplier<T> Supplier}
 */
public interface RegistryHolder<R, T extends R> extends IdHolder<T>, HolderHolder<R>
{
    /**
     * Typically provided as a {@code DeferredHolder} field on a record.
     */
    @Override
    DeferredHolder<R, T> holder();

    @Override
    default T get()
    {
        return holder().get();
    }
}
