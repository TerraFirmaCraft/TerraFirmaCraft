/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.datalayers.stability;

import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.world.classic.DataLayer;
import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerStabilityInit extends GenLayerTFC
{
    public GenLayerStabilityInit(long par1)
    {
        super(par1);
    }

    @Override
    public int[] getInts(int par1, int par2, int maxX, int maxZ)
    {
        int[] cache = IntCache.getIntCache(maxX * maxZ);

        for (int z = 0; z < maxZ; ++z)
        {
            for (int x = 0; x < maxX; ++x)
            {
                this.initChunkSeed(par1 + x, par2 + z);
                cache[x + z * maxX] = this.nextInt(3) == 0 ? DataLayer.SEISMIC_UNSTABLE.layerID : DataLayer.SEISMIC_STABLE.layerID;
            }
        }

        return cache;
    }
}
