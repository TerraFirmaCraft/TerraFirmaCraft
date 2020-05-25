/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.biome;

import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerDeepOcean extends GenLayerTFC
{
    public GenLayerDeepOcean(long seed, GenLayerTFC parent)
    {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int parX, int parZ, int parXSize, int parZSize)
    {
        int xSize = parXSize + 2;
        int zSize = parZSize + 2;
        int thisID;
        int[] parentIDs = this.parent.getInts(parX - 1, parZ - 1, xSize, zSize);
        int[] outCache = IntCache.getIntCache(parXSize * parZSize);

        for (int z = 0; z < parZSize; ++z)
        {
            for (int x = 0; x < parXSize; ++x)
            {
                int upID = parentIDs[x + 1 + z * xSize];
                int rightID = parentIDs[x + 2 + (z + 1) * xSize];
                int leftID = parentIDs[x + (z + 1) * xSize];
                int southID = parentIDs[x + 1 + (z + 2) * xSize];
                thisID = parentIDs[x + 1 + (z + 1) * xSize];
                int oceanCount = 0;
                int outIndex = x + z * parXSize;

                if (upID == oceanID) ++oceanCount;
                if (rightID == oceanID) ++oceanCount;
                if (leftID == oceanID) ++oceanCount;
                if (southID == oceanID) ++oceanCount;

                if (thisID == oceanID && oceanCount > 3)
                {
                    outCache[outIndex] = deepOceanID;
                }
                else
                {
                    outCache[outIndex] = thisID;
                }
            }
        }

        return outCache;
    }
}
