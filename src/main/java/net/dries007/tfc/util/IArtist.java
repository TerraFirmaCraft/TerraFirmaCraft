/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

/**
 * This is an entry point for custom debug drawing utilities to be passed through standard code.
 */
@FunctionalInterface
public interface IArtist<T>
{
    static <T> IArtist<T> nope()
    {
        return (name, index, instance) -> {};
    }

    void draw(String name, int index, T instance);
}
