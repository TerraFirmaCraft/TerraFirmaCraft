package net.dries007.tfc.world.layer;

import java.awt.*;

import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;

import imageutil.Images;
import net.dries007.tfc.api.Rock;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

public class LayerDrawingUtil
{
    public static final Images<IAreaFactory<LazyArea>> IMAGES = Images.get(af -> {
        IArea area = af.make();
        return (x, y) -> area.getValue((int) x, (int) y);
    });

    static
    {
        IMAGES.size(1000).color(Images.Colors.LINEAR_GRAY).disable();
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
        if (biome == RIVER) return new Color(0, 200, 255);
        return Color.BLACK;
    }

    public static Color landColor(double val, double min, double max)
    {
        int i = (int) Math.round(val);
        if (i == PLAINS) return Color.GREEN;
        if (i == DEEP_OCEAN) return Color.BLUE;
        return Color.BLACK;
    }

    public static Color riverColor(double val, double min, double max)
    {
        int i = (int) Math.round(val);
        if (i == RIVER) return Color.CYAN;
        return Color.BLACK;
    }

    public static Color elevationColor(double val, double min, double max)
    {
        int i = (int) Math.round(val);
        if (i == PLAINS) return new Color(100, 200, 100);
        if (i == HILLS) return new Color(255, 200, 0);
        if (i == MOUNTAINS) return new Color(255, 100, 0);
        if (i == DEEP_OCEAN) return Color.BLUE;
        return Color.BLACK;
    }

    public static Color rockCategoryColor(double val, double min, double max)
    {
        int i = (int) Math.round(val);
        if (i == 0) return Color.BLUE;
        if (i == 1) return Color.CYAN;
        if (i == 2) return Color.RED;
        if (i == 3) return Color.ORANGE;
        return Color.BLACK;
    }

    public static Color rockColor(double val, double min, double max)
    {
        return Images.Colors.DISCRETE_20.apply(val, 0, Rock.Default.values().length);
    }

    public static String biomeName(int biome)
    {
        if (biome == OCEAN) return "OCEAN";
        if (biome == DEEP_OCEAN) return "DEEP_OCEAN";
        if (biome == DEEP_OCEAN_RIDGE) return "DEEP_OCEAN_RIDGE";

        if (biome == PLAINS) return "PLAINS";
        if (biome == HILLS) return "HILLS";
        if (biome == LOWLANDS) return "LOWLANDS";
        if (biome == LOW_CANYONS) return "LOW_CANYONS";

        if (biome == ROLLING_HILLS) return "ROLLING_HILLS";
        if (biome == BADLANDS) return "BADLANDS";
        if (biome == PLATEAU) return "PLATEAU";
        if (biome == OLD_MOUNTAINS) return "OLD_MOUNTAINS";

        if (biome == MOUNTAINS) return "MOUNTAINS";
        if (biome == FLOODED_MOUNTAINS) return "FLOODED_MOUNTAINS";
        if (biome == CANYONS) return "CANYONS";

        if (biome == SHORE) return "SHORE";
        if (biome == STONE_SHORE) return "STONE_SHORE";

        if (biome == MOUNTAINS_EDGE) return "MOUNTAINS_EDGE";
        if (biome == LAKE) return "LAKE";
        return "UNKNOWN";
    }
}
