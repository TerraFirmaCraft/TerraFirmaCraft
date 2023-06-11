/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.visualizations;

import java.awt.Color;
import java.util.Locale;
import java.util.function.DoubleFunction;

import net.minecraft.util.Mth;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.Artist;
import net.dries007.tfc.TestHelper;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.RegionGenerator;

import static net.dries007.tfc.visualizations.TFCLayersVisualizations.*;

public class RegionVisualizations extends TestHelper
{
    final DoubleFunction<Color> colorWheel = Artist.Colors.multiLinearGradient(
        new Color(210, 20, 170),
        new Color(210, 100, 100),
        new Color(210, 210, 0),
        new Color(50, 160, 60),
        new Color(0, 180, 240)
    );

    final DoubleFunction<Color> temperature = Artist.Colors.multiLinearGradient(
        new Color(180, 20, 240),
        new Color(0, 180, 240),
        new Color(180, 180, 220),
        new Color(210, 210, 0),
        new Color(200, 120, 60),
        new Color(200, 40, 40)
    );

    final DoubleFunction<Color> blue = Artist.Colors.linearGradient(
        new Color(50, 50, 150),
        new Color(100, 140, 255));

    final DoubleFunction<Color> green = Artist.Colors.linearGradient(
        new Color(0, 100, 0),
        new Color(80, 200, 80));

    @Test
    public void testStitchedRegions()
    {
        final long seed = seed();
        final RegionGenerator rn = new RegionGenerator(seed);

        drawStitchedRegion(rn, RegionGenerator.Task.ADD_RIVERS_AND_LAKES);
    }

    private void drawStitchedRegion(RegionGenerator rn, RegionGenerator.Task task)
    {
        Artist.raw()
            .dimensionsSized(1000)
            .draw("regions_%02d_%s".formatted(task.ordinal(), task.name().toLowerCase(Locale.ROOT)), Artist.Pixel.coerceInt((x, y) -> {
                final Region region = rn.getOrCreateRegion(x, y);
                return drawRegion(task, region).apply(x, y);
            }));
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
                return biomeColorS(point.biome);
            }
            if (!point.land())
            {
                return switch(task) {
                    case ANNOTATE_BASE_LAND_HEIGHT -> point.baseOceanDepth < 4 ? new Color(150, 160, 255) :
                        point.baseOceanDepth < 8 ?
                            new Color(120, 120, 240) :
                            new Color(100, 100, 200);
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
        });
    }
}
