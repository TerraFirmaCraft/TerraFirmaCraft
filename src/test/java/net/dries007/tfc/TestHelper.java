package net.dries007.tfc;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;

import net.dries007.tfc.world.layer.Plate;
import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.layer.framework.AreaFactory;
import net.dries007.tfc.world.layer.framework.TypedArea;
import net.dries007.tfc.world.layer.framework.TypedAreaFactory;
import net.dries007.tfc.world.river.MidpointFractal;
import net.dries007.tfc.world.river.RiverFractal;

import static org.junit.jupiter.api.Assertions.fail;

public interface TestHelper
{
    Artist.Custom<MidpointFractal> MIDPOINT_FRACTAL = Artist.custom((fractal, g) -> draw(fractal, g, 1));
    Artist.Custom<List<MidpointFractal>> MULTI_MIDPOINT_FRACTAL = Artist.custom((fractal, g) -> fractal.forEach(f -> draw(f, g, 1)));

    Artist.Custom<RiverFractal> RIVER_FRACTAL = Artist.custom((fractal, g) -> draw(fractal, g, 1));
    Artist.Custom<List<RiverFractal>> MULTI_RIVER_FRACTAL = Artist.custom((fractal, g) -> fractal.forEach(f -> draw(f, g, 1)));

    Artist.Typed<TypedAreaFactory<Plate>, Plate> PLATES = Artist.forMap(factory -> {
        final TypedArea<Plate> area = factory.get();
        return Artist.Pixel.coerceInt(area::get);
    });

    Artist.Typed<AreaFactory, Integer> AREA = Artist.forMap(factory -> {
        final Area area = factory.get();
        return Artist.Pixel.coerceInt(area::get);
    });

    Artist.Raw RAW = Artist.raw();

    Random SEEDS = new Random();

    static long seed()
    {
        long seed = SEEDS.nextLong();
        System.out.println("Seed " + seed);
        return seed;
    }

    static void boostrap()
    {
        try
        {
            Field field = SharedConstants.class.getDeclaredField("CURRENT_VERSION");
            field.setAccessible(true);
            field.set(null, DetectedVersion.BUILT_IN);
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            fail("Unable to set SharedConstants#CURRENT_VERSION", e);
        }

        Bootstrap.bootStrap();
    }

    static <T> void background(T t, Graphics2D g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 1000, 1000);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(3));
    }

    static <T> void islandBackground(T t, Graphics2D g)
    {
        g.setColor(new Color(0, 0, 200));
        g.fillRect(0, 0, 1000, 1000);
        g.setColor(new Color(80, 150, 80));
        g.fillOval(0, 0, 1000, 1000);
        g.setColor(new Color(150, 200, 255));
        g.setStroke(new BasicStroke(3));
    }

    static void draw(MidpointFractal fractal, Graphics2D g, float s)
    {
        for (int i = 0; i < (fractal.segments.length >> 1) - 1; i++)
        {
            g.drawLine((int) (s * fractal.segments[(i << 1)]), (int) (s * fractal.segments[(i << 1) + 1]), (int) (s * fractal.segments[(i << 1) + 2]), (int) (s * fractal.segments[(i << 1) + 3]));
        }
    }

    static void draw(RiverFractal fractal, Graphics2D g, float s)
    {
        for (RiverFractal.Edge edge : fractal.getEdges())
        {
            g.drawLine((int) (s * edge.drain().x()), (int) (s * edge.drain().y()), (int) (s * edge.source().x()), (int) (s * edge.source().y()));
        }
    }

    static Color plateColor(Plate plate)
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
}
