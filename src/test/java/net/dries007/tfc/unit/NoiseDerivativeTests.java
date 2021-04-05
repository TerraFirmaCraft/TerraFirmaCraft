/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.unit;

import java.awt.*;

import net.dries007.tfc.Artist;
import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.noise.INoise1D;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.NoiseUtil;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import org.junit.jupiter.api.Test;

public class NoiseDerivativeTests
{
    boolean inside(float x, float exp, float dL)
    {
        return x < exp + dL && x > exp - dL;
    }

    @Test
    public void test2DNoiseDerivatives()
    {
        final long seed = 273492831L;
        final INoise2D simplex = new OpenSimplex2D(seed).octaves(4);

        float p = 0.8f;
        int n = 5;
        int size = 1000;
        float dL = 3 * (2 * p) / ((float) size / n);
        Artist.Raw artist = Artist.raw().dimensions(0, 0, 40, n).size(size);

        artist.draw("simplex", Artist.Pixel.coerceFloat((x, y) -> {
            if (y < 1)
            {
                float pixel = NoiseUtil.lerp(-p, p, 1 - y);
                float height = simplex.noise(x, 0);
                return pixel < height ? Color.BLACK : Color.WHITE;
            }
            else if (y < 2)
            {
                float pixel = NoiseUtil.lerp(-p, p, 2 - y);
                float height = simplex.noise(x, 0);
                float surface = height + 0.08f;
                if (pixel < height) return Color.BLACK;
                if (pixel < surface) return Color.GRAY;
                return Color.WHITE;
            }
            else if (y < 3)
            {
                float derDelta = 0.5f;
                float pixel = NoiseUtil.lerp(-1, 1, 3 - y);
                float left = simplex.noise(x - derDelta, 0);
                float right = simplex.noise(x + derDelta, 0);
                float der = 1.6f * (right - left) / (2 * derDelta);
                if (inside(pixel, 0, dL)) return Color.RED;
                else if (inside(pixel, der, dL)) return Color.BLACK;
                else return Color.WHITE;
            }
            else if (y < 4)
            {
                float derDelta = 0.5f;
                float left = simplex.noise(x - derDelta, 0);
                float right = simplex.noise(x + derDelta, 0);
                float der = (right - left) / (2 * derDelta);

                float pixel = NoiseUtil.lerp(-p, p, 4 - y);
                float height = simplex.noise(x, 0);
                float surface = height + NoiseUtil.lerp(0.08f, 0, 2.3f * Math.abs(der));
                if (pixel < height) return Color.BLACK;
                if (pixel < surface) return Color.GRAY;
                return Color.WHITE;
            }
            else if (y < 5)
            {
                float derDelta = 0.5f;
                float left = simplex.noise(x - derDelta, 0);
                float right = simplex.noise(x + derDelta, 0);
                float der = (right - left) / (2 * derDelta);

                float pixel = NoiseUtil.lerp(-p, p, 5 - y);
                float height = simplex.noise(x, 0);
                float derAdjSurface = NoiseUtil.lerp(0.08f, 0, 2.9f * Math.abs(der));
                float surface = height + derAdjSurface - 0.12f * height;
                if (pixel < height) return Color.BLACK;
                if (pixel < surface) return Color.GRAY;
                return Color.WHITE;
            }
            return Color.PINK;
        }));
    }
}
