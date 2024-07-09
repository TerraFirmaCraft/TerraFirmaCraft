package net.dries007.tfc.util.registry;

import java.util.function.Supplier;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * An interface to make registration types less annoying. Implemented on a per-type record basis as such:
 * {@snippet :
 * record Id<T>(DeferredHolder<Block, T> holder) implements RegistryHolder<Block, T> {}
 * }
 * This then makes it easy to register objects with minimal types, while still having access to both the specific type,
 * and the underlying holder (and thus registry type).
 *
 * @param <R> The type of the registry, also used to retrieve the {@link net.minecraft.core.Holder<R>}
 * @param <T> The type of the object, also used to retrieve the {@link java.util.function.Supplier<T>}
 */
public interface RegistryHolder<R, T extends R> extends Supplier<R>
{
    DeferredHolder<R, T> holder();

    @Override
    default R get()
    {
        return holder().get();
    }
}
