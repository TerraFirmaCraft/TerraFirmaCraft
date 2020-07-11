package net.dries007.tfc.tests;

import java.awt.*;
import java.util.Random;
import java.util.stream.IntStream;

import net.dries007.tfc.ImageUtil;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.Metaballs2D;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class MetaballsTests
{
    static final ImageUtil<INoise2D> IMAGES = ImageUtil.noise(target -> (x, y) ->
            target.noise((float) x, (float) y),
        builder -> builder.color(value -> value > 0.5 ? Color.BLACK : Color.WHITE).size(40).dimensions(-20, -20, 20, 20)
    );
    static final ImageUtil<BiIntFunction<INoise2D>> TILED_IMAGES = ImageUtil.noise(target -> (x, y) ->
            target.get((int) (x / 40), (int) (y / 40)).noise((float) x % 40 - 20, (float) y % 40 - 20),
        builder -> builder.scale(ImageUtil.Scales.fixedRange(0, 1)).color(value -> value > 0.5 ? Color.BLACK : Color.WHITE).size(40 * 20).dimensions(40 * 20)
    );

    @Test
    void testMetaballs()
    {
        IMAGES.draw("metaballs", new Metaballs2D(20, new Random()));
    }

    @Test
    void testTiledMetaballs()
    {
        INoise2D[][] noises = IntStream.range(0, 20)
            .mapToObj(i -> IntStream.range(0, 20)
                .mapToObj(j -> new Metaballs2D(20, new Random()))
                .toArray(INoise2D[]::new))
            .toArray(INoise2D[][]::new);
        TILED_IMAGES.draw("metaballs_tiled", (i, j) -> noises[i][j]);
    }

    interface BiIntFunction<T>
    {
        T get(int x, int z);
    }
}
