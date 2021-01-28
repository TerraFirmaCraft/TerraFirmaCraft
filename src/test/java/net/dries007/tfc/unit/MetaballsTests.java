/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.unit;

import java.awt.*;
import java.util.Random;
import java.util.stream.IntStream;

import net.dries007.tfc.Artist;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.Metaballs2D;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class MetaballsTests
{
    static final Artist.Noise<INoise2D> IMAGES = Artist.<INoise2D>forNoise(target -> (x, y) -> target.noise((float) x, (float) y)).color(value -> value > 0.5 ? Color.BLACK : Color.WHITE).size(40).center(20);
    static final Artist.Noise<BiIntFunction<INoise2D>> TILED_IMAGES = Artist.<BiIntFunction<INoise2D>>forNoise(target -> (x, y) -> target.get((int) (x / 40), (int) (y / 40)).noise((float) x % 40 - 20, (float) y % 40 - 20)).scale(Artist.Scales.fixedRange(0, 1)).color(value -> value > 0.5 ? Color.BLACK : Color.WHITE).size(40 * 20).dimensions(40 * 20);

    @Test
    public void testMetaballs()
    {
        IMAGES.draw("metaballs", new Metaballs2D(20, new Random()));
    }

    @Test
    public void testTiledMetaballs()
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
