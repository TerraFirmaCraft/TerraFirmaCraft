/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Like {@link java.util.Optional}, but with a tri-state of "empty, unloaded", "empty, loaded", and "present, loaded". It also supports
 * interior mutability, so is useful to use as a cache within a data component.
 * @param <T> The type of the underlying cached value
 */
public final class CachedMut<T>
{
    public static <T> CachedMut<T> unloaded()
    {
        return new CachedMut<>(null, false); // Because we expose interior mutability, we cannot have singleton empty instances
    }

    public static <T> CachedMut<T> empty()
    {
        return new CachedMut<>(null, true); // Because we expose interior mutability, we cannot have singleton empty instances
    }

    public static <T> CachedMut<T> of(T value)
    {
        return new CachedMut<>(value, true);
    }

    private @Nullable T value;
    private boolean loaded;

    private CachedMut(@Nullable T value, boolean loaded)
    {
        this.value = value;
        this.loaded = loaded;
    }

    /**
     * @return The value of this cache, or {@code null} if the value is empty or unloaded.
     */
    @Nullable
    public T value()
    {
        return value;
    }

    /**
     * @return {@code true} if this cache is loaded.
     */
    @Contract(pure = true)
    public boolean isLoaded()
    {
        return loaded;
    }

    /**
     * @return {@code true} if this cache is loaded, and present.
     * @throws AssertionError if the cache is not loaded.
     */
    @Contract(pure = true)
    public boolean isPresent()
    {
        assert loaded;
        return value != null;
    }

    /**
     * Loads this cache with the provided {@code value}.
     * @throws AssertionError if the cache is already loaded
     */
    public void load(@Nullable T value)
    {
        assert !this.loaded;
        this.value = value;
        this.loaded = true;
    }

    /**
     * Unloads this cache, resetting it to an unloaded state.
     */
    public void unload()
    {
        this.value = null;
        this.loaded = false;
    }

    @Override
    public String toString()
    {
        return loaded ? (value != null ? value.toString() : "<empty>") : "<unloaded>";
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof CachedMut; // We cannot fundamentally compare
    }

    @Override
    public int hashCode()
    {
        return 0;
    }
}
