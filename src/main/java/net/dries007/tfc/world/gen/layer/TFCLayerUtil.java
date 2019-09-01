/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.layer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.LongFunction;
import java.util.function.Supplier;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.VoroniZoomLayer;
import net.minecraft.world.gen.layer.ZoomLayer;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import imageutil.Images;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.biome.provider.TFCBiomeProviderSettings;
import net.dries007.tfc.world.gen.TFCGenerationSettings;
import net.dries007.tfc.world.gen.rock.RockCategory;
import test.OverworldLayerTests;

import static net.dries007.tfc.api.registries.TFCRegistries.ROCKS;
import static test.OverworldLayerTests.IMAGES;

public class TFCLayerUtil
{
    private static int id = 0;
    /* Biomes */
    public static final int OCEAN = getId(TFCBiomes.OCEAN);
    public static final int DEEP_OCEAN = getId(TFCBiomes.DEEP_OCEAN);
    public static final int DEEP_OCEAN_RIDGE = getId(TFCBiomes.DEEP_OCEAN_RIDGE);
    public static final int PLAINS = getId(TFCBiomes.PLAINS);
    public static final int HILLS = getId(TFCBiomes.HILLS);
    public static final int LOWLANDS = getId(TFCBiomes.LOWLANDS);
    public static final int LOW_CANYONS = getId(TFCBiomes.LOW_CANYONS);
    public static final int ROLLING_HILLS = getId(TFCBiomes.ROLLING_HILLS);
    public static final int BADLANDS = getId(TFCBiomes.BADLANDS);
    public static final int PLATEAU = getId(TFCBiomes.PLATEAU);
    public static final int OLD_MOUNTAINS = getId(TFCBiomes.OLD_MOUNTAINS);
    public static final int MOUNTAINS = getId(TFCBiomes.MOUNTAINS);
    public static final int FLOODED_MOUNTAINS = getId(TFCBiomes.FLOODED_MOUNTAINS);
    public static final int CANYONS = getId(TFCBiomes.CANYONS);
    public static final int SHORE = getId(TFCBiomes.SHORE);
    public static final int STONE_SHORE = getId(TFCBiomes.STONE_SHORE);
    public static final int MOUNTAINS_EDGE = getId(TFCBiomes.MOUNTAINS_EDGE);
    public static final int LAKE = getId(TFCBiomes.LAKE);

    public static List<IAreaFactory<LazyArea>> createOverworldBiomeLayer(long seed, TFCBiomeProviderSettings settings)
    {
        Random seedGenerator = new Random(seed);
        LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed, seedModifier);
        Supplier<LazyAreaLayerContext> contextSupplier = () -> contextFactory.apply(seedGenerator.nextLong());

        IAreaFactory<LazyArea> areaFactory;
        int layerCount = 0;

        IMAGES.color(OverworldLayerTests::landColor).disable();

        // Section 1: Creation of Land / Ocean Distribution
        areaFactory = new IslandLayer(settings.getIslandFrequency()).apply(contextSupplier.get());
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

        areaFactory = repeat(ZoomLayer.NORMAL, settings.getBiomeZoomLevel(), areaFactory, contextFactory, 1000L);
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

    public static List<IAreaFactory<LazyArea>> createOverworldRockLayers(TFCGenerationSettings settings, long seed)
    {
        Random seedGenerator = new Random(seed);
        LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed, seedModifier);
        Supplier<LazyAreaLayerContext> contextSupplier = () -> contextFactory.apply(seedGenerator.nextLong());

        IAreaFactory<LazyArea> areaFactory;
        int layerCount = 0;

        IMAGES.color(Images.Colors.DISCRETE_20).disable();

        List<IAreaFactory<LazyArea>> finishedLayers = new ArrayList<>(3);

        for (RockCategory.Layer level : RockCategory.Layer.values())
        {
            areaFactory = new RockLayer(level).apply(contextSupplier.get());
            IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.getValues().size(), -30, -30, 30, 30);

            areaFactory = new RockMixLayer(level).apply(contextSupplier.get(), areaFactory);
            IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.getValues().size(), -30, -30, 30, 30);

            areaFactory = ZoomLayer.NORMAL.apply(contextSupplier.get(), areaFactory);
            IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.getValues().size(), -30, -30, 30, 30);

            areaFactory = ZoomLayer.NORMAL.apply(contextSupplier.get(), areaFactory);
            IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.getValues().size(), -30, -30, 30, 30);

            areaFactory = ZoomLayer.NORMAL.apply(contextSupplier.get(), areaFactory);
            IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.getValues().size(), -300, -300, 300, 300);

            areaFactory = ZoomLayer.NORMAL.apply(contextSupplier.get(), areaFactory);
            IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.getValues().size(), -300, -300, 300, 300);

            areaFactory = SmoothLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
            IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.getValues().size(), -300, -300, 300, 300);

            areaFactory = SmoothLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
            IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.getValues().size(), -300, -300, 300, 300);

            areaFactory = TFCLayerUtil.repeat(ZoomLayer.NORMAL, settings.getRockZoomLevel(), areaFactory, contextSupplier);
            IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.getValues().size(), -3000, -3000, 3000, 3000);

            areaFactory = VoroniZoomLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
            IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.getValues().size(), -3000, -3000, 3000, 3000);

            finishedLayers.add(areaFactory);
        }
        return finishedLayers;
    }

    public static <A extends IArea, C extends IExtendedNoiseRandom<A>> IAreaFactory<A> repeat(IAreaTransformer1 transformer, int count, IAreaFactory<A> originalLayer, Supplier<C> contextSupplier)
    {
        IAreaFactory<A> newFactory = originalLayer;
        for (int i = 0; i < count; ++i)
        {
            newFactory = transformer.apply(contextSupplier.get(), newFactory);
        }
        return newFactory;
    }

    public static <A extends IArea, C extends IExtendedNoiseRandom<A>> IAreaFactory<A> repeat(IAreaTransformer1 transformer, int count, IAreaFactory<A> originalLayer, LongFunction<C> contextFactory, long seed)
    {
        IAreaFactory<A> newFactory = originalLayer;
        for (int i = 0; i < count; ++i)
        {
            newFactory = transformer.apply(contextFactory.apply(seed + i), newFactory);
        }
        return newFactory;
    }

    public static Color biomeColor(double val, double min, double max)
    {
        int biome = (int) Math.round(val);
        if (biome == OCEAN) return new Color(0, 0, 255);
        if (biome == DEEP_OCEAN) return new Color(0, 0, 180);
        if (biome == DEEP_OCEAN_RIDGE) return new Color(0, 0, 120);

        if (biome == PLAINS) return new Color(0, 230, 120);
        if (biome == HILLS) return new Color(0, 180, 20);
        if (biome == LOWLANDS) return new Color(160, 200, 120);
        if (biome == LOW_CANYONS) return new Color(200, 100, 0);

        if (biome == ROLLING_HILLS) return new Color(0, 160, 0);
        if (biome == BADLANDS) return new Color(255, 160, 0);
        if (biome == PLATEAU) return new Color(240, 150, 100);
        if (biome == OLD_MOUNTAINS) return new Color(140, 170, 140);

        if (biome == MOUNTAINS) return new Color(140, 140, 140);
        if (biome == FLOODED_MOUNTAINS) return new Color(110, 110, 110);
        if (biome == CANYONS) return new Color(160, 60, 0);

        if (biome == SHORE) return new Color(230, 210, 100);
        if (biome == STONE_SHORE) return new Color(210, 190, 80);

        if (biome == MOUNTAINS_EDGE) return new Color(180, 180, 180);
        if (biome == LAKE) return new Color(0, 100, 255);
        return Color.BLACK;
    }

    static boolean isShoreCompatible(int value)
    {
        return value != LOWLANDS && value != LOW_CANYONS && value != CANYONS;
    }

    static boolean isLakeCompatible(int value)
    {
        return isLow(value) || value == CANYONS || value == ROLLING_HILLS;
    }

    static boolean isOcean(int value)
    {
        return value == OCEAN || value == DEEP_OCEAN || value == DEEP_OCEAN_RIDGE;
    }

    static boolean isMountains(int value)
    {
        return value == MOUNTAINS || value == FLOODED_MOUNTAINS || value == MOUNTAINS_EDGE || value == OLD_MOUNTAINS;
    }

    static boolean isLow(int value)
    {
        return value == PLAINS || value == HILLS || value == LOW_CANYONS || value == LOWLANDS;
    }

    private static int getId(Biome biome)
    {
        if (OverworldLayerTests.isTestMode) return ++id; // For switching between testing mode and minecraft mode
        return ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getID(biome);
    }
}
