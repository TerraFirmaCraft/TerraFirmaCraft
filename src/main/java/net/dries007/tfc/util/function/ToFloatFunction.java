package net.dries007.tfc.util.function;

@FunctionalInterface
public interface ToFloatFunction<T>
{
    float applyAsFloat(T t);
}
