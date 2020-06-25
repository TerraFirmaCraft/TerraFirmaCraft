package net.dries007.tfc.images;

@FunctionalInterface
public interface NoiseTransformer<T>
{
    NoiseSource apply(T target);
}
