/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.datalayers.ph;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerAddPH extends GenLayerTFC
{
    public GenLayerAddPH(long par1, GenLayer par3GenLayer)
    {
        super(par1);
        this.parent = par3GenLayer;
    }

    @Override
    public int[] getInts(int xCoord, int zCoord, int xSize, int zSize)
    {
        int var5 = xCoord - 1;
        int var6 = zCoord - 1;
        int var7 = xSize + 2;
        int var8 = zSize + 2;
        int[] inCache = this.parent.getInts(var5, var6, var7, var8);
        int[] outCache = IntCache.getIntCache(xSize * zSize);

        for (int var11 = 0; var11 < zSize; ++var11)
        {
            for (int var12 = 0; var12 < xSize; ++var12)
            {
                int id0 = inCache[var12 + 0 + (var11 + 0) * var7];
                int id1 = inCache[var12 + 2 + (var11 + 0) * var7];
                int id2 = inCache[var12 + 0 + (var11 + 2) * var7];
                int id3 = inCache[var12 + 2 + (var11 + 2) * var7];
                int thisID = inCache[var12 + 1 + (var11 + 1) * var7];
                this.initChunkSeed(var12 + xCoord, var11 + zCoord);

                if (id0 > thisID || id1 > thisID || id2 > thisID || id3 > thisID)
                {
                    int count = 1;
                    int outID = thisID;

                    if (id0 < GenPHLayer.MAX && this.nextInt(count++) == 0)
                        outID = id0 + 1;

                    if (id1 < GenPHLayer.MAX && this.nextInt(count++) == 0)
                        outID = id1 + 1;

                    if (id2 < GenPHLayer.MAX && this.nextInt(count++) == 0)
                        outID = id2 + 1;

                    if (id3 < GenPHLayer.MAX && this.nextInt(count++) == 0)
                        outID = id3 + 1;

                    if (this.nextInt(3) == 0 && outID <= GenPHLayer.MAX)
                        outCache[var12 + var11 * xSize] = outID;
                    else
                        outCache[var12 + var11 * xSize] = thisID;
                }
                else if (id0 < thisID || id1 < thisID || id2 < thisID || id3 < thisID)
                {
                    int count = 1;
                    int outID = thisID;

                    if (id0 > GenPHLayer.MIN && this.nextInt(count++) == 0)
                        outID = id0 - 1;

                    if (id1 > GenPHLayer.MIN && this.nextInt(count++) == 0)
                        outID = id1 - 1;

                    if (id2 > GenPHLayer.MIN && this.nextInt(count++) == 0)
                        outID = id2 - 1;

                    if (id3 > GenPHLayer.MIN && this.nextInt(count++) == 0)
                        outID = id3 - 1;

                    if (this.nextInt(3) == 0 && outID >= GenPHLayer.MIN)
                        outCache[var12 + var11 * xSize] = outID;
                    else
                        outCache[var12 + var11 * xSize] = thisID;
                }
                else
                {
                    outCache[var12 + var11 * xSize] = thisID;
                }
            }
        }
        return outCache;
    }
}
