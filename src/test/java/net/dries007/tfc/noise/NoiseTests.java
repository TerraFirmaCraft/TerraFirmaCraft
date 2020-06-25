/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.noise;

import java.awt.*;
import java.util.Random;
import java.util.stream.IntStream;

import net.dries007.tfc.config.NoiseLayerType;
import net.dries007.tfc.images.Colors;
import net.dries007.tfc.images.ImageUtil;
import net.dries007.tfc.images.Scales;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.Metaballs2D;

@SuppressWarnings("unused")
public class NoiseTests
{
    public static final ImageUtil<INoise2D> NOISE = ImageUtil.get(target -> (x, y) -> target.noise((float) x, (float) y));
    public static final ImageUtil<BiIntFunction<INoise2D>> TILED_NOISE = ImageUtil.get(target -> (x, y) -> target.get((int) (x / 40), (int) (y / 40)).noise((float) x % 40 - 20, (float) y % 40 - 20));

    public static void main()
    {
        long seed = System.currentTimeMillis();

        INoise2D temp = NoiseLayerType.PERIODIC_Z.create(seed, 20_000).scaled(-10, 30);
        INoise2D rainfall = NoiseLayerType.PERIODIC_X.create(seed, 20_000).terraces(4).scaled(-50, 500).flattened(0, 500);

        NOISE.color(Colors.LINEAR_BLUE_RED).size(1000).dimensions(20000, 20000).scale(Scales.DYNAMIC_RANGE);

        NOISE.draw("avg_temp", temp);
        NOISE.draw("rainfall", rainfall);
    }

    public static void testMetaballs()
    {
        Random random = new Random();
        INoise2D noise = new Metaballs2D(20, random);

        NOISE.color(value -> value > 0.5 ? Color.BLACK : Color.WHITE).size(40).dimensions(40);

        NOISE.draw("metaballs", noise);

        INoise2D[][] noises = IntStream.range(0, 20).mapToObj(i -> IntStream.range(0, 20).mapToObj(j -> new Metaballs2D(20, random)).toArray(INoise2D[]::new)).toArray(INoise2D[][]::new);

        NOISE.size(40 * 20).dimensions(40 * 20).scale(Scales.fixedRange(0, 1));
        TILED_NOISE.draw("metaballs_tiled", (i, j) -> noises[i][j]);
    }

    interface BiIntFunction<T>
    {
        T get(int x, int z);
    }
}
