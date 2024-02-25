/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.drawing;

import java.awt.Color;
import java.util.Locale;
import java.util.Random;
import java.util.function.DoubleFunction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.Artist;
import net.dries007.tfc.TestHelper;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.layer.framework.AreaFactory;
import net.dries007.tfc.world.region.ChooseRocks;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.region.RegionPartition;
import net.dries007.tfc.world.region.RiverEdge;
import net.dries007.tfc.world.river.MidpointFractal;
import net.dries007.tfc.world.settings.Settings;

import static net.dries007.tfc.world.layer.TFCLayers.*;

@Disabled
@SuppressWarnings("SameParameterValue")
public class RegionGeneratorTest extends TestHelper
{
    final DoubleFunction<Color> blue = Artist.Colors.linearGradient(
        new Color(50, 50, 150),
        new Color(100, 140, 255));

    final DoubleFunction<Color> green = Artist.Colors.linearGradient(
        new Color(0, 100, 0),
        new Color(80, 200, 80));

    final DoubleFunction<Color> temperature = Artist.Colors.multiLinearGradient(
        new Color(180, 20, 240),
        new Color(0, 180, 240),
        new Color(180, 180, 220),
        new Color(210, 210, 0),
        new Color(200, 120, 60),
        new Color(200, 40, 40));

    @Test
    public void testStitchedRegions()
    {
        drawStitchedRegion(newRegionGenerator(), RegionGenerator.Task.CHOOSE_ROCKS);
    }

    @Test
    public void testSingleRegion()
    {
        newRegionGenerator().visualizeRegion(0, 0, (task, region) -> {
            final String taskName = taskName("", task);
            final Artist.Pixel<Color> pixel = drawRegion(task, region);

            if (task == RegionGenerator.Task.ADD_RIVERS_AND_LAKES)
            {
                final int size = 100;
                final int square = 10;
                final int half = square / 2;
                Artist.custom((v, g) -> {
                        Artist.raw().center(size).size(size * 2 * square).draw(pixel, g);

                        g.setColor(new Color(30, 180, 250));

                        for (RiverEdge edge : region.rivers())
                        {
                            final int dx = (int) Math.round(((edge.drain().x() + size) * square) + half);
                            final int dz = (int) Math.round(((edge.drain().y() + size) * square) + half);
                            final int sx = (int) Math.round(((edge.source().x() + size) * square) + half);
                            final int sz = (int) Math.round(((edge.source().y() + size) * square) + half);

                            g.drawLine(dx, dz, sx, sz);
                        }
                    })
                    .center(size)
                    .size(size * 2 * square)
                    .draw(taskName);
            }
            else
            {
                Artist.raw()
                    .centerSized(100)
                    .draw(taskName, pixel);
            }
        });
    }

    @Test
    public void testDrawingRiversFromPartition()
    {
        drawRegionWithRivers(newRegionGenerator(), RegionGenerator.Task.CHOOSE_BIOMES);
    }

    @Test
    public void testBiomesAtScale()
    {
        final Artist.Typed<AreaFactory, Integer> artist = Artist.forMap(factory -> {
            final Area area = factory.get();
            return Artist.Pixel.coerceInt(area::get);
        });

        final RegionGenerator generator = newRegionGenerator();
        final AreaFactory biomeLayer = TFCLayers.createRegionBiomeLayer(generator, generator.seed());

        artist.color(RegionGeneratorTest::biomeColor);
        artist.dimensionsSized(1000).draw("biomes_4km", biomeLayer);
        artist.dimensions(4000).size(1000).draw("biomes_16km", biomeLayer);
    }

    private void drawRegionWithRivers(RegionGenerator rn, RegionGenerator.Task task)
    {
        Artist.raw()
            .dimensions(100)
            .size(800)
            .draw(taskName("_rivers", task), (xi, zi) -> {
                final int x = (int) xi;
                final int z = (int) zi;
                final float xf = (float) xi;
                final float zf = (float) zi;
                final RegionPartition.Point point = rn.getOrCreatePartitionPoint(x, z);
                for (RiverEdge edge : point.rivers())
                {
                    final MidpointFractal river = edge.fractal();
                    if (river.maybeIntersect(xf, zf, 0.1f) && river.intersect(xf, zf, 0.1f))
                    {
                        return new Color(100, 210, 250);
                    }
                }

                final Region region = rn.getOrCreateRegion(x, z);

                return drawRegion(task, region).apply(x, z);
            });
    }

    private void drawStitchedRegion(RegionGenerator rn, RegionGenerator.Task task)
    {
        Artist.raw()
            .dimensionsSized(1000)
            .draw(taskName("_stitched", task), Artist.Pixel.coerceInt((x, y) -> {
                final Region region = rn.getOrCreateRegion(x, y);
                return drawRegion(task, region).apply(x, y);
            }));
    }

    private String taskName(String name, RegionGenerator.Task task)
    {
        return "region%s_%02d_%s".formatted(name, task.ordinal(), task.name().toLowerCase(Locale.ROOT));
    }

    private Artist.Pixel<Color> drawRegion(RegionGenerator.Task task, Region region)
    {
        return Artist.Pixel.coerceInt((x, y) -> {
            if (!region.isIn(x, y)) return new Color(100, 100, 100);
            final Region.Point point = region.at(x, y);
            if (point == null) return new Color(160, 160, 160);
            if (task == RegionGenerator.Task.ANNOTATE_DISTANCE_TO_CELL_EDGE)
            {
                return blue.apply(point.distanceToEdge / 24f);
            }
            if (task == RegionGenerator.Task.CHOOSE_BIOMES)
            {
                return biomeColor(point.biome);
            }
            if (task == RegionGenerator.Task.CHOOSE_ROCKS)
            {
                final double value = new Random(point.rock >> 2).nextDouble();
                return switch (point.rock & 0b11)
                    {
                        case ChooseRocks.OCEAN -> blue.apply(value);
                        case ChooseRocks.LAND -> green.apply(value);
                        case ChooseRocks.VOLCANIC -> new Color(200, (int) (100 * value), 100);
                        case ChooseRocks.UPLIFT -> new Color(180, (int) (180 * value), 200);
                        default -> throw new RuntimeException("value: " + point.rock);
                    };
            }
            if (!point.land())
            {
                if (task == RegionGenerator.Task.ANNOTATE_BASE_LAND_HEIGHT)
                {
                    return point.baseOceanDepth < 4 ? new Color(150, 160, 255) :
                        point.baseOceanDepth < 8 ?
                            new Color(120, 120, 240) :
                            new Color(100, 100, 200);
                }
                return point.shore() ?
                    (point.river() ?
                        new Color(150, 160, 255) :
                        new Color(120, 120, 240)) :
                    blue.apply(0.5 + 0.5 * region.noise());
            }

            return switch (task)
                {
                    default -> new Color(0, 130, 0);
                    case ADD_MOUNTAINS -> point.mountain() ?
                        (point.baseLandHeight <= 2 ?
                            new Color(240, 110, 50) :
                            new Color(150, 150, 150)) :
                        green.apply(point.baseLandHeight / 24f);
                    case ANNOTATE_DISTANCE_TO_OCEAN -> green.apply(point.distanceToOcean / 20f);
                    case ADD_RIVERS_AND_LAKES -> point.lake() ? new Color(150, 160, 255) : green.apply(point.baseLandHeight / 24f);
                    case ANNOTATE_BASE_LAND_HEIGHT -> green.apply(point.baseLandHeight / 24f);
                    case ANNOTATE_BIOME_ALTITUDE -> green.apply(Mth.clampedMap(point.discreteBiomeAltitude(), 0, 3, 0, 1));
                    case ANNOTATE_CLIMATE -> temperature.apply(Mth.clampedMap(point.temperature, -35f, 35f, 0f, 0.999f));
                    case ANNOTATE_RAINFALL -> temperature.apply(Mth.clampedMap(point.rainfall, 0f, 500f, 0f, 0.999f));
                };
        });
    }

    public static Color biomeColor(int biome)
    {
        if (biome == OCEAN) return new Color(0, 0, 220);
        if (biome == OCEAN_REEF) return new Color(70, 160, 250);
        if (biome == DEEP_OCEAN) return new Color(0, 0, 160);
        if (biome == DEEP_OCEAN_TRENCH) return new Color(0, 0, 80);
        if (biome == LAKE) return new Color(30, 30, 255);
        if (biome == MOUNTAIN_LAKE || biome == OCEANIC_MOUNTAIN_LAKE || biome == OLD_MOUNTAIN_LAKE || biome == VOLCANIC_MOUNTAIN_LAKE || biome == PLATEAU_LAKE) return new Color(180, 180, 255);
        if (biome == RIVER) return new Color(0, 200, 255);

        if (biome == OCEANIC_MOUNTAINS || biome == VOLCANIC_OCEANIC_MOUNTAINS) return new Color(255, 0, 255);
        if (biome == CANYONS) return new Color(180, 60, 255);
        if (biome == LOW_CANYONS) return new Color(200, 110, 255);
        if (biome == LOWLANDS) return new Color(220, 150, 230);

        if (biome == MOUNTAINS || biome == VOLCANIC_MOUNTAINS) return new Color(255, 50, 50);
        if (biome == OLD_MOUNTAINS) return new Color(240, 100, 100);
        if (biome == PLATEAU) return new Color(210, 120, 120);

        if (biome == BADLANDS) return new Color(255, 150, 0);
        if (biome == INVERTED_BADLANDS) return new Color(240, 180, 0);

        if (biome == SHORE) return new Color(230, 210, 130);

        if (biome == HIGHLANDS) return new Color(20, 80, 30);
        if (biome == ROLLING_HILLS) return new Color(50, 100, 50);
        if (biome == HILLS) return new Color(80, 130, 80);
        if (biome == PLAINS) return new Color(100, 200, 100);

        return Color.BLACK;
    }

    private RegionGenerator newRegionGenerator()
    {
        return new RegionGenerator(new Settings(false, 0, 0, 0, 20_000, 0, 20_000, 0, null, 0.5f, 0.5f), new XoroshiroRandomSource(seed()));
    }
}
