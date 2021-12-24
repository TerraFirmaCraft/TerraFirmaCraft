/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.noise;

import java.util.function.ToDoubleBiFunction;

public record Kernel(double[] values, int radius)
{
    public static Kernel create(ToDoubleBiFunction<Integer, Integer> func, int radius)
    {
        final int size = radius * 2 + 1;
        final double[] array = new double[size * size];
        double sum = 0;
        for (int x = 0; x < size; x++)
        {
            for (int z = 0; z < size; z++)
            {
                final double value = func.applyAsDouble(x - radius, z - radius);
                assert value >= 0 : "Invalid kernel value: " + value + " for x = " + x + ", z = " + z;
                array[x + z * size] = value;
                sum += value;
            }
        }
        assert 0.99 < sum && sum < 1.01 : "Invalid kernel sum: " + sum + " is not ~= 1.00";
        return new Kernel(array, radius);
    }

    public int width()
    {
        return 2 * radius + 1;
    }
}
