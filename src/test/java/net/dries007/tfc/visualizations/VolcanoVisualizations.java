/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.visualizations;

import net.minecraft.util.Mth;

import net.dries007.tfc.Artist;
import net.dries007.tfc.TestHelper;
import net.dries007.tfc.world.biome.VolcanoNoise;
import net.dries007.tfc.world.noise.Cellular2D;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class VolcanoVisualizations extends TestHelper
{
    @Test
    public void testVolcanoNoise()
    {
        long seed = TestHelper.seed();
        VolcanoNoise vn = new VolcanoNoise(seed);
        Cellular2D cn = new Cellular2D(seed).spread(0.0035f);

        final Artist.Raw artist = Artist.raw().dimensions(1000).size(1000);

        artist.draw("volcanoes", Artist.Pixel.coerceFloat((x, z) -> {
            final Cellular2D.Cell cell = cn.cell(x, z);
            final Cellular2D.Cell centerCell = cn.cell(cell.x(), cell.y());
            assertEquals(cell.x(), centerCell.x(), 0.00001f);
            assertEquals(cell.y(), centerCell.y(), 0.00001f);
            final float easing = calculateEasing(cell.f1());
            if (easing > 0)
            {
                if (calculateEasing(centerCell.f2()) > -1.2f)
                {
                    return Artist.Colors.LINEAR_GREEN_YELLOW.apply(easing);
                }
                return Artist.Colors.LINEAR_BLUE_RED.apply(easing);
            }
            return Artist.Colors.RANDOM_NEAREST_INT.apply(20 * cell.noise());
        }));
    }

    private static float calculateEasing(float f1)
    {
        return Mth.map(f1, 0, 0.05f, 1, 0);
    }

    private static float calculateClampedEasing(float f1)
    {
        return Mth.clamp(calculateEasing(f1), 0, 1);
    }
}
