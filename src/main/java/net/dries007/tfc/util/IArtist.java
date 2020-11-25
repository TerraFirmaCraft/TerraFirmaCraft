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
