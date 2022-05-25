/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.visualizations;

import java.awt.*;
import java.util.function.IntFunction;

import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import net.dries007.tfc.Artist;
import net.dries007.tfc.TestHelper;
import net.dries007.tfc.world.noise.Cellular3D;
import net.dries007.tfc.world.noise.Noise3D;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class AquiferVisualizations extends TestHelper
{
    @BeforeAll
    public static void setup()
    {
        bootstrap();
    }

    @Test
    public void testAquiferNoise()
    {
        final long seed = seed();
        final Cellular3D aquiferCellNoise = new Cellular3D(seed).spread(0.015f);

        final Artist.Noise<Noise3D> xyPlane = Artist.<Noise3D>forNoise(noise -> Artist.NoisePixel.coerceFloat((x, y) -> noise.noise(x, y, 0))).dimensions(0, -64, 1000, 256 - 64).size(1000, 256);
        final IntFunction<Artist.Noise<Noise3D>> atFixedY = y -> Artist.<Noise3D>forNoise(noise -> Artist.NoisePixel.coerceFloat((x, z) -> noise.noise(x, y, z))).dimensionsSized(1000, 1000);

        xyPlane.color(Artist.Colors.LINEAR_BLUE_RED).draw("cell_xy", aquiferCellNoise);
        atFixedY.apply(0).color(Artist.Colors.LINEAR_BLUE_RED).draw("cell_y0", aquiferCellNoise);

        Fxyz<Color> aquifer = (x, y, z) -> {
            y = 256 - y - 64;
            if (y == 63) return new Color(0, 200, 0);
            if (y < -56) return new Color(250, 0, 0);
            final Cellular3D.Cell cell = aquiferCellNoise.cell(x, y / 0.6f, z);
            final float cellValue = aquiferCellNoise.noise(x, y / 0.6f, z);
            if (cellValue < 0.25f) return new Color(150, 150, 150);
            final float cellY = cell.y();
            final RandomSource random = new XoroshiroRandomSource(seed, Float.floatToIntBits(cellValue));
            final float aquiferY = (random.nextFloat() - random.nextFloat()) * 5 + cellY;
            final boolean lava = cellY < 40 && (random.nextInt(3) == 0);
            final int s = (int) Mth.clamp((cellY + 64) * 250 / 256, 0, 250);
            if (y >= aquiferY) return new Color(200, 200, 200);
            if (y < aquiferY) return lava ?
                new Color(250, s, 50) :
                new Color(s, s, 250);
            return Color.BLACK;
        };

        Artist.raw().dimensionsSized(1000, 1000).draw("aquifers_xz_y0", Artist.Pixel.coerceInt((x, z) -> aquifer.apply(x, 0, z)));
        Artist.raw().dimensionsSized(1000, 256).draw("aquifers_xy", Artist.Pixel.coerceInt((x, z) -> aquifer.apply(x, z, 0)));
    }

    interface Fxyz<T> { T apply(int x, int y, int z); }
}
