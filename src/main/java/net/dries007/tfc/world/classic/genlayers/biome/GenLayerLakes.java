/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.biome;

import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerLakes extends GenLayerTFC
{
    public GenLayerLakes(long seed, GenLayerTFC parent)
    {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int x, int z, int sizeX, int sizeZ)
    {
        int[] ints = this.parent.getInts(x - 1, z - 1, sizeX + 2, sizeZ + 2);
        int[] out = IntCache.getIntCache(sizeX * sizeZ);

        for (int zz = 0; zz < sizeZ; ++zz)
        {
            for (int xx = 0; xx < sizeX; ++xx)
            {
                this.initChunkSeed(xx + x, zz + z);
                int thisID = ints[xx + 1 + (zz + 1) * (sizeX + 2)];

                int zn = ints[xx + 1 + (zz + 1 - 1) * (sizeX + 2)]; // z-1
                int xp = ints[xx + 1 + 1 + (zz + 1) * (sizeX + 2)]; // x+1
                int xn = ints[xx + 1 - 1 + (zz + 1) * (sizeX + 2)]; // x-1
                int zp = ints[xx + 1 + (zz + 1 + 1) * (sizeX + 2)]; // z+1

                if (isOceanicBiome(thisID) && !isOceanicBiome(zn) && !isOceanicBiome(xp) && !isOceanicBiome(xn) && !isOceanicBiome(zp))
                {
                    out[xx + zz * sizeX] = lakeID;
                }
                else
                {
                    out[xx + zz * sizeX] = thisID;
                }
            }
        }
        return out;
    }
}
