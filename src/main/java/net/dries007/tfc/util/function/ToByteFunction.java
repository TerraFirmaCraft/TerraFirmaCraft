package net.dries007.tfc.util.function;

@FunctionalInterface
public interface ToByteFunction<T>
{
    byte get(T t);
}
