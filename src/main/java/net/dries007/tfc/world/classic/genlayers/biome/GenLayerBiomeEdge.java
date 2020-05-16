/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.biome;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerBiomeEdge extends GenLayerTFC
{
    public GenLayerBiomeEdge(long seed, GenLayer parent)
    {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int x, int z, int xSize, int zSize)
    {
        int[] inCache = this.parent.getInts(x - 1, z - 1, xSize + 2, zSize + 2);
        int[] outCache = IntCache.getIntCache(xSize * zSize);

        for (int zz = 0; zz < zSize; ++zz)
        {
            for (int xx = 0; xx < xSize; ++xx)
            {
                this.initChunkSeed(xx + x, zz + z);
                int thisID = inCache[xx + 1 + (zz + 1) * (xSize + 2)];

                int zn = inCache[xx + 1 + (zz + 1 - 1) * (xSize + 2)]; // z - 1
                int xp = inCache[xx + 1 + 1 + (zz + 1) * (xSize + 2)]; // x + 1
                int xn = inCache[xx + 1 - 1 + (zz + 1) * (xSize + 2)]; // x - 1
                int zp = inCache[xx + 1 + (zz + 1 + 1) * (xSize + 2)]; // z + 1

                if (thisID == highHillsID)
                {
                    if (zn == highHillsID && xp == highHillsID && xn == highHillsID && zp == highHillsID)
                        outCache[xx + zz * xSize] = thisID;
                    else
                        outCache[xx + zz * xSize] = highHillsEdgeID;
                }
                else if (thisID == mountainsID)
                {
                    if (zn == mountainsID && xp == mountainsID && xn == mountainsID && zp == mountainsID)
                        outCache[xx + zz * xSize] = thisID;
                    else
                        outCache[xx + zz * xSize] = mountainsEdgeID;
                }
                else if (thisID == swamplandID)
                {
                    if (zn == swamplandID && xp == swamplandID && xn == swamplandID && zp == swamplandID)
                        outCache[xx + zz * xSize] = thisID;
                    else
                        outCache[xx + zz * xSize] = plainsID;
                }
                else if (thisID == highPlainsID)
                {
                    if (zn == highPlainsID && xp == highPlainsID && xn == highPlainsID && zp == highPlainsID)
                        outCache[xx + zz * xSize] = thisID;
                    else
                        outCache[xx + zz * xSize] = plainsID;
                }
                else
                {
                    outCache[xx + zz * xSize] = thisID;
                }
            }
        }
        return outCache;
    }
}
