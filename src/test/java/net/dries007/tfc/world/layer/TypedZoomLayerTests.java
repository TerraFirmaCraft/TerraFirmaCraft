/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import java.util.Random;

import net.dries007.tfc.world.layer.framework.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TypedZoomLayerTests
{
    @Test
    public void testTypedNormalZoomLayer()
    {
        run(ZoomLayer.NORMAL, new TypedZoomLayer.Normal<>());
    }

    @Test
    public void testTypedFuzzyZoomLayer()
    {
        run(ZoomLayer.FUZZY, new TypedZoomLayer.Fuzzy<>());
    }

    private void run(TransformLayer zoom, TypedTransformLayer<Integer> typedZoom)
    {
        // Assert that a zoom layer + TypedZoomLayer<Integer> produce the same results

        final long seed = System.currentTimeMillis();
        final Random random = new Random(seed);
        final SourceLayer source = (sourceContext, x, z) -> sourceContext.nextInt(5);
        final TypedSourceLayer<Integer> typedSource = (sourceContext, x, z) -> sourceContext.nextInt(5);

        AreaContext context = new AreaContext(random.nextLong());
        AreaFactory layer = source.apply(context);
        TypedAreaFactory<Integer> typedLayer = typedSource.apply(context);

        for (int i = 1; i <= 5; i++)
        {
            context = new AreaContext(random.nextLong());
            layer = zoom.apply(context, layer);
            typedLayer = typedZoom.apply(context, typedLayer);
        }

        Area area = layer.get();
        TypedArea<Integer> typedArea = typedLayer.get();

        for (int i = 0; i < 10_000; i++)
        {
            final int x = random.nextInt(), z = random.nextInt();
            Assertions.assertEquals(area.get(x, z), (int) typedArea.get(x, z), "Zoom layer and typed zoom layer behave differently");
        }
    }
}
