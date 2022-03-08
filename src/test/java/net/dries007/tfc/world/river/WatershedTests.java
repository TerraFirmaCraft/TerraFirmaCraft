/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.river;

import net.dries007.tfc.TestBase;
import net.dries007.tfc.world.layer.Plate;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.layer.framework.TypedArea;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static net.dries007.tfc.TestHelper.seed;
import static org.junit.jupiter.api.Assertions.*;

public class WatershedTests extends TestBase
{
    @Test
    public void testBasicWatershedProperties()
    {
        final long seed = seed();
        final TypedArea<Plate> plates = TFCLayers.createEarlyPlateLayers(seed).get();

        for (int x = -40; x <= 40; x++)
        {
            for (int z = -40; z <= 40; z++)
            {
                Watershed shed = Watershed.create(plates, x, z, seed, 0, 0, 0, 0);
                final Plate plate = shed.getPlate();

                assertEquals(plate, plates.get(x, z));

                if (shed instanceof Watershed.Rivers rv)
                {
                    assertFalse(plate.oceanic());
                    assertFalse(rv.getSources().isEmpty());
                    assertFalse(rv.getSources().contains(RiverHelpers.pack(x, z)));
                }
            }
        }
    }

    @RepeatedTest(10)
    public void testWatershedsIndependentOfSamplePos()
    {
        TypedArea<Plate> plates = null;
        long seed = 0;
        Watershed.Rivers rivers = null;
        for (int i = 0; i < 100; i++)
        {
            seed = seed();
            plates = TFCLayers.createEarlyPlateLayers(seed).get();
            Watershed origin = Watershed.create(plates, 0, 0, seed, 1, 0.8f, 10, 0.1f);
            if (origin instanceof Watershed.Rivers rv)
            {
                rivers = rv;
                break;
            }
        }

        assertNotNull(rivers, "No watershed with rivers found?");

        for (int x = -10; x <= 10; x++)
        {
            for (int z = -10; z <= 10; z++)
            {
                final Watershed offset = Watershed.create(plates, x, z, seed, 1, 0.8f, 10, 0.1f);
                if (offset.getPlate().equals(rivers.getPlate()))
                {
                    // Watersheds belong to the same plate. We need to ensure they (would) generate identically
                    // What this practically means, is that the iteration order of their sources are identical
                    assertTrue(offset instanceof Watershed.Rivers);

                    Watershed.Rivers other = (Watershed.Rivers) offset;
                    assertIterableEquals(rivers.getRivers(), other.getRivers());
                }
            }
        }
    }
}
