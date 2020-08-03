package net.dries007.tfc.tests;

import java.awt.*;
import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.ZoomLayer;

import net.dries007.tfc.ImageUtil;
import net.dries007.tfc.world.biome.IBiomeFactory;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.layer.*;
import org.junit.jupiter.api.Test;

public class TFCLayerUtilTests
{
    public static final ImageUtil<IAreaFactory<LazyArea>> IMAGES = ImageUtil.noise(lazyAreaIAreaFactory -> {
        LazyArea area = lazyAreaIAreaFactory.make();
        return (left, right) -> area.getValue((int) left, (int) right);
    }, b -> b.scale(ImageUtil.Scales.NEAREST_INT).size(1000));

    @Test
    void testBiomeLayers()
    {
        long seed = 1234;
        Random random = new Random(seed);
        Supplier<LazyAreaLayerContext> contextFactory = () -> new LazyAreaLayerContext(25, seed, random.nextLong());
        IAreaFactory<LazyArea> mainLayer, riverLayer;
        int layer = 0;

        // Ocean / Continents

        IMAGES.color(this::landColor).dimensions(200);

        mainLayer = new IslandLayer(6).apply(contextFactory.get());
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = ZoomLayer.FUZZY.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = ZoomLayer.NORMAL.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        for (int i = 0; i < 2; i++)
        {
            mainLayer = AddIslandLayer.HEAVY.apply(contextFactory.get(), mainLayer);
            IMAGES.draw("layer_" + ++layer, mainLayer);

            mainLayer = SmoothLayer.INSTANCE.apply(contextFactory.get(), mainLayer);
            IMAGES.draw("layer_" + ++layer, mainLayer);
        }

        // Oceans and Continents => Elevation Mapping
        IMAGES.color(this::elevationColor).dimensions(400);

        mainLayer = ElevationLayer.INSTANCE.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = ZoomLayer.NORMAL.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        // Elevation Mapping => Rivers
        riverLayer = ZoomLayer.NORMAL.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        for (int i = 0; i < 6; i++)
        {
            riverLayer = ZoomLayer.NORMAL.apply(contextFactory.get(), riverLayer);
            IMAGES.draw("layer_" + ++layer, mainLayer);
        }

        riverLayer = RiverLayer.INSTANCE.apply(contextFactory.get(), riverLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        riverLayer = ZoomLayer.NORMAL.apply(contextFactory.get(), riverLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        IMAGES.color(this::biomeColor).dimensions(1000);

        // Elevation Mapping => Biomes
        mainLayer = BiomeLayer.INSTANCE.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = ZoomLayer.NORMAL.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = ZoomLayer.NORMAL.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = RemoveOceanLayer.INSTANCE.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = OceanLayer.INSTANCE.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = EdgeBiomeLayer.INSTANCE.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = AddLakeLayer.INSTANCE.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        for (int i = 0; i < 2; i++)
        {
            mainLayer = ZoomLayer.NORMAL.apply(contextFactory.get(), mainLayer);
            IMAGES.draw("layer_" + ++layer, mainLayer);
        }

        mainLayer = ShoreLayer.CASTLE.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        for (int i = 0; i < 4; i++)
        {
            mainLayer = ZoomLayer.NORMAL.apply(contextFactory.get(), mainLayer);
            IMAGES.draw("layer_" + ++layer, mainLayer);
        }

        IMAGES.dimensions(10000);

        mainLayer = SmoothLayer.INSTANCE.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = MixRiverLayer.INSTANCE.apply(contextFactory.get(), mainLayer, riverLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = BiomeRiverWidenLayer.MEDIUM.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = BiomeRiverWidenLayer.LOW.apply(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);
    }

    private Color landColor(double value)
    {
        Biome biome = IBiomeFactory.getBiome((int) value);
        return biome == TFCBiomes.DEEP_OCEAN.get() ? Color.BLUE : Color.GREEN;
    }

    private Color elevationColor(double value)
    {
        int id = (int) value;
        if (id == TFCLayerUtil.DEEP_OCEAN) return Color.BLUE;
        if (id == TFCLayerUtil.OCEAN) return Color.CYAN;
        if (id == TFCLayerUtil.PLAINS) return Color.GREEN;
        if (id == TFCLayerUtil.HILLS) return Color.YELLOW;
        if (id == TFCLayerUtil.MOUNTAINS) return Color.RED;
        if (id == TFCLayerUtil.RIVER) return Color.PINK;
        return Color.BLACK;
    }

    private Color biomeColor(double value)
    {
        int id = (int) value;
        if (id == TFCLayerUtil.DEEP_OCEAN) return new Color(0, 0, 250);
        if (id == TFCLayerUtil.OCEAN) return new Color(60, 100, 250);
        if (id == TFCLayerUtil.PLAINS) return new Color(0, 150, 0);
        if (id == TFCLayerUtil.HILLS) return new Color(30, 130, 30);
        if (id == TFCLayerUtil.LOWLANDS) return new Color(20, 200, 20);
        if (id == TFCLayerUtil.LOW_CANYONS) return new Color(40, 100, 40);
        if (id == TFCLayerUtil.ROLLING_HILLS) return new Color(100, 100, 0);
        if (id == TFCLayerUtil.BADLANDS) return new Color(150, 100, 0);
        if (id == TFCLayerUtil.PLATEAU) return new Color(200, 100, 0);
        if (id == TFCLayerUtil.OLD_MOUNTAINS) return new Color(200, 150, 100);
        if (id == TFCLayerUtil.MOUNTAINS) return new Color(200, 200, 200);
        if (id == TFCLayerUtil.FLOODED_MOUNTAINS) return new Color(180, 180, 250);
        if (id == TFCLayerUtil.CANYONS) return new Color(200, 0, 150);
        if (id == TFCLayerUtil.SHORE) return new Color(255, 230, 160);
        if (id == TFCLayerUtil.LAKE) return new Color(120, 200, 255);
        if (id == TFCLayerUtil.RIVER) return new Color(80, 140, 255);
        return Color.BLACK;
    }
}
