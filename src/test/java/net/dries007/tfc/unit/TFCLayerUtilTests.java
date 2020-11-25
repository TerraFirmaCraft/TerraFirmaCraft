package net.dries007.tfc.unit;

import java.awt.*;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;

import net.dries007.tfc.Artist;
import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.biome.TFCBiomeProvider;
import net.dries007.tfc.world.layer.Plate;
import net.dries007.tfc.world.layer.TFCLayerUtil;
import net.dries007.tfc.world.layer.traits.ITypedAreaFactory;
import org.junit.jupiter.api.Test;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

public class TFCLayerUtilTests
{
    static final Artist.Typed<ITypedAreaFactory<Plate>, Plate> PLATES = Artist.forMap(factory -> Artist.Pixel.coerceInt(factory.make()::get));
    static final Artist.Typed<IAreaFactory<LazyArea>, Integer> AREA = Artist.forMap(factory -> Artist.Pixel.coerceInt(factory.make()::get));
    static final Artist.Noise<IAreaFactory<LazyArea>> FLOAT_AREA = AREA.mapNoise(Float::intBitsToFloat);

    @Test
    public void testCreateOverworldBiomeLayer()
    {
        final long seed = System.currentTimeMillis();
        final TFCBiomeProvider.LayerSettings settings = new TFCBiomeProvider.LayerSettings();

        // Drawing is done via callbacks in TFCLayerUtil
        IArtist<ITypedAreaFactory<Plate>> plateArtist = (name, index, instance) -> {
            PLATES.center(index == 1 ? 10 : 20).color(this::plateElevationColor);
            PLATES.draw(name + '_' + index, instance);
        };

        IArtist<IAreaFactory<LazyArea>> layerArtist = (name, index, instance) -> {
            switch (name)
            {
                case "plate_boundary":
                    AREA.centerSized(20).color(this::plateBoundaryColor).draw(name + '_' + index, instance);
                    break;
                case "river":
                {
                    int zoom;
                    if (index <= 5) zoom = index - 1;
                    else if (index <= 7) zoom = 4;
                    else zoom = 5;

                    if (index <= 5)
                        FLOAT_AREA.centerSized((1 << zoom) * 40).color(Artist.Colors.LINEAR_BLUE_RED).draw(name + '_' + index, instance);
                    else AREA.centerSized((1 << zoom) * 10).color(this::riverColor).draw(name + '_' + index, instance);
                    break;
                }
                case "lake":
                {
                    int zoom;
                    if (index <= 1) zoom = 0;
                    else if (index <= 3) zoom = 1;
                    else if (index <= 5) zoom = 2;
                    else zoom = 3;

                    AREA.centerSized((1 << zoom) * 40).color(this::lakeColor).draw(name + '_' + index, instance);
                    break;
                }
                case "biomes":
                {
                    int zoom;
                    if (index <= 2) zoom = 0;
                    else if (index <= 4) zoom = 1;
                    else if (index <= 6) zoom = 2;
                    else if (index <= 9) zoom = 3;
                    else zoom = 4;

                    AREA.color(this::biomeColor).centerSized((1 << zoom) * 20).draw(name + '_' + index, instance);
                    break;
                }
            }
        };

        TFCLayerUtil.createOverworldBiomeLayer(seed, settings, plateArtist, layerArtist);
    }

    private Color plateElevationColor(Plate plate)
    {
        if (plate.isOceanic())
        {
            return new Color(0, MathHelper.clamp((int) (plate.getElevation() * 255), 0, 255), 255);
        }
        else
        {
            return new Color(0, MathHelper.clamp((int) (100 + 155 * plate.getElevation()), 100, 255), 0);
        }
    }

    private Color plateBoundaryColor(int value)
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
        if (id == DEEP_OCEAN) return new Color(0, 0, 250);
        if (id == OCEAN) return new Color(60, 100, 250);
        if (id == PLAINS) return new Color(0, 150, 0);
        if (id == HILLS) return new Color(30, 130, 30);
        if (id == LOWLANDS) return new Color(20, 200, 20);
        if (id == LOW_CANYONS) return new Color(40, 100, 40);
        if (id == ROLLING_HILLS) return new Color(100, 100, 0);
        if (id == BADLANDS) return new Color(150, 100, 0);
        if (id == PLATEAU) return new Color(200, 100, 0);
        if (id == OLD_MOUNTAINS) return new Color(200, 150, 100);
        if (id == MOUNTAINS) return new Color(200, 200, 200);
        if (id == FLOODED_MOUNTAINS) return new Color(180, 180, 250);
        if (id == CANYONS) return new Color(160, 60, 60);
        if (id == SHORE) return new Color(255, 230, 160);
        if (id == LAKE) return new Color(120, 200, 255);
        if (id == RIVER) return new Color(80, 140, 255);
        if (id == OLD_MOUNTAIN_LAKE || id == FLOODED_MOUNTAIN_LAKE || id == PLATEAU_LAKE || id == MOUNTAIN_LAKE)
            return new Color(150, 140, 205);
        if (id == OLD_MOUNTAIN_RIVER || id == FLOODED_MOUNTAIN_RIVER || id == MOUNTAIN_RIVER)
            return new Color(130, 110, 205);
        if (id == TFCLayerUtil.DEEP_OCEAN_RIDGE) return new Color(15, 40, 170);
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
        if (id == INLAND_MARKER) return new Color(100, 100, 100);
        return Color.BLACK;
    }
}
