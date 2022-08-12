/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import io.netty.buffer.Unpooled;
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
        TestAssertions.assertEquals(expected, actual);
    }

    public static void assertItemStackEquals(ItemStack expected, ItemStack actual)
    {
        TestAssertions.assertEquals(expected, actual);
    }

    public static void assertRecipeEquals(Recipe<?> expected, Recipe<?> actual)
    {
        TestAssertions.assertEquals(expected, actual);
    }

    public static <R extends Recipe<?>, S extends RecipeSerializer<R>> R encodeAndDecode(R recipe, S serializer)
    {
        return encodeAndDecode(recipe, (r, buf) -> serializer.toNetwork(buf, r), buf -> serializer.fromNetwork(recipe.getId(), buf));
    }

    public static <T> T encodeAndDecode(T t, BiConsumer<T, FriendlyByteBuf> encode, Function<FriendlyByteBuf, T> decode)
    {
        final FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        encode.accept(t, buffer);
        final T result = decode.apply(buffer);
        assertEquals(0, buffer.readableBytes(), "Buffer has " + buffer.readableBytes() + " remaining bytes");
        return result;
    }

    public static <T> T writeAndRead(T before, BiConsumer<T, CompoundTag> encoder, T after, BiConsumer<T, CompoundTag> decoder)
    {
        return writeAndRead(before, t -> {
            final CompoundTag tag = new CompoundTag();
            encoder.accept(t, tag);
            return tag;
        }, tag -> {
            decoder.accept(after, tag);
            return after;
        });
    }

    public static <T> T writeAndRead(T before, Function<T, CompoundTag> encoder, Function<CompoundTag, T> decoder)
    {
        return encoder.andThen(decoder).apply(before);
    }
}
