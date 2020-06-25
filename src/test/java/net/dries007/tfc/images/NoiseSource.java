package net.dries007.tfc.images;

@FunctionalInterface
public interface NoiseSource
{
    double apply(double x, double y);
}
