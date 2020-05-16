package net.dries007.tfc.world.noise;

import java.awt.*;
import java.util.Random;
import java.util.stream.IntStream;

import imageutil.Images;
import net.dries007.tfc.config.LayerType;

public class NoiseTests
{
    public static final Images<INoise2D> IMAGES = Images.get(target -> (x, y) -> target.noise((float) x, (float) y));
    public static final Images<BiIntFunction<INoise2D>> TILED_IMAGES = Images.get(target -> (x, y) -> target.get((int) (x / 40), (int) (y / 40)).noise((float) x % 40 - 20, (float) y % 40 - 20));

    public static void main(String[] args)
    {
        long seed = System.currentTimeMillis();

        INoise2D temp = LayerType.SIN_Z.create(seed, 40_000);
        INoise2D rainfall = LayerType.NOISE.create(seed, 40_000);

        IMAGES.color(Images.Colors.LINEAR_BLUE_RED).size(1000);
        IMAGES.draw("avg_temp", temp.terraces(9), -1, 1, -20000, -20000, 20000, 20000);

        IMAGES.color(Images.Colors.LINEAR_BLUE_RED).size(1000);
        IMAGES.draw("rainfall", rainfall.terraces(9), -1, 1, -20000, -20000, 20000, 20000);
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
