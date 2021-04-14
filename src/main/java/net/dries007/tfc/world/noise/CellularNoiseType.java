/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

/**
 * Important to note: all distances are concerning square distances!
 * F1 / F2 are used by 2D cell noise. F3 is only calculated by 3D cell noise.
 */
public enum CellularNoiseType
{
    VALUE,
    F1,
    F2,
    F3,
    F1_DIV_F2,
    F1_DIV_F3,
    F1_MUL_F2;

    public float apply(float f1, float f2, float f3, int closestHash)
    {
        switch (this)
        {
            case VALUE:
                return closestHash * (1 / 2147483648.0f);
            case F1:
                return f1;
            case F2:
                return f2;
            case F3:
                return f3;
            case F1_MUL_F2:
                return f1 * f2 * 0.5f - 1;
            case F1_DIV_F2:
                return f1 / f2 - 1;
            case F1_DIV_F3:
                return f1 / f3 - 1;
        }
        throw new IllegalStateException("Unknown CellularNoiseType: " + name());
    }
}
