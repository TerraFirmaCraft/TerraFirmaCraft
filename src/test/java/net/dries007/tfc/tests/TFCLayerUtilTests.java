/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.tests;

import java.awt.*;
import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.ZoomLayer;

import net.dries007.tfc.Artist;
import net.dries007.tfc.world.layer.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class TFCLayerUtilTests
{
    public static final Artist.Typed<Integer, IAreaFactory<LazyArea>> IMAGES = Artist.map(factory -> {
        LazyArea area = factory.make();
        return (left, right) -> area.get((int) left, (int) right);
    });

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

        mainLayer = new IslandLayer(6).run(contextFactory.get());
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = ZoomLayer.FUZZY.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = AddIslandLayer.NORMAL.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = ZoomLayer.NORMAL.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = AddIslandLayer.NORMAL.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        for (int i = 0; i < 2; i++)
        {
            mainLayer = AddIslandLayer.HEAVY.run(contextFactory.get(), mainLayer);
            IMAGES.draw("layer_" + ++layer, mainLayer);

            mainLayer = SmoothLayer.INSTANCE.run(contextFactory.get(), mainLayer);
            IMAGES.draw("layer_" + ++layer, mainLayer);
        }

        // Oceans and Continents => Elevation Mapping
        IMAGES.color(this::elevationColor).dimensions(400);

        mainLayer = ElevationLayer.INSTANCE.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = ZoomLayer.NORMAL.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        // Elevation Mapping => Rivers
        riverLayer = ZoomLayer.NORMAL.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        for (int i = 0; i < 6; i++)
        {
            riverLayer = ZoomLayer.NORMAL.run(contextFactory.get(), riverLayer);
            IMAGES.draw("layer_" + ++layer, mainLayer);
        }

        riverLayer = RiverLayer.INSTANCE.run(contextFactory.get(), riverLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        riverLayer = ZoomLayer.NORMAL.run(contextFactory.get(), riverLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        IMAGES.color(this::biomeColor).dimensions(1000);

        // Elevation Mapping => Biomes
        mainLayer = BiomeLayer.INSTANCE.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = ZoomLayer.NORMAL.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = AddIslandLayer.NORMAL.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = ZoomLayer.NORMAL.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = RemoveOceanLayer.INSTANCE.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = OceanLayer.INSTANCE.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = EdgeBiomeLayer.INSTANCE.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = AddLakeLayer.INSTANCE.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        for (int i = 0; i < 2; i++)
        {
            mainLayer = ZoomLayer.NORMAL.run(contextFactory.get(), mainLayer);
            IMAGES.draw("layer_" + ++layer, mainLayer);
        }

        mainLayer = ShoreLayer.CASTLE.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        for (int i = 0; i < 4; i++)
        {
            mainLayer = ZoomLayer.NORMAL.run(contextFactory.get(), mainLayer);
            IMAGES.draw("layer_" + ++layer, mainLayer);
        }

        IMAGES.dimensions(10000);

        mainLayer = SmoothLayer.INSTANCE.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = MixRiverLayer.INSTANCE.run(contextFactory.get(), mainLayer, riverLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = BiomeRiverWidenLayer.MEDIUM.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);

        mainLayer = BiomeRiverWidenLayer.LOW.run(contextFactory.get(), mainLayer);
        IMAGES.draw("layer_" + ++layer, mainLayer);
    }

    private Color landColor(double value)
    {
        int id = (int) value;
        if (id == TFCLayerUtil.DEEP_OCEAN) return Color.BLUE;
        if (id == TFCLayerUtil.PLAINS) return Color.GREEN;
        return Color.BLACK;
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
