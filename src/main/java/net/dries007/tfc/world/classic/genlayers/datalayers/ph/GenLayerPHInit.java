/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.datalayers.ph;

import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerPHInit extends GenLayerTFC
{
    public GenLayerPHInit(long par1)
    {
        super(par1);
    }

    @Override
    public int[] getInts(int par1, int par2, int maxX, int maxZ)
    {
        int[] outCache = IntCache.getIntCache(maxX * maxZ);

        for (int z = 0; z < maxZ; ++z)
        {
            for (int x = 0; x < maxX; ++x)
            {
                this.initChunkSeed(par1 + x, par2 + z);
                int out = GenPHLayer.MIN + this.nextInt(4);
                outCache[x + z * maxX] = out;
            }
        }

        return outCache;
    }
}
