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
import net.dries007.tfc.world.biome.TFCBiomeProvider;
import net.dries007.tfc.world.layer.*;
import net.dries007.tfc.world.layer.traits.ITypedAreaFactory;
import net.dries007.tfc.world.layer.traits.LazyTypedAreaLayerContext;
import net.dries007.tfc.world.noise.VoronoiNoise2D;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

@Disabled
class TFCLayerUtilTests
{
    static final Artist.Typed<ITypedAreaFactory<Plate>, Plate> PLATES = Artist.forMap(factory -> Artist.Pixel.coerceInt(factory.make()::get));
    static final Artist.Typed<IAreaFactory<LazyArea>, Integer> AREA = Artist.forMap(factory -> Artist.Pixel.coerceInt(factory.make()::get));

    @Test
    void testLakeLayers()
    {
        // Only for imaging
        final long seed = System.currentTimeMillis();
        int count = 0;

        AREA.center(100).color(this::lakeColor);

        // Copy pasta from TFCLayerUtil with added draw calls
        final Random random = new Random(seed);
        final Supplier<LazyAreaLayerContext> layerContext = () -> new LazyAreaLayerContext(25, seed, random.nextLong());

        IAreaFactory<LazyArea> lakeLayer;

        // Lakes
        AREA.center(1_000);
        lakeLayer = NullLayer.INSTANCE.run(layerContext.get());
        AREA.draw("lakes_" + ++count, lakeLayer);
        lakeLayer = LakeLayer.LARGE.run(layerContext.get(), lakeLayer);
        AREA.draw("lakes_" + ++count, lakeLayer);
        lakeLayer = LargeLakeLayer.INSTANCE.run(layerContext.get(), lakeLayer);
        AREA.draw("lakes_" + ++count, lakeLayer);
        for (int i = 0; i < 2; i++)
        {
            lakeLayer = ZoomLayer.NORMAL.run(layerContext.get(), lakeLayer);
            AREA.draw("lakes_" + ++count, lakeLayer);
        }
        AREA.center(4_000);
        lakeLayer = LakeLayer.SMALL.run(layerContext.get(), lakeLayer);
        AREA.draw("lakes_" + ++count, lakeLayer);
    }

    @Test
    void testRiverLayers()
    {
        // Only for imaging
        final long seed = System.currentTimeMillis();
        int count = 0;

        AREA.center(100).color(this::riverColor);

        // Copy pasta from TFCLayerUtil with added draw calls
        final Random random = new Random(seed);
        final Supplier<LazyAreaLayerContext> layerContext = () -> new LazyAreaLayerContext(25, seed, random.nextLong());

        IAreaFactory<LazyArea> riverLayer;

        // Rivers
        riverLayer = new FloatNoiseLayer(new VoronoiNoise2D(random.nextLong()).spread(0.12f)).run(layerContext.get());
        AREA.draw("rivers_" + ++count, riverLayer);

        for (int i = 0; i < 4; i++)
        {
            riverLayer = ZoomLayer.NORMAL.run(layerContext.get(), riverLayer);
            AREA.draw("rivers_" + ++count, riverLayer);
        }

        riverLayer = RiverLayer.INSTANCE.run(layerContext.get(), riverLayer);
        AREA.draw("rivers_" + ++count, riverLayer);
        riverLayer = RiverAcuteVertexLayer.INSTANCE.run(layerContext.get(), riverLayer);
        AREA.draw("rivers_" + ++count, riverLayer);
        riverLayer = ZoomLayer.NORMAL.run(layerContext.get(), riverLayer);
        AREA.draw("rivers_" + ++count, riverLayer);
    }

    @Test
    void testPlateTectonicLayers()
    {
        // Only for imaging
        final long seed = System.currentTimeMillis();
        final TFCBiomeProvider.LayerSettings layerSettings = new TFCBiomeProvider.LayerSettings();
        int count = 0;

        PLATES.center(100);

        // Copy pasta from TFCLayerUtil with added draw calls
        final Random random = new Random(seed);
        final Supplier<LazyTypedAreaLayerContext<Plate>> plateContext = () -> new LazyTypedAreaLayerContext<>(25, seed, random.nextLong());
        final Supplier<LazyAreaLayerContext> layerContext = () -> new LazyAreaLayerContext(25, seed, random.nextLong());

        ITypedAreaFactory<Plate> plateLayer;
        IAreaFactory<LazyArea> mainLayer;


        // Tectonic Plates - generate plates and annotate border regions with converging / diverging boundaries
        plateLayer = new PlateGenerationLayer(new VoronoiNoise2D(random.nextLong()), 0.2f, layerSettings.getOceanPercent()).apply(plateContext.get());
        PLATES.color(this::elevationPlateColor).draw("plates_" + ++count, plateLayer);
        plateLayer = TypedZoomLayer.<Plate>fuzzy().run(plateContext.get(), plateLayer);
        PLATES.draw("plates_" + ++count, plateLayer);
        mainLayer = PlateBoundaryLayer.INSTANCE.run(layerContext.get(), plateLayer);
        AREA.color(this::boundaryColor).draw("plates_" + ++count, mainLayer);
    }

    @Test
    void testBiomeLayers()
    {
        // Only for imaging
        final long seed = System.currentTimeMillis();
        final TFCBiomeProvider.LayerSettings layerSettings = new TFCBiomeProvider.LayerSettings();
        int count = 0;

        AREA.center(100).color(this::biomeColor);

        // Copy pasta from TFCLayerUtil with added draw calls
        final Random random = new Random(seed);
        final Supplier<LazyTypedAreaLayerContext<Plate>> plateContext = () -> new LazyTypedAreaLayerContext<>(25, seed, random.nextLong());
        final Supplier<LazyAreaLayerContext> layerContext = () -> new LazyAreaLayerContext(25, seed, random.nextLong());

        ITypedAreaFactory<Plate> plateLayer;
        IAreaFactory<LazyArea> mainLayer, riverLayer, lakeLayer;

        // Tectonic Plates - generate plates and annotate border regions with converging / diverging boundaries
        plateLayer = new PlateGenerationLayer(new VoronoiNoise2D(random.nextLong()), 0.2f, layerSettings.getOceanPercent()).apply(plateContext.get());
        plateLayer = TypedZoomLayer.<Plate>fuzzy().run(plateContext.get(), plateLayer);
        mainLayer = PlateBoundaryLayer.INSTANCE.run(layerContext.get(), plateLayer);

        // Rivers
        riverLayer = new FloatNoiseLayer(new VoronoiNoise2D(random.nextLong()).spread(0.12f)).run(layerContext.get());

        for (int i = 0; i < 4; i++)
        {
            riverLayer = ZoomLayer.NORMAL.run(layerContext.get(), riverLayer);
        }

        riverLayer = RiverLayer.INSTANCE.run(layerContext.get(), riverLayer);
        riverLayer = RiverAcuteVertexLayer.INSTANCE.run(layerContext.get(), riverLayer);
        riverLayer = ZoomLayer.NORMAL.run(layerContext.get(), riverLayer);

        // Biomes
        mainLayer = PlateBiomeLayer.INSTANCE.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);
        mainLayer = OceanBorderLayer.INSTANCE.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        // Lakes
        lakeLayer = NullLayer.INSTANCE.run(layerContext.get());
        lakeLayer = LakeLayer.LARGE.run(layerContext.get(), lakeLayer);
        lakeLayer = LargeLakeLayer.INSTANCE.run(layerContext.get(), lakeLayer);
        for (int i = 0; i < 2; i++)
        {
            lakeLayer = ZoomLayer.NORMAL.run(layerContext.get(), lakeLayer);
        }
        lakeLayer = LakeLayer.SMALL.run(layerContext.get(), lakeLayer);

        // Add biome level features - lakes, island chains, edge biomes, shores
        mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);
        mainLayer = ArchipelagoLayer.INSTANCE.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);
        mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);
        mainLayer = EdgeBiomeLayer.INSTANCE.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);
        mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);
        mainLayer = MixLakeLayer.INSTANCE.run(layerContext.get(), mainLayer, lakeLayer);
        AREA.draw("biomes_" + ++count, mainLayer);
        mainLayer = ShoreLayer.INSTANCE.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        AREA.center(1_000);
        for (int i = 0; i < 4; i++)
        {
            mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
            AREA.draw("biomes_" + ++count, mainLayer);
        }

        mainLayer = SmoothLayer.INSTANCE.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        // Mix rivers and expand them in low biomes
        AREA.center(5_000);
        mainLayer = MixRiverLayer.INSTANCE.run(layerContext.get(), mainLayer, riverLayer);
        AREA.draw("biomes_" + ++count, mainLayer);
        mainLayer = BiomeRiverWidenLayer.MEDIUM.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);
        mainLayer = BiomeRiverWidenLayer.LOW.run(layerContext.get(), mainLayer);
        AREA.draw("biomes_" + ++count, mainLayer);

        AREA.center(20_000).draw("biomes_40k", mainLayer);
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

    private Color riverColor(int id)
    {
        if (id == RIVER_MARKER) return new Color(80, 140, 255);
        return Color.BLACK;
    }

    private Color lakeColor(int id)
    {
        if (id == LAKE_MARKER) return new Color(20, 140, 255);
        if (id == LARGE_LAKE_MARKER) return new Color(0, 100, 200);
        return Color.BLACK;
    }
}
