/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.noise;

import java.awt.*;
import java.util.Random;
import java.util.stream.IntStream;

import imageutil.Images;
import net.dries007.tfc.config.NoiseLayerType;
import net.dries007.tfc.world.biome.BiomeTemperature;

@SuppressWarnings("unused")
public class NoiseTests
{
    public static final Images<INoise2D> IMAGES = Images.get(target -> (x, y) -> target.noise((float) x, (float) y));
    public static final Images<BiIntFunction<INoise2D>> TILED_IMAGES = Images.get(target -> (x, y) -> target.get((int) (x / 40), (int) (y / 40)).noise((float) x % 40 - 20, (float) y % 40 - 20));

    public static final Color[] TEMPERATURE_COLORS = new Color[] {
        new Color(0, 0, 100),
        new Color(0, 40, 200),
        new Color(20, 100, 255),
        new Color(50, 160, 255),
        new Color(80, 255, 255)
    };

    public static void main(String[] args)
    {
        long seed = System.currentTimeMillis();

        INoise2D temp = NoiseLayerType.PERIODIC_Z.create(seed, 20_000).scaled(-10, 30);
        INoise2D rainfall = NoiseLayerType.PERIODIC_X.create(seed, 20_000).terraces(4).scaled(-50, 500).flattened(0, 500);

        IMAGES.color((value, min, max) -> TEMPERATURE_COLORS[BiomeTemperature.get((float) value).ordinal()]);
        IMAGES.draw("avg_temp", temp, -10, 30, -20000, -20000, 20000, 20000);

        IMAGES.color(Images.Colors.LINEAR_BLUE_RED).size(1000);
        IMAGES.draw("rainfall", rainfall, 0, 500, -20000, -20000, 20000, 20000);
    }

    public static void testMetaballs()
    {
        Random random = new Random();
        INoise2D noise = new Metaballs2D(20, random);

        IMAGES.color((value, min, max) -> value > 0.5 ? Color.BLACK : Color.WHITE).size(40);

        IMAGES.draw("metaballs", noise, 0, 1, -20, -20, 20, 20);

        INoise2D[][] noises = IntStream.range(0, 20).mapToObj(i -> IntStream.range(0, 20).mapToObj(j -> new Metaballs2D(20, random)).toArray(INoise2D[]::new)).toArray(INoise2D[][]::new);

        IMAGES.color((value, min, max) -> value > 0.5 ? Color.BLACK : Color.WHITE).size(40 * 20);
        TILED_IMAGES.draw("metaballs_tiled", (i, j) -> noises[i][j], 0, 1, 0, 0, 40 * 20, 40 * 20);
    }

    interface BiIntFunction<T>
    {
        T get(int x, int z);
    }
}
