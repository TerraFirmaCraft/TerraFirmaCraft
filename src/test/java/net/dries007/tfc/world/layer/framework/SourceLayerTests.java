/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SourceLayerTests
{
    @Test
    public void testSourceLayerIsDeterministic()
    {
        final long seed = System.currentTimeMillis();
        final Random random = new Random(seed);
        final SourceLayer source = (sourceContext, x, z) -> sourceContext.nextInt();
        final Area layer = source.apply(new AreaContext(seed)).get();

        int[] values = new int[10_000], coordinates = new int[20_000];
        for (int i = 0; i < 10_000; i++)
        {
            final int x = random.nextInt(), z = random.nextInt();
            coordinates[i << 1] = x;
            coordinates[(i << 1) | 1] = z;
            values[i] = layer.get(x, z);
        }

        for (int i = 0; i < 10_000; i++)
        {
            final int x = coordinates[i << 1], z = coordinates[(i << 1) | 1];
            Assertions.assertEquals(values[i], layer.get(x, z), () -> "Layer emitted different values at x=" + x + ", z=" + z);
        }
    }
}
