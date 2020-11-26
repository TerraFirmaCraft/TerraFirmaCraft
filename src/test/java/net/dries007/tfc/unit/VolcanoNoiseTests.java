package net.dries007.tfc.unit;

import net.dries007.tfc.Artist;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.CellularNoiseType;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import org.junit.jupiter.api.Test;

public class VolcanoNoiseTests
{
    static final Artist.Noise<INoise2D> NOISE = Artist.<INoise2D>forNoise(target -> Artist.NoisePixel.coerceFloat(target::noise)).scale(Artist.Scales.DYNAMIC_RANGE).color(Artist.Colors.LINEAR_GRAY);

    @Test
    public void testVolcanoNoise()
    {
        // Assume a static volcano at 0,0
        // Generate a height map for it

        long seed = 12342352361263L;

        NOISE.center(4);

        final float c1 = 9;
        final float c2 = 0.05f;
        final float d1 = -5 / (c1 + 1);
        final float d2 = d1 + 5 / (c2 + 1) - 90 * (c2 + 0.05f) * (c2 + 0.05f);

        INoise2D originNoise = new OpenSimplex2D(seed).octaves(4).scaled(-0.3f, 0.3f).spread(3f);
        INoise2D originDomainNoise = new OpenSimplex2D(seed + 1).octaves(2).scaled(-0.08f, 0.08f).spread(7f);
        INoise2D originVolcano = (x, z) -> {
            final float q = x * x + z * z + originDomainNoise.noise(x, z);
            if (q > c1)
            {
                return 0;
            }
            else if (q > c2)
            {
                return 5 / (q + 1) + d1;
            }
            else
            {
                return 90 * (q + 0.05f) * (q + 0.05f) + d2;
            }
        };

        NOISE.draw("volcano_1", originNoise);
        NOISE.draw("volcano_2", originVolcano);
        NOISE.draw("volcano_3", originVolcano.add(originNoise));

        INoise2D cellNoise = new Cellular2D(seed + 1, 1.0f, CellularNoiseType.DISTANCE);

        NOISE.draw("volcano_4", cellNoise);

        final float c3 = 7;
        INoise2D spreadCellNoise = cellNoise.spread(1 / c3).scaled(-c3 * c3, c3 * c3);

        NOISE.center(20).draw("volcano_5", spreadCellNoise);
        NOISE.draw("volcano_6", spreadCellNoise.map(x -> x > c1 ? 0 : 1));

        INoise2D warpedCellNoise = spreadCellNoise.add(originDomainNoise);

        NOISE.draw("volcano_7", warpedCellNoise);
        NOISE.draw("volcano_8", warpedCellNoise.map(x -> x > c1 ? 0 : 1));

        INoise2D volcanoNoise = warpedCellNoise.map(q -> {
            if (q > c1)
            {
                return 0;
            }
            else if (q > c2)
            {
                return 5 / (q + 1) + d1;
            }
            else
            {
                return 80 * (q + 0.05f) * (q + 0.05f) + d2;
            }
        });

        NOISE.draw("volcano_9", volcanoNoise);
    }
}
