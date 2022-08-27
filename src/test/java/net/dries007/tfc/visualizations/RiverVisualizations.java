/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.visualizations;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import net.dries007.tfc.Artist;
import net.dries007.tfc.TestHelper;
import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.layer.Plate;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.AreaFactory;
import net.dries007.tfc.world.layer.framework.TypedAreaFactory;
import net.dries007.tfc.world.river.MidpointFractal;
import net.dries007.tfc.world.river.RiverFractal;
import net.dries007.tfc.world.river.RiverHelpers;
import net.dries007.tfc.world.river.Watershed;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class RiverVisualizations extends TestHelper
{
    public static final Artist.Custom<MidpointFractal> MIDPOINT_FRACTAL = Artist.custom((fractal, g) -> draw(fractal, g, 1));
    public static final Artist.Custom<List<MidpointFractal>> MULTI_MIDPOINT_FRACTAL = Artist.custom((fractal, g) -> fractal.forEach(f -> draw(f, g, 1)));
    public static final Artist.Custom<RiverFractal> RIVER_FRACTAL = Artist.custom((fractal, g) -> draw(fractal, g, 1));
    public static final Artist.Custom<List<RiverFractal>> MULTI_RIVER_FRACTAL = Artist.custom((fractal, g) -> fractal.forEach(f -> draw(f, g, 1)));

    public static Color plateColor(Plate plate)
    {
        final Random random = new Random(plate.hashCode());
        if (plate.oceanic())
        {
            return new Color(0, random.nextInt(255), 255);
        }
        else
        {
            return new Color(0, random.nextInt(155) + 100, 0);
        }
    }

    public static void draw(RiverFractal fractal, Graphics2D g, float s)
    {
        for (RiverFractal.Edge edge : fractal.getEdges())
        {
            g.drawLine((int) (s * edge.drain().x()), (int) (s * edge.drain().y()), (int) (s * edge.source().x()), (int) (s * edge.source().y()));
        }
    }

    public static void draw(MidpointFractal fractal, Graphics2D g, float s)
    {
        for (int i = 0; i < (fractal.segments.length >> 1) - 1; i++)
        {
            g.drawLine((int) (s * fractal.segments[(i << 1)]), (int) (s * fractal.segments[(i << 1) + 1]), (int) (s * fractal.segments[(i << 1) + 2]), (int) (s * fractal.segments[(i << 1) + 3]));
        }
    }

    public static <T> void islandBackground(T t, Graphics2D g)
    {
        g.setColor(new Color(0, 0, 200));
        g.fillRect(0, 0, 1000, 1000);
        g.setColor(new Color(80, 150, 80));
        g.fillOval(0, 0, 1000, 1000);
        g.setColor(new Color(150, 200, 255));
        g.setStroke(new BasicStroke(3));
    }

    public static <T> void background(T t, Graphics2D g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 1000, 1000);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(3));
    }

    @Test
    public void testBisectingRiverFractal()
    {
        long seed = seed();
        for (int i = 0; i < 10; i++)
        {
            RandomSource random = new XoroshiroRandomSource(seed);
            MidpointFractal fractal = new MidpointFractal(random, i, 0, 500, 1000, 500);
            MIDPOINT_FRACTAL.before(RiverVisualizations::background).draw("midpoint_fractal" + i, fractal);
        }
    }

    @Test
    public void testGrownRiverFractal()
    {
        long seed = seed();
        RandomSource random = new XoroshiroRandomSource(seed);
        RiverFractal fractal = RiverFractal.build(random, 250, 500, 0, 60, 15, 10);
        RIVER_FRACTAL.before(RiverVisualizations::background).draw("grown_fractal", fractal);
    }

    @Test
    public void testGrownAndBisectedRiverFractal()
    {
        final long seed = seed();
        final RandomSource random = new XoroshiroRandomSource(seed);
        final RiverFractal fractal = RiverFractal.build(random, 250, 500, 0, 80, 15, 10);
        final List<MidpointFractal> fractals = fractal.getEdges().stream().map(e -> e.fractal(random, 4)).collect(Collectors.toList());
        RIVER_FRACTAL.before(RiverVisualizations::background).draw("grown_fractal", fractal);
        MULTI_MIDPOINT_FRACTAL.before(RiverVisualizations::background).draw("grown_fractal_midpoint", fractals);
    }

    @Test
    public void testMultiGrownRiverFractals()
    {
        final long seed = seed();
        final RandomSource random = new XoroshiroRandomSource(seed);

        // Rivers grown from both horizontal edges, pointing inwards.
        final RiverFractal.MultiParallelBuilder context = new RiverFractal.MultiParallelBuilder();
        for (int i = 0; i < 6; i++)
        {
            context.add(new RiverFractal.Builder(random, (i % 2 == 0) ? 50 : 950, i * 100 + 150, (i % 2 == 0) ? 0 : (float) Math.PI, 50, 15, 10));
        }

        final List<RiverFractal> fractals = context.build();
        final List<MidpointFractal> midpoints = fractals.stream().flatMap(fractal -> fractal.getEdges().stream().map(e -> e.fractal(random, 4))).collect(Collectors.toList());
        MULTI_RIVER_FRACTAL.before(RiverVisualizations::background).draw("multi_grown_fractal", fractals);
        MULTI_MIDPOINT_FRACTAL.before(RiverVisualizations::background).draw("multi_grown_fractal_midpoint", midpoints);
    }

    @Test
    public void testMultiGrownRiverFractalsIsland()
    {
        final long seed = seed();
        final RandomSource random = new XoroshiroRandomSource(seed);

        final RiverFractal.MultiParallelBuilder context = new RiverFractal.MultiParallelBuilder()
        {
            @Override
            protected boolean isLegal(RiverFractal.Vertex vertex)
            {
                return RiverHelpers.norm2(vertex.x() - 500, vertex.y() - 500) < 500 * 500;
            }
        };

        for (int i = 0; i < 15; i++)
        {
            float angle = random.nextFloat() * 2 * Mth.PI;
            context.add(new RiverFractal.Builder(random, 500 + Mth.cos(angle) * 500, 500 + Mth.sin(angle) * 500, angle - Mth.PI, 50, 20, 10));
        }

        final List<RiverFractal> fractals = context.build();
        final List<MidpointFractal> midpoints = fractals.stream().flatMap(fractal -> fractal.getEdges().stream().map(e -> e.fractal(random, 4))).collect(Collectors.toList());

        MULTI_RIVER_FRACTAL.before(RiverVisualizations::islandBackground).draw("multi_grown_fractal_island", fractals);
        MULTI_MIDPOINT_FRACTAL.before(RiverVisualizations::islandBackground).draw("multi_grown_fractal_island_midpoint", midpoints);
    }

    @Test
    public void testWatershedsWithRivers()
    {
        final long seed = seed();
        final TypedAreaFactory<Plate> plates = TFCLayers.createEarlyPlateLayers(seed);
        final Watershed.Context context = new Watershed.Context(plates, seed, 0.8f, 1.1f, 16, 0.2f);
        final Set<Watershed> sheds = new HashSet<>();

        IntStream.rangeClosed(0, 40).forEach(x -> IntStream.rangeClosed(0, 40).forEach(z -> sheds.add(context.create(x, z))));

        final List<RiverFractal> rivers = sheds.stream()
            .flatMap(s -> s.getRivers().stream())
            .toList();

        final float s = 1000 / 40f;
        TFCLayersVisualizations.PLATES.color(RiverVisualizations::plateColor).dimensions(40);
        Artist.custom((t, g) -> {
            TFCLayersVisualizations.PLATES.draw(plates, g);

            g.setColor(Color.RED);
            for (Watershed shed : sheds)
            {
                if (shed instanceof Watershed.Rivers rv)
                {
                    rv.getSources().forEach((long key) -> {
                        final float x0 = (RiverHelpers.unpackX(key) + 0.5f) * s, z0 = (RiverHelpers.unpackZ(key) + 0.5f) * s;
                        g.drawOval((int) x0 - 3, (int) z0 - 3, 6, 6);
                    });
                }
            }

            g.setColor(Color.ORANGE);
            g.setStroke(new BasicStroke(2));
            rivers.forEach(fractal -> draw(fractal, g, s));
        }).draw("rivers");

        final RandomSource random = new XoroshiroRandomSource(seed);
        final List<MidpointFractal> fractals = rivers.stream()
            .flatMap(r -> r.getEdges().stream().map(e -> e.fractal(random, 4)))
            .toList();

        Artist.custom((t, g) -> {
            TFCLayersVisualizations.PLATES.draw(plates, g);

            g.setColor(Color.CYAN);
            g.setStroke(new BasicStroke(2));
            fractals.forEach(fractal -> draw(fractal, g, s));
        }).draw("rivers_midpoint");
    }

    @Test
    public void testRiverComparison()
    {
        long seed = seed();
        TFCLayersVisualizations.AREA.dimensions(5000).color(i -> {
            if (TFCLayers.isRiver(i)) return new Color(100, 200, 255);
            if (TFCLayers.isLake(i)) return new Color(50, 200, 255);
            if (TFCLayers.isOcean(i)) return new Color(50, 150, 255);
            return new Color(70, 160, 100);
        });

        final TypedAreaFactory<Plate> plates = TFCLayers.createEarlyPlateLayers(seed);
        final Watershed.Context context = new Watershed.Context(plates, seed, 0.8f, 1.1f, 16, 0.2f);
        final AreaFactory riverArea = TFCLayers.createOverworldBiomeLayerWithRivers(seed, context, IArtist.nope(), IArtist.nope());

        TFCLayersVisualizations.AREA.draw("rivers_on_biomes", riverArea);
    }
}
