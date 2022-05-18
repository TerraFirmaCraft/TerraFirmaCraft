/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.world;

import java.util.Random;

import net.dries007.tfc.TestHelper;
import net.dries007.tfc.world.layer.TypedZoomLayer;
import net.dries007.tfc.world.layer.ZoomLayer;
import net.dries007.tfc.world.layer.framework.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZoomLayerTests extends TestHelper
{
    @Test
    public void testZoomLayerMode()
    {
        testMode((a, b, c, d) -> ZoomLayer.NORMAL.choose(new AreaContext(seed()), a, b, c, d));
    }

    @Test
    public void testTypedZoomLayerMode()
    {
        testMode((a, b, c, d) -> new TypedZoomLayer.Normal<Integer>().choose(new AreaContext(seed()), a, b, c, d));
    }

    @Test
    public void testTypedAndDefaultNormalZoomLayerAreEquivalent()
    {
        assertSameResults(ZoomLayer.NORMAL, new TypedZoomLayer.Normal<>());
    }

    @Test
    public void testTypedAndDefaultFuzzyZoomLayerAreaEquivalent()
    {
        assertSameResults(ZoomLayer.FUZZY, new TypedZoomLayer.Fuzzy<>());
    }

    private void testMode(Mode mode)
    {
        assertEquals(1, mode.call(1, 1, 1, 1));
        assertEquals(1, mode.call(2, 1, 1, 1));
        assertEquals(1, mode.call(1, 3, 1, 1));
        assertEquals(1, mode.call(1, 1, 4, 1));
        assertEquals(1, mode.call(1, 1, 1, 5));
        assertEquals(1, mode.call(1, 1, 2, 3));
        assertEquals(1, mode.call(1, 3, 1, 4));
        assertEquals(1, mode.call(1, 3, 5, 1));
        assertEquals(1, mode.call(2, 1, 1, 6));
        assertEquals(1, mode.call(2, 1, 5, 1));
        assertEquals(1, mode.call(2, 6, 1, 1));
        // All other cases are random
    }

    private void assertSameResults(TransformLayer zoom, TypedTransformLayer<Integer> typedZoom)
    {
        // Assert that a zoom layer + TypedZoomLayer<Integer> produce the same results

        final long seed = System.currentTimeMillis();
        final Random random = new Random(seed);
        final SourceLayer source = (sourceContext, x, z) -> sourceContext.random().nextInt(5);
        final TypedSourceLayer<Integer> typedSource = (sourceContext, x, z) -> sourceContext.random().nextInt(5);

        long contextSeed = random.nextLong();
        AreaFactory layer = source.apply(contextSeed);
        TypedAreaFactory<Integer> typedLayer = typedSource.apply(contextSeed);

        for (int i = 1; i <= 5; i++)
        {
            contextSeed = random.nextLong();
            layer = zoom.apply(contextSeed, layer);
            typedLayer = typedZoom.apply(contextSeed, typedLayer);
        }

        Area area = layer.get();
        TypedArea<Integer> typedArea = typedLayer.get();

        for (int i = 0; i < 10_000; i++)
        {
            final int x = random.nextInt(), z = random.nextInt();
            Assertions.assertEquals(area.get(x, z), (int) typedArea.get(x, z), "Zoom layer and typed zoom layer behave differently");
        }
    }

    interface Mode
    {
        int call(int a, int b, int c, int d);
    }
}
