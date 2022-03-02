package net.dries007.tfc.client.model;

import net.minecraft.util.ToFloatFunction;

import net.dries007.tfc.util.Helpers;

public enum Easing
{
    LINEAR(x -> x),
    EASE_IN_CUBIC(Helpers::cube),
    EASE_OUT_CUBIC(x -> 1 - Helpers.cube(1 - x)),
    EASE_IN_OUT_CUBIC(x -> x < 0.5f ? 4 * x * x * x : 1 - Helpers.cube(-2 * x + 2) / 2);

    private final ToFloatFunction<Float> function;

    Easing(ToFloatFunction<Float> function)
    {
        this.function = function;
    }

    /**
     * Expects a value [0, 1]
     */
    public float apply(float x)
    {
        return function.apply(x);
    }
}
