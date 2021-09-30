/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.river;

import java.awt.*;
import java.util.List;
import java.util.stream.IntStream;

import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.SimpleRandomSource;

import net.dries007.tfc.Artist;
import net.dries007.tfc.TestHelper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static net.dries007.tfc.TestHelper.seed;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MidpointFractalTests
{
    @RepeatedTest(10)
    public void testIntersectImpliesMaybeIntersect()
    {
        long seed = seed();
        RandomSource random = new SimpleRandomSource(seed);
        float x0 = random.nextFloat(), y0 = random.nextFloat(), x1 = random.nextFloat(), y1 = random.nextFloat();
        MidpointFractal fractal = new MidpointFractal(random, random.nextInt(10), x0, y0, x1, y1);
        for (float x = 0; x < 1; x += 0.01f)
        {
            for (float y = 0; y < 1; y += 0.01f)
            {
                final boolean intersect = fractal.intersect(x, y, 0.03f);
                final boolean maybe = fractal.maybeIntersect(x, y, 0.03f);
                assertTrue(!intersect || maybe, "Seed: " + seed + " points: " + x + " , " + y); // intersect -> maybe
            }
        }
    }

    @Test
    @Disabled
    public void drawMidpointExample()
    {
        long seed = -7794280502563316471L;
        RandomSource random = new SimpleRandomSource(seed);
        float x0 = random.nextFloat(), y0 = random.nextFloat(), x1 = random.nextFloat(), y1 = random.nextFloat();
        int bisects = random.nextInt(10);
        MidpointFractal fractal = new MidpointFractal(random, bisects, x0, y0, x1, y1);

        List<MidpointFractal> prev = IntStream.range(0, bisects).mapToObj(i -> {
            RandomSource r = new SimpleRandomSource(seed);
            for (int j = 0; j < 4; j++) r.nextFloat();
            r.nextInt(4);
            return new MidpointFractal(r, i, x0, y0, x1, y1);
        }).toList();

        Artist.custom((v, g) -> {

            for (int i = 0; i < 1000; i++)
            {
                for (int j = 0; j < 1000; j++)
                {
                    if (fractal.maybeIntersect(0.001f * i, 0.001f * j, 0.03f))
                    {
                        g.setColor(Color.GRAY);
                        g.drawRect(i, j, 1, 1);
                    }
                    if (fractal.intersect(0.001f * i, 0.001f * j, 0.03f))
                    {
                        g.setColor(Color.LIGHT_GRAY);
                        g.drawRect(i, j, 1, 1);
                    }
                }
            }

            g.setColor(Color.RED);
            prev.forEach(m -> TestHelper.draw(m, g, 1000));
            g.setColor(Color.WHITE);
            TestHelper.draw(fractal, g, 1000);

        }).draw("midpoint_example");
    }
}
