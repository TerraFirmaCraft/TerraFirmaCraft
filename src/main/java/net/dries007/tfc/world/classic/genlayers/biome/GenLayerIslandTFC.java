/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.biome;

import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerIslandTFC extends GenLayerTFC
{
    public GenLayerIslandTFC(long par1)
    {
        super(par1);
    }

    @Override
    public int[] getInts(int x, int z, int sizeX, int sizeZ)
    {
        int[] var5 = IntCache.getIntCache(sizeX * sizeZ);

        for (int zz = 0; zz < sizeZ; ++zz)
        {
            for (int xx = 0; xx < sizeX; ++xx)
            {
                this.initChunkSeed(x + xx, z + zz);
                var5[xx + zz * sizeX] = this.nextInt(4) == 0 ? plainsID : oceanID;
            }
        }

        if (x > -sizeX && x <= 0 && z > -sizeZ && z <= 0)
            var5[-x + -z * sizeX] = plainsID;

        return var5;
    }
}
