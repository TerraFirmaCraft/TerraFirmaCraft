/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import java.util.Random;

import net.dries007.tfc.Artist;
import net.dries007.tfc.world.layer.framework.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class ZoomLayerTests
{
    @Test
    public void testMode()
    {
        assertEquals(1, mode(1, 1, 1, 1));
        assertEquals(1, mode(2, 1, 1, 1));
        assertEquals(1, mode(1, 3, 1, 1));
        assertEquals(1, mode(1, 1, 4, 1));
        assertEquals(1, mode(1, 1, 1, 5));
        assertEquals(1, mode(1, 1, 2, 3));
        assertEquals(1, mode(1, 3, 1, 4));
        assertEquals(1, mode(1, 3, 5, 1));
        assertEquals(1, mode(2, 1, 1, 6));
        assertEquals(1, mode(2, 1, 5, 1));
        assertEquals(1, mode(2, 6, 1, 1));
        // All other cases are random
    }

    @Test
    public void testNormal()
    {
        run("test_normal_zoom_layer", ZoomLayer.NORMAL);
    }

    @Test
    public void testFuzzy()
    {
        run("test_fuzzy_zoom_layer", ZoomLayer.FUZZY);
    }

    private int mode(int a, int b, int c, int d)
    {
        return ZoomLayer.NORMAL.choose(new AreaContext(System.currentTimeMillis()), a, b, c, d);
    }

    private void run(String name, TransformLayer zoom)
    {
        final long seed = System.currentTimeMillis();
        final Random random = new Random(seed);
        final SourceLayer source = (sourceContext, x, z) -> sourceContext.nextInt(5);
        final Artist.Typed<AreaFactory, Integer> artist = Artist.<AreaFactory, Integer>forMap(layer -> {
            final Area area = layer.get();
            return Artist.Pixel.coerceInt(area::get);
        }).centerSized(64).color(i -> Artist.Colors.COLORS[i]);

        AreaFactory layer = source.apply(random.nextLong());
        artist.draw(name + "_0", layer);

        for (int i = 1; i <= 5; i++)
        {
            layer = zoom.apply(random.nextLong(), layer);
            artist.draw(name + "_" + i, layer);
        }
    }
}
