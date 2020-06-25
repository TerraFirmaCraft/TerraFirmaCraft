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
import net.dries007.tfc.world.layer.traits.ITypedAreaFactory;
import net.dries007.tfc.world.layer.traits.LazyTypedAreaLayerContext;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.VoronoiNoise2D;
import org.junit.jupiter.api.Test;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

class TFCLayerUtilTests
{
    static final Artist.Typed<ITypedAreaFactory<Plate>, Plate> PLATES = Artist.forMap(factory -> Artist.Pixel.coerceInt(factory.make()::get));
    static final Artist.Typed<IAreaFactory<LazyArea>, Integer> AREA = Artist.forMap(factory -> Artist.Pixel.coerceInt(factory.make()::get));
    static final Artist.Noise<INoise2D> NOISE = Artist.forNoise(noise -> Artist.NoisePixel.coerceFloat(noise::noise));

    @Test
    void testPlateTectonicsLayers()
    {
        final long seed = System.currentTimeMillis();
        final Random random = new Random(seed);

        final Supplier<LazyTypedAreaLayerContext<Plate>> plateContext = () -> new LazyTypedAreaLayerContext<>(25, seed, random.nextLong());
        final Supplier<LazyAreaLayerContext> layerContext = () -> new LazyAreaLayerContext(25, seed, random.nextLong());

        ITypedAreaFactory<Plate> plateLayer;
        IAreaFactory<LazyArea> mainLayer, riverLayer;
        int count = 0;

        final VoronoiNoise2D platesVoronoi = new VoronoiNoise2D(random.nextLong(), 2f, 2);

        PLATES.center(100);
        NOISE.center(100);
        AREA.center(100);

        plateLayer = new PlateGenerationLayer(platesVoronoi, 0.2f, 70).apply(plateContext.get()); // 45

        PLATES.color(this::elevationPlateColor).draw("plates_" + ++count, plateLayer);

        plateLayer = TypedZoomLayer.<Plate>fuzzy().run(plateContext.get(), plateLayer);
        PLATES.draw("plates_" + ++count, plateLayer);

        mainLayer = PlateBoundaryLayer.INSTANCE.run(layerContext.get(), plateLayer);
        AREA.color(this::boundaryColor).draw("plates_" + ++count, mainLayer);

        // RIVERS
        count = 0;
        riverLayer = new FloatNoiseLayer(new VoronoiNoise2D(random.nextLong()).spread(0.12f)).run(layerContext.get());
        AREA.color(this::riverNoiseColor).draw("river_" + ++count, riverLayer);

        AREA.center(1_000);

        for (int i = 0; i < 2; i++)
        {
            riverLayer = ZoomLayer.FUZZY.run(layerContext.get(), riverLayer);
            AREA.draw("river_" + ++count, riverLayer);

            riverLayer = ZoomLayer.NORMAL.run(layerContext.get(), riverLayer);
            AREA.draw("river_" + ++count, riverLayer);
        }

        for (int i = 0; i < 2; i++)
        {
            riverLayer = ZoomLayer.NORMAL.run(layerContext.get(), riverLayer);
            AREA.draw("river_" + ++count, riverLayer);
        }

        AREA.center(5_000);

        riverLayer = RiverLayer.INSTANCE.run(layerContext.get(), riverLayer);
        AREA.color(this::riverColor).draw("river_" + ++count, riverLayer);

        riverLayer = ZoomLayer.NORMAL.run(layerContext.get(), riverLayer);
        AREA.draw("river_" + ++count, riverLayer);

        AREA.center(100);
        count = 0;

        mainLayer = PlateBiomeLayer.INSTANCE.run(layerContext.get(), mainLayer);
        AREA.color(this::biomeColor).draw("biomes_" + ++count, mainLayer);

        mainLayer = OceanBorderLayer.INSTANCE.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        mainLayer = AddLakeLayer.LARGE.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        mainLayer = LargeLakeLayer.INSTANCE.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        AREA.center(200);

        mainLayer = ArchipelagoLayer.INSTANCE.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        AREA.center(400);

        mainLayer = EdgeBiomeLayer.INSTANCE.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        mainLayer = AddLakeLayer.SMALL.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        AREA.center(1_000);

        mainLayer = ShoreLayer.INSTANCE.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        for (int i = 0; i < 4; i++)
        {
            mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
            AREA.draw("biomes_" + ++count, mainLayer);
        }

        AREA.center(5_000);

        mainLayer = SmoothLayer.INSTANCE.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        mainLayer = MixRiverLayer.INSTANCE.run(layerContext.get(), mainLayer, riverLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        mainLayer = BiomeRiverWidenLayer.MEDIUM.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        mainLayer = BiomeRiverWidenLayer.LOW.run(layerContext.get(), mainLayer);

        AREA.draw("biomes_40k", mainLayer);
    }

    private Color elevationPlateColor(Plate plate)
    {
        return Artist.Colors.LINEAR_GRAY.apply(Artist.Scales.DYNAMIC_RANGE.apply(plate.getElevation(), -1, 1));
    }

    private Color boundaryColor(int value)
    {
        if (value == OCEANIC) return new Color(0, 0, 200);
        if (value == CONTINENTAL_LOW) return new Color(50, 200, 50);
        if (value == CONTINENTAL_MID) return new Color(50, 150, 50);
        if (value == CONTINENTAL_HIGH) return new Color(70, 100, 70);
        if (value == OCEAN_OCEAN_CONVERGING) return new Color(100, 0, 200);
        if (value == OCEAN_OCEAN_DIVERGING) return new Color(0, 100, 200);
        if (value == OCEAN_CONTINENT_CONVERGING) return new Color(200, 0, 100);
        if (value == OCEAN_CONTINENT_DIVERGING) return new Color(200, 0, 250);
        if (value == CONTINENT_CONTINENT_CONVERGING) return new Color(250, 150, 20);
        if (value == CONTINENT_CONTINENT_DIVERGING) return new Color(200, 100, 20);
        return Color.BLACK;
    }

    private Color biomeColor(int id)
    {
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

    private Color riverNoiseColor(int id)
    {
        return new Color(id & 0xFFFFFF);
    }

    private Color riverColor(int id)
    {
        if (id == RIVER_MARKER) return new Color(80, 140, 255);
        return Color.BLACK;
    }
}
