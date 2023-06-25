/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.visualizations;

import java.awt.Color;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.Artist;
import net.dries007.tfc.TestHelper;
import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.Area;
import net.dries007.tfc.world.layer.framework.AreaFactory;
import net.dries007.tfc.world.layer.framework.TypedArea;
import net.dries007.tfc.world.layer.framework.TypedAreaFactory;

import static net.dries007.tfc.world.layer.TFCLayers.*;

@Disabled
public class TFCLayersVisualizations extends TestHelper
{
    public static final Artist.Typed<TypedAreaFactory<Plate>, Plate> PLATES = Artist.forMap(factory -> {
        final TypedArea<Plate> area = factory.get();
        return Artist.Pixel.coerceInt(area::get);
    });
    public static final Artist.Typed<AreaFactory, Integer> AREA = Artist.forMap(factory -> {
        final Area area = factory.get();
        return Artist.Pixel.coerceInt(area::get);
    });
    public static final Artist.Raw RAW = Artist.raw();

    @Test
    public void testCreateOverworldBiomeLayer()
    {
        final AreaFactory layer = TFCLayers.createOverworldBiomeLayer(seed(), (name, index, instance) -> {
            PLATES.color(this::plateElevationColor);
            PLATES.centerSized(index == 1 ? 10 : 20).draw(name + '_' + index, instance);
            PLATES.centerSized(index == 1 ? 100 : 200).draw(name + '_' + index + "_wide", instance);
        }, (name, index, instance) -> {
            switch (name)
            {
                case "plate_boundary" -> {
                    AREA.centerSized(20).color(this::plateBoundaryColor).draw(name + '_' + index, instance);
                    AREA.centerSized(200).draw(name + '_' + index + "_wide", instance);
                }
                case "lake" -> {
                    int zoom;
                    if (index <= 1) zoom = 0;
                    else if (index <= 3) zoom = 1;
                    else if (index <= 5) zoom = 2;
                    else zoom = 3;

                    AREA.centerSized((1 << zoom) * 40).color(this::lakeColor).draw(name + '_' + index, instance);
                }
                case "biomes" -> {
                    int zoom;
                    if (index <= 2) zoom = 0;
                    else if (index <= 5) zoom = 1;
                    else if (index <= 7) zoom = 2;
                    else if (index <= 10) zoom = 3;
                    else if (index <= 14) zoom = index - 7;
                    else zoom = 8;

                    AREA.color(this::biomeColor).center((1 << zoom) * 16).size(Math.min(1024, (1 << zoom) * 16)).draw(name + '_' + index, instance);
                }
            }
        });

        AREA.color(this::biomeColor).center(1250 * 4).size(1250* 2); // 10km image (biomes are 1/4 scale)
        AREA.draw("biomes_40km", layer);
    }

    @Test
    public void testOverworldForestLayer()
    {
        final AreaFactory layer = TFCLayers.createOverworldForestLayer(seed(), (name, index, instance) -> {
            int zoom;
            if (index <= 2) zoom = 1;
            else if (index <= 4) zoom = 2;
            else if (index == 5) zoom = 3;
            else if (index <= 8) zoom = 4;
            else if (index <= 12) zoom = index - 4;
            else zoom = 8;

            AREA.color(this::forestColor).centerSized((1 << zoom));
            AREA.draw(name + '_' + index, instance);
        });

        AREA.color(this::forestColor).center(1250).size(1250); // 10km image (biomes are 1/4 scale), at 1 pixel = 4 blocks
        AREA.draw("forest_10km", layer);
    }

    @Test
    public void testBiomesWithForest()
    {
        final Area biomeArea = TFCLayers.createOverworldBiomeLayer(seed(), IArtist.nope(), IArtist.nope()).get();
        final Area forestArea = TFCLayers.createOverworldForestLayer(seed(), IArtist.nope()).get();

        RAW.center(1250).size(1250); // 10 km image, at 1 pixel = 4 blocks
        RAW.draw("biomes_with_forests_10km", Artist.Pixel.coerceInt((x, z) -> {
            int value = biomeArea.get(x, z);
            Color oceanBiomeColor = waterBiomeColor(value);
            if (oceanBiomeColor != null)
            {
                return oceanBiomeColor;
            }
            return forestColor(forestArea.get(x, z));
        }));
    }

    @Test
    public void testOverworldRockLayer()
    {
        final AreaFactory rockArea = TFCLayers.createOverworldRockLayer(seed(), 7, 20);
        AREA.color(Artist.Colors.RANDOM_INT)
            .center(5_000).size(1000)
            .draw("rocks_10km", rockArea);

    }

    private Color plateElevationColor(Plate plate)
    {
        if (plate.oceanic())
        {
            return new Color(0, Mth.clamp((int) (plate.elevation() * 255), 0, 255), 255);
        }
        else
        {
            return new Color(0, Mth.clamp((int) (100 + 155 * plate.elevation()), 100, 255), 0);
        }
    }

    private Color biomeColor(int biome) { return biomeColorS(biome); }

    public static Color biomeColorS(int biome)
    {
        if (biome == OCEAN) return new Color(0, 0, 220);
        if (biome == OCEAN_REEF) return new Color(70, 160, 250);
        if (biome == DEEP_OCEAN) return new Color(0, 0, 160);
        if (biome == DEEP_OCEAN_TRENCH) return new Color(0, 0, 80);
        if (biome == LAKE) return new Color(30, 30, 255);

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

        if (biome == ROLLING_HILLS) return new Color(50, 100, 50);
        if (biome == HILLS) return new Color(80, 130, 80);
        if (biome == PLAINS) return new Color(100, 200, 100);

        return Color.BLACK;
    }

    @Nullable
    private Color waterBiomeColor(int id)
    {
        if (id == DEEP_OCEAN) return new Color(0, 0, 250);
        if (id == OCEAN) return new Color(60, 100, 250);
        if (id == SHORE) return new Color(255, 230, 160);
        if (id == LAKE) return new Color(120, 200, 255);
        if (id == RIVER) return new Color(80, 140, 255);
        if (id == OLD_MOUNTAIN_LAKE || id == OCEANIC_MOUNTAIN_LAKE || id == PLATEAU_LAKE || id == MOUNTAIN_LAKE || id == VOLCANIC_MOUNTAIN_LAKE || id == VOLCANIC_OCEANIC_MOUNTAIN_LAKE)
            return new Color(150, 140, 205);
        if (id == OLD_MOUNTAIN_RIVER || id == OCEANIC_MOUNTAIN_RIVER || id == MOUNTAIN_RIVER || id == VOLCANIC_OCEANIC_MOUNTAIN_RIVER || id == VOLCANIC_MOUNTAIN_RIVER)
            return new Color(130, 110, 205);
        if (id == DEEP_OCEAN_TRENCH) return new Color(15, 40, 170);
        if (id == OCEAN_OCEAN_CONVERGING_MARKER) return new Color(160, 160, 255);
        if (id == OCEAN_OCEAN_DIVERGING_MARKER) return new Color(0, 0, 100);
        if (id == OCEAN_REEF_MARKER) return new Color(200, 200, 0);
        if (id == OCEAN_REEF) return new Color(200, 250, 100);
        return null;
    }

    private Color lakeColor(int id)
    {
        if (id == LAKE_MARKER) return new Color(20, 140, 255);
        if (id == INLAND_MARKER) return new Color(100, 100, 100);
        return Color.BLACK;
    }

    private Color forestColor(int id)
    {
        if (id == FOREST_NONE) return new Color(140, 160, 140);
        if (id == FOREST_NORMAL) return new Color(70, 180, 70);
        if (id == FOREST_SPARSE) return new Color(30, 120, 30);
        if (id == FOREST_EDGE) return new Color(110, 140, 40);
        if (id == FOREST_OLD) return new Color(0, 80, 0);
        return Color.BLACK;
    }
}
