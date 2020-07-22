/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.minecraft.util.math.MathHelper;

public class TestBiomeFactory implements IBiomeFactory
{
    private final int zoom;

    public TestBiomeFactory(int zoom)
    {
        this.zoom = zoom;
    }

    @Override
    public TFCBiome getBiome(int x, int z)
    {
        x >>= zoom;
        z >>= zoom;
        x += MathHelper.sin(z * 0.025f) * 12;
        if (x < 0)
        {
            return TFCBiomes.HILLS.get();
        }
        else if (x < 2)
        {
            return TFCBiomes.HILLS_LARGE_EDGE.get();
        }
        else if (x < 4)
        {
            return TFCBiomes.SHORE_LAND_LARGE_EDGE.get();
        }
        else if (x < 6)
        {
            return TFCBiomes.SHORE.get();
        }
        else if (x < 14)
        {
            return TFCBiomes.SHORE_OCEAN_LARGE_EDGE.get();
        }
        else if (x < 22)
        {
            return TFCBiomes.OCEAN_LARGE_EDGE.get();
        }
        else
        {
            return TFCBiomes.OCEAN.get();
        }
    }
}
