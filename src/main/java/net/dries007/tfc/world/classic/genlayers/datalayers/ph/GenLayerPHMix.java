/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.datalayers.ph;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerPHMix extends GenLayerTFC
{
    public GenLayerPHMix(long par1, GenLayer par3GenLayer)
    {
        super(par1);
        this.parent = par3GenLayer;
    }

    @Override
    public int[] getInts(int x, int z, int xSize, int zSize)
    {
        int[] var5 = this.parent.getInts(x - 1, z - 1, xSize + 2, zSize + 2);
        int[] outCache = IntCache.getIntCache(xSize * zSize);
        int thisID;
        int id0;
        int id1;
        int id2;
        int id3;
        int index;

        for (int var7 = 0; var7 < zSize; ++var7)
        {
            for (int var8 = 0; var8 < xSize; ++var8)
            {
                this.initChunkSeed(var8 + x, var7 + z);
                thisID = var5[var8 + 1 + (var7 + 1) * (xSize + 2)];
                id0 = var5[var8 + 1 + (var7 + 1 - 1) * (xSize + 2)];
                id1 = var5[var8 + 1 + 1 + (var7 + 1) * (xSize + 2)];
                id2 = var5[var8 + 1 - 1 + (var7 + 1) * (xSize + 2)];
                id3 = var5[var8 + 1 + (var7 + 1 + 1) * (xSize + 2)];
                index = var8 + var7 * xSize;

                if (id0 >= thisID + 2 || id1 >= thisID + 2 || id2 >= thisID + 2 || id3 >= thisID + 2)
                    if (thisID + 1 < GenPHLayer.MAX)
                        thisID++;
                if (id0 <= thisID - 2 || id1 <= thisID - 2 || id2 <= thisID - 2 || id3 <= thisID - 2)
                    if (thisID - 1 > GenPHLayer.MIN)
                        thisID--;

                outCache[index] = thisID;
            }
        }
        return outCache;
    }
}
