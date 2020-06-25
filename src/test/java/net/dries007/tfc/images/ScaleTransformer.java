package net.dries007.tfc.images;

@FunctionalInterface
public interface ScaleTransformer
{
    double scale(double value, double min, double max);
}
