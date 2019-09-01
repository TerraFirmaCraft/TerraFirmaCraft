/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package test;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.LongFunction;
import java.util.function.Supplier;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.VoroniZoomLayer;
import net.minecraft.world.gen.layer.ZoomLayer;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

import imageutil.Images;
import net.dries007.tfc.world.gen.layer.*;

import static net.dries007.tfc.world.gen.layer.TFCLayerUtil.*;

/**
 * Test class for overworld biome layer generation
 */
public class OverworldLayerTests
{
    public static final Images<IAreaFactory<LazyArea>> IMAGES = Images.get(af -> {
        IArea area = af.make();
        return (x, y) -> area.getValue((int) x, (int) y);
    });
    public static boolean isTestMode = false;

    static
    {
        IMAGES.size(1000).color(Images.Colors.LINEAR_GRAY).disable();
    }

    public static void main(String[] args)
    {
        isTestMode = true;
        IMAGES.enable();

        boolean findSpawnBiomes = false;
        boolean drawHugeArea = false;

        List<IAreaFactory<LazyArea>> layers = testOverworldLayers(System.currentTimeMillis());

        if (findSpawnBiomes)
        {
            LazyArea actual = layers.get(1).make();
            System.out.println("Biome at (0, 0): " + biomeName(actual.getValue(0, 0)));
            System.out.println("Biome at (-256, 0): " + biomeName(actual.getValue(-256, 0)));
            System.out.println("Biome at (256, 0): " + biomeName(actual.getValue(256, 0)));
            System.out.println("Biome at (0, -256): " + biomeName(actual.getValue(0, -256)));
            System.out.println("Biome at (0, 256): " + biomeName(actual.getValue(0, 256)));
        }

        if (drawHugeArea)
        {
            IMAGES.draw("biomes_actual_10km", layers.get(0), 0, 0, -5000, -5000, 5000, 5000);
        }
    }

    public static Color landColor(double val, double min, double max)
    {
        int i = (int) Math.round(val);
        if (i == PLAINS)
        {
            return Color.GREEN;
        }
        else if (i == DEEP_OCEAN)
        {
            return Color.BLUE;
        }
        return Color.BLACK;
    }

    public static Color elevationColor(double val, double min, double max)
    {
        int i = (int) Math.round(val);
        if (i == PLAINS)
        {
            return new Color(100, 200, 100);
        }
        else if (i == HILLS)
        {
            return new Color(255, 200, 0);
        }
        else if (i == MOUNTAINS)
        {
            return new Color(255, 100, 0);
        }
        else if (i == DEEP_OCEAN)
        {
            return Color.BLUE;
        }
        return Color.BLACK;
    }

    private static List<IAreaFactory<LazyArea>> testOverworldLayers(long seed)
    {
        Random seedGenerator = new Random(seed);
        LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed, seedModifier);
        Supplier<LazyAreaLayerContext> contextSupplier = () -> contextFactory.apply(seedGenerator.nextLong());

        IAreaFactory<LazyArea> areaFactory;
        int layerCount = 0;

        IMAGES.color(OverworldLayerTests::landColor);

        // Section 1: Creation of Land / Ocean Distribution
        areaFactory = new IslandLayer(10).apply(contextSupplier.get());
        IMAGES.draw("layer_land_" + ++layerCount, areaFactory, 0, 0, -10, -10, 10, 10);

        areaFactory = ZoomLayer.FUZZY.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_land_" + ++layerCount, areaFactory, 0, 0, -20, -20, 20, 20);

        areaFactory = AddIslandLayer.NORMAL.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_land_" + ++layerCount, areaFactory, 0, 0, -20, -20, 20, 20);

        areaFactory = ZoomLayer.NORMAL.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_land_" + ++layerCount, areaFactory, 0, 0, -40, -40, 40, 40);

        areaFactory = AddIslandLayer.NORMAL.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_land_" + ++layerCount, areaFactory, 0, 0, -40, -40, 40, 40);

        areaFactory = repeat(AddIslandLayer.HEAVY, 1, areaFactory, contextSupplier);
        IMAGES.draw("layer_land_" + ++layerCount, areaFactory, 0, 0, -40, -40, 40, 40);

        areaFactory = SmoothLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_land_" + ++layerCount, areaFactory, 0, 0, -40, -40, 40, 40);

        areaFactory = repeat(AddIslandLayer.HEAVY, 1, areaFactory, contextSupplier);
        IMAGES.draw("layer_land_" + ++layerCount, areaFactory, 0, 0, -40, -40, 40, 40);

        areaFactory = SmoothLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_land_" + ++layerCount, areaFactory, 0, 0, -40, -40, 40, 40);

        // Section 2: Elevation

        Images.get().color(OverworldLayerTests::elevationColor);
        layerCount = 0;

        areaFactory = ElevationLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_elevation_" + ++layerCount, areaFactory, 0, 0, -40, -40, 40, 40);

        areaFactory = ZoomLayer.NORMAL.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_elevation_" + ++layerCount, areaFactory, 0, 0, -80, -80, 80, 80);

        // Section 3: Biome Supertypes

        IMAGES.color(TFCLayerUtil::biomeColor);
        layerCount = 0;

        areaFactory = BiomeLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_biome_" + ++layerCount, areaFactory, 0, 0, -80, -80, 80, 80);

        areaFactory = ZoomLayer.NORMAL.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_biome_" + ++layerCount, areaFactory, 0, 0, -160, -160, 160, 160);

        areaFactory = AddIslandLayer.NORMAL.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_biome_" + ++layerCount, areaFactory, 0, 0, -160, -160, 160, 160);

        areaFactory = ZoomLayer.NORMAL.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_biome_" + ++layerCount, areaFactory, 0, 0, -320, -320, 320, 320);

        areaFactory = RemoveOceanLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_biome_" + ++layerCount, areaFactory, 0, 0, -320, -320, 320, 320);

        areaFactory = AddIslandLayer.HEAVY.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_biome_" + ++layerCount, areaFactory, 0, 0, -320, -320, 320, 320);

        areaFactory = SmoothLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_biome_" + ++layerCount, areaFactory, 0, 0, -320, -320, 320, 320);

        areaFactory = AddIslandLayer.NORMAL.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_biome_" + ++layerCount, areaFactory, 0, 0, -320, -320, 320, 320);

        areaFactory = OceanLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_biome_" + ++layerCount, areaFactory, 0, 0, -320, -320, 320, 320);

        areaFactory = EdgeBiomeLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_biome_" + ++layerCount, areaFactory, 0, 0, -320, -320, 320, 320);

        areaFactory = repeat(ZoomLayer.NORMAL, 4, areaFactory, contextFactory, 1000L);
        IMAGES.draw("layer_biome_" + ++layerCount, areaFactory, 0, 0, -320, -320, 320, 320);

        areaFactory = ShoreLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_biome_" + ++layerCount, areaFactory, 0, 0, -160, -160, 320, 320);

        areaFactory = repeat(ZoomLayer.NORMAL, 2, areaFactory, contextFactory, 1004L);
        IMAGES.draw("layer_biome_" + ++layerCount, areaFactory, 0, 0, -320, -320, 320, 320);

        areaFactory = SmoothLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_biome_" + ++layerCount, areaFactory, 0, 0, -320, -320, 320, 320);

        IAreaFactory<LazyArea> areaFactoryActual = VoroniZoomLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_biome_" + ++layerCount, areaFactoryActual, 0, 0, -640, -640, 640, 640);

        return Arrays.asList(areaFactory, areaFactoryActual);
    }

    private static IAreaFactory<LazyArea> testRockLayers()
    {
        return null;
    }

    private static <A extends IArea, C extends IExtendedNoiseRandom<A>> IAreaFactory<A> repeat(IAreaTransformer1 transformer, int count, IAreaFactory<A> originalLayer, Supplier<C> contextSupplier)
    {
        IAreaFactory<A> newFactory = originalLayer;
        for (int i = 0; i < count; ++i)
        {
            newFactory = transformer.apply(contextSupplier.get(), newFactory);
        }
        return newFactory;
    }

    private static <A extends IArea, C extends IExtendedNoiseRandom<A>> IAreaFactory<A> repeat(IAreaTransformer1 transformer, int count, IAreaFactory<A> originalLayer, LongFunction<C> contextFactory, long seed)
    {
        IAreaFactory<A> newFactory = originalLayer;
        for (int i = 0; i < count; ++i)
        {
            newFactory = transformer.apply(contextFactory.apply(seed + i), newFactory);
        }
        return newFactory;
    }

    private static String biomeName(int biome)
    {
        if (biome == OCEAN) return "OCEAN";
        if (biome == DEEP_OCEAN) return "DEEP_OCEAN";
        if (biome == DEEP_OCEAN_RIDGE) return "DEEP_OCEAN_RIDGE";

        if (biome == PLAINS) return "PLAINS";
        if (biome == HILLS) return "HILLS";
        if (biome == LOWLANDS) return "LOWLANDS";
        if (biome == LOW_CANYONS) return "LOW_CANYONS";

        if (biome == ROLLING_HILLS) return "ROLLING_HILLS";
        if (biome == BADLANDS) return "BADLANDS";
        if (biome == PLATEAU) return "PLATEAU";
        if (biome == OLD_MOUNTAINS) return "OLD_MOUNTAINS";

        if (biome == MOUNTAINS) return "MOUNTAINS";
        if (biome == FLOODED_MOUNTAINS) return "FLOODED_MOUNTAINS";
        if (biome == CANYONS) return "CANYONS";

        if (biome == SHORE) return "SHORE";
        if (biome == STONE_SHORE) return "STONE_SHORE";

        if (biome == MOUNTAINS_EDGE) return "MOUNTAINS_EDGE";
        if (biome == LAKE) return "LAKE";
        return "UNKNOWN";
    }

}
