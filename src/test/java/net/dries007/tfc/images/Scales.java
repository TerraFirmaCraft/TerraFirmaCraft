package net.dries007.tfc.images;

public final class Scales
{
    public static final ScaleTransformer EXACT = (value, min, max) -> value;
    public static final ScaleTransformer NEAREST_INT = (value, min, max) -> (int) (value + 0.5);
    public static final ScaleTransformer DYNAMIC_RANGE = (value, min, max) -> max == min ? value : (value - min) / (max - min);

    public static ScaleTransformer fixedRange(double fixedMin, double fixedMax)
    {
        return (value, min, max) -> DYNAMIC_RANGE.scale(value, fixedMin, fixedMax);
    }
}
