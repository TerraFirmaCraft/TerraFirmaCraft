/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.drawing;

import java.awt.Color;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.DoubleFunction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.test.TestSetup;
import net.dries007.tfc.world.region.ChooseRocks;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.region.RegionPartition;
import net.dries007.tfc.world.region.RiverEdge;
import net.dries007.tfc.world.settings.Settings;

import static net.dries007.tfc.world.layer.TFCLayers.*;
import static net.dries007.tfc.world.region.RegionGenerator.Task.*;

@Disabled
@SuppressWarnings("SameParameterValue")
public class RegionGeneratorTests implements TestSetup
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
    public void testRegionGenerator()
    {
        drawStitchedRegions("", EnumSet.allOf(RegionGenerator.Task.class), 250);
    }

    private void drawStitchedRegions(String name, Set<RegionGenerator.Task> tasksToDraw, int size)
    {
        record Pos(int x, int z) {}

        final RegionGenerator generator = newRegionGenerator();
        final Set<Pos> points = new HashSet<>();
        final Map<RegionGenerator.Task, Map<Pos, Color>> drawn = new HashMap<>();

        for (int x = 0; x < size; x++)
            for (int z = 0; z < size; z++)
                points.add(new Pos(x, z));

        @Nullable Pos pos;
        while ((pos = points.stream().findFirst().orElse(null)) != null)
        {
            generator.visualizeRegion(pos.x, pos.z, (task, region) -> {
                if (!tasksToDraw.contains(task)) return;
                final Map<Pos, Color> drawnTask = drawn.computeIfAbsent(task, key -> new HashMap<>());
                for (int rx = region.minX(); rx <= region.maxX(); rx++)
                    for (int rz = region.minZ(); rz <= region.maxZ(); rz++)
                        if (region.at(rx, rz) != null)
                        {
                            final Pos at = new Pos(rx, rz);
                            points.remove(at);
                            drawnTask.put(at, taskColor(task, region, rx, rz));
                        }
            });
        }

        drawn.forEach((task, drawnTask) -> Artist.raw()
            .dimensions(size)
            .size(size * (task == ADD_RIVERS_AND_LAKES ? 8 : 1))
            .draw(taskName(name, task), task == ADD_RIVERS_AND_LAKES
                ? drawWithRivers(generator, task)
                : Artist.Pixel.coerceInt((x, z) -> drawnTask.get(new Pos(x, z)))));
    }

    private Artist.Pixel<Color> drawWithRivers(RegionGenerator generator, RegionGenerator.Task task)
    {
        return (xi, zi) -> {
            final int x = (int) xi, z = (int) zi;
            final float xf = (float) xi, zf = (float) zi;
            final Region region = generator.getOrCreateRegion(x, z);
            final RegionPartition.Point point = generator.getOrCreatePartitionPoint(x, z);
            for (RiverEdge edge : point.rivers())
            {
                if (edge.fractal().intersect(xf, zf, 0.1f)) // Use a slightly larger distance than is typical, so we draw it more visibly
                {
                    return new Color(100, 210, 250);
                }
            }

            return taskColor(task, region, x, z);
        };
    }

    private String taskName(String name, RegionGenerator.Task task)
    {
        return "region%s_%02d_%s".formatted(name, task.ordinal(), task.name().toLowerCase(Locale.ROOT));
    }

    private Color taskColor(RegionGenerator.Task task, Region region, int x, int y)
    {
        if (!region.isIn(x, y)) return new Color(100, 100, 100);
        final Region.Point point = region.at(x, y);
        if (point == null) return new Color(160, 160, 160);
        if (task == ANNOTATE_DISTANCE_TO_CELL_EDGE)
        {
            return blue.apply(point.distanceToEdge / 24f);
        }
        if (task == CHOOSE_BIOMES)
        {
            return biomeColor(point.biome);
        }
        if (task == CHOOSE_ROCKS)
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
        if (task == ADD_CONTINENTS)
        {
            return blue.apply(0.5 + 0.5 * region.noise());
        }
        if (!point.land())
        {
            return switch (task) {
                case ANNOTATE_BASE_LAND_HEIGHT -> point.baseOceanDepth < 4 ? new Color(150, 160, 255) :
                    point.baseOceanDepth < 8 ?
                        new Color(120, 120, 240) :
                        new Color(100, 100, 200);
                case ANNOTATE_CLIMATE -> blue.apply(Mth.clampedMap(point.temperature, -35f, 35f, 0f, 0.999f));
                case ANNOTATE_RAINFALL -> blue.apply(Mth.clampedMap(point.rainfall, 0f, 500f, 0f, 0.999f));
                default -> point.shore() ?
                    (point.river() ?
                        new Color(150, 160, 255) :
                        new Color(120, 120, 240)) :
                    blue.apply(0.5 + 0.5 * region.noise());
            };
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
    }

    private Color biomeColor(int biome)
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
        return new RegionGenerator(new Settings(false, 0, 0, 0, 20_000, 0, 20_000, 0, null, 0.5f, 0.5f), new XoroshiroRandomSource(1798237841231L));
    }
}
