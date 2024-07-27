/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component;

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

    @Nullable
    public T value()
    {
        return value;
    }

    public boolean isPresent()
    {
        return value != null;
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    public void load(@Nullable T value)
    {
        assert !this.loaded;
        this.value = value;
        this.loaded = true;
    }

    public void unload()
    {
        this.value = null;
        this.loaded = false;
    }

    @Override
    public String toString()
    {
        return isLoaded() ? (isPresent() ? value.toString() : "<empty>") : "<unloaded>";
    }
}
