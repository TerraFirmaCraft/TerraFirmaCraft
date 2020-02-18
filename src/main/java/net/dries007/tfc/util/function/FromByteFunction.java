package net.dries007.tfc.util.function;

@FunctionalInterface
public interface FromByteFunction<T>
{
    T get(byte b);
}
