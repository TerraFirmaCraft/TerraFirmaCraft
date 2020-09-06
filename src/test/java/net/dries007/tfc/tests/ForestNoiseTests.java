package net.dries007.tfc.tests;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.dries007.tfc.ImageUtil;
import net.dries007.tfc.config.NoiseLayerType;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;
import org.junit.jupiter.api.Test;

public class ForestNoiseTests
{
    static final ImageUtil<INoise2D> IMAGES = ImageUtil.noise(target -> (x, y) -> target.noise((float) x, (float) y), builder -> builder.scale(ImageUtil.Scales.DYNAMIC_RANGE).color(ImageUtil.Colors.LINEAR_BLUE_RED).size(1000).dimensions(-10_000, -10_000, 10_000, 10_000));

    static final ImageUtil<ImageUtil.Double2ObjectBiFunction<Color>> COLOR = ImageUtil.colored(v -> v, builder -> builder.size(1000).dimensions(-10_000, -10_000, 10_000, 10_000));

    @Test
    void testForestBaseNoise()
    {
        long seed = System.currentTimeMillis();
        INoise2D forestBase = new SimplexNoise2D(seed).octaves(4).spread(0.001f).abs();
        INoise2D forestWeirdness = new SimplexNoise2D(seed + 1).octaves(4).spread(0.0015f);
        INoise2D forestDensity = new SimplexNoise2D(seed + 2).octaves(4).spread(0.0015f);

        INoise2D temperature = NoiseLayerType.PERIODIC_Z.create(seed + 3, 20_000).scaled(-10, 30);
        INoise2D rainfall = NoiseLayerType.NOISE.create(seed + 4, 20_000).scaled(-50, 550).flattened(0, 500);

        List<TreeSpawn> trees = Stream.of(
            new TreeSpawn(30f, 210f, 19f, 31f),
            new TreeSpawn(60f, 140f, -6f, 12f),
            new TreeSpawn(10f, 80f, -10f, 16f),
            new TreeSpawn(20f, 180f, -15f, 7f),
            new TreeSpawn(0f, 120f, 4f, 33f),
            new TreeSpawn(160f, 320f, 11f, 35f),
            new TreeSpawn(280f, 480f, -2f, 14f),
            new TreeSpawn(80f, 250f, 7f, 29f),
            new TreeSpawn(210f, 500f, 15f, 35f),
            new TreeSpawn(140f, 360f, 3f, 20f),
            new TreeSpawn(180f, 430f, -8f, 12f),
            new TreeSpawn(280f, 500f, 16f, 35f),
            new TreeSpawn(60f, 250f, -15f, 7f),
            new TreeSpawn(10f, 190f, 8f, 18f),
            new TreeSpawn(250f, 420f, -5f, 12f),
            new TreeSpawn(120f, 380f, -11f, 6f),
            new TreeSpawn(120f, 290f, 17f, 33f),
            new TreeSpawn(10f, 240f, -8f, 17f),
            new TreeSpawn(230f, 400f, 15f, 32f)
        ).collect(Collectors.toList());

        Color darkGreen = new Color(0, 100, 0);
        Color midGreen = new Color(0, 160, 0);
        Color midGreenGray = new Color(120, 150, 120);
        Color gray = new Color(140, 140, 140);

        COLOR.draw("forest_base", (x, z) -> {
            float value = forestBase.noise((float) x, (float) z);
            if (value > 0.5) return darkGreen;
            if (value > 0.18) return midGreen;
            if (value > 0.06) return midGreenGray;
            return gray;
        });

        IMAGES.draw("forest_weirdness", forestWeirdness);
        IMAGES.draw("forest_density", forestDensity);

        IMAGES.draw("forest_temperature", temperature);
        IMAGES.draw("forest_rainfall", rainfall);

        COLOR.draw("forest_top_tree", (x, z) -> {
            float temp = temperature.noise((float) x, (float) z);
            float rain = rainfall.noise((float) x, (float) z);

            for (TreeSpawn t : trees)
            {
                if (t.minRain < rain && t.maxRain > rain && t.minTemp < temp && t.maxTemp > temp)
                {
                    return t.color;
                }
            }
            return Color.BLACK;
        });

        COLOR.draw("forest_all_trees", (x, z) -> {
            float temp = temperature.noise((float) x, (float) z);
            float rain = rainfall.noise((float) x, (float) z);

            List<TreeSpawn> possibleTrees = new ArrayList<>();
            for (TreeSpawn t : trees)
            {
                if (t.minRain < rain && t.maxRain > rain && t.minTemp < temp && t.maxTemp > temp)
                {
                    possibleTrees.add(t);
                    if (possibleTrees.size() >= 3)
                    {
                        break;
                    }
                }
            }

            float weird = forestWeirdness.noise((float) x, (float) z);
            Collections.rotate(trees, -(int) (weird * (trees.size() - 1f)));
            TreeSpawn t = getTree(possibleTrees);
            if (t != null)
            {
                return t.color;
            }
            return Color.BLACK;
        });
    }

    TreeSpawn getTree(List<TreeSpawn> list)
    {
        if (list.isEmpty())
        {
            return null;
        }
        int index = 0;
        while (index < list.size() - 1 && TreeSpawn.random.nextFloat() < 0.5)
        {
            index++;
        }
        return list.get(index);
    }

    static class TreeSpawn
    {
        static final Random random = new Random();

        final float minRain;
        final float maxRain;
        final float minTemp;
        final float maxTemp;
        final Color color;

        TreeSpawn(float minRain, float maxRain, float minTemp, float maxTemp)
        {
            this.minRain = minRain;
            this.maxRain = maxRain;
            this.minTemp = minTemp;
            this.maxTemp = maxTemp;
            this.color = new Color(100 + random.nextInt(155), 100 + random.nextInt(155), 100 + random.nextInt(155));
        }
    }
}
