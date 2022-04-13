/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredients;
import net.dries007.tfc.common.recipes.ingredients.TFCIngredients;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifiers;
import net.dries007.tfc.world.layer.Plate;
import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.layer.framework.AreaFactory;
import net.dries007.tfc.world.layer.framework.TypedArea;
import net.dries007.tfc.world.layer.framework.TypedAreaFactory;
import net.dries007.tfc.world.river.MidpointFractal;
import net.dries007.tfc.world.river.RiverFractal;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Ensure that we are bootstrapped before each test runs, to prevent errors from uncertain loading order.
 */
public class TestHelper
{
    public static final Artist.Custom<MidpointFractal> MIDPOINT_FRACTAL = Artist.custom((fractal, g) -> draw(fractal, g, 1));
    public static final Artist.Custom<List<MidpointFractal>> MULTI_MIDPOINT_FRACTAL = Artist.custom((fractal, g) -> fractal.forEach(f -> draw(f, g, 1)));
    public static final Artist.Custom<RiverFractal> RIVER_FRACTAL = Artist.custom((fractal, g) -> draw(fractal, g, 1));
    public static final Artist.Custom<List<RiverFractal>> MULTI_RIVER_FRACTAL = Artist.custom((fractal, g) -> fractal.forEach(f -> draw(f, g, 1)));
    public static final Artist.Typed<TypedAreaFactory<Plate>, Plate> PLATES = Artist.forMap(factory -> {
        final TypedArea<Plate> area = factory.get();
        return Artist.Pixel.coerceInt(area::get);
    });
    public static final Artist.Typed<AreaFactory, Integer> AREA = Artist.forMap(factory -> {
        final Area area = factory.get();
        return Artist.Pixel.coerceInt(area::get);
    });
    public static final Artist.Raw RAW = Artist.raw();
    public static final Random SEEDS = new Random();

    private static final AtomicBoolean BOOTSTRAP = new AtomicBoolean(false);

    @BeforeAll
    public static void setup()
    {
        bootstrap();
    }

    public static long seed()
    {
        long seed = SEEDS.nextLong();
        System.out.println("Seed " + seed);
        return seed;
    }

    public static void bootstrap()
    {
        if (!BOOTSTRAP.get())
        {
            BOOTSTRAP.set(true);

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

            // Various TFC bootstraps that we can do
            ItemStackModifiers.registerItemStackModifierTypes();
            BlockIngredients.registerBlockIngredientTypes();
            TFCIngredients.registerIngredientTypes();
        }
    }

    public static <T> void background(T t, Graphics2D g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 1000, 1000);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(3));
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

    public static void draw(MidpointFractal fractal, Graphics2D g, float s)
    {
        for (int i = 0; i < (fractal.segments.length >> 1) - 1; i++)
        {
            g.drawLine((int) (s * fractal.segments[(i << 1)]), (int) (s * fractal.segments[(i << 1) + 1]), (int) (s * fractal.segments[(i << 1) + 2]), (int) (s * fractal.segments[(i << 1) + 3]));
        }
    }

    public static void draw(RiverFractal fractal, Graphics2D g, float s)
    {
        for (RiverFractal.Edge edge : fractal.getEdges())
        {
            g.drawLine((int) (s * edge.drain().x()), (int) (s * edge.drain().y()), (int) (s * edge.source().x()), (int) (s * edge.source().y()));
        }
    }

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

    public static void assertIngredientEquals(Ingredient expected, Ingredient actual)
    {
        assertEquals(expected.getClass(), actual.getClass());
        assertEquals(expected.getSerializer(), actual.getSerializer());
        assertEquals(expected.toJson(), actual.toJson());

        final ItemStack[] expectedItems = expected.getItems(), actualItems = actual.getItems();
        assertEquals(expectedItems.length, actualItems.length);
        for (int i = 0; i < Math.min(expectedItems.length, actualItems.length); i++)
        {
            assertItemStackEquals(expectedItems[i], actualItems[i]);
        }
    }

    public static void assertItemStackEquals(ItemStack expected, ItemStack actual)
    {
        assertEquals(expected.getItem(), actual.getItem());
        assertEquals(expected.getCount(), actual.getCount());
        assertEquals(expected.getTag(), actual.getTag());
        assertEquals(expected.toString(), actual.toString());
    }
}
