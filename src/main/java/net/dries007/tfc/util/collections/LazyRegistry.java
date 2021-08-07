package net.dries007.tfc.util.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraftforge.common.util.Lazy;

/**
 * This mimics the behavior of a {@link net.minecraftforge.registries.DeferredRegister} but for a simple non-forge registry, which just uses {@link net.minecraftforge.common.util.Lazy} semantics.
 */
public class LazyRegistry<T>
{
    private List<Delayed<? extends T>> values;
    private boolean registered;

    public LazyRegistry()
    {
        this.values = new ArrayList<>();
        this.registered = false;
    }

    public void registerAll()
    {
        for (Delayed<? extends T> lazy : values)
        {
            lazy.set();
        }
        registered = true;
        values = null;
    }

    public <V extends T> Lazy<V> register(Supplier<V> factory)
    {
        final Delayed<V> lazy = new Delayed<>();
        lazy.factory = factory;
        lazy.value = null;
        return lazy;
    }

    private class Delayed<V extends T> implements Lazy<V>
    {
        private Supplier<V> factory;
        private V value;

        @Override
        public V get()
        {
            if (registered)
            {
                return value;
            }
            throw new RuntimeException("Tried to access LazyRegistry element before elements were registered");
        }

        private void set()
        {
            value = factory.get();
            factory = null;
        }
    }
}
