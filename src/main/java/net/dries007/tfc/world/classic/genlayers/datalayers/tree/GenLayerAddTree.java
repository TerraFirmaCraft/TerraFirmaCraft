/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.datalayers.tree;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerAddTree extends GenLayerTFC
{
    public GenLayerAddTree(long par1, GenLayer par3GenLayer)
    {
        super(par1);
        this.parent = par3GenLayer;
    }

    @Override
    public int[] getInts(int par1, int par2, int xMax, int zMax)
    {
        int var5 = par1 - 1;
        int var6 = par2 - 1;
        int var7 = xMax + 2;
        int var8 = zMax + 2;
        int[] var9 = this.parent.getInts(var5, var6, var7, var8);
        int[] outCache = IntCache.getIntCache(xMax * zMax);

        for (int z = 0; z < zMax; ++z)
        {
            for (int x = 0; x < xMax; ++x)
            {
                int var13 = var9[x + 0 + (z + 0) * var7];
                int var14 = var9[x + 2 + (z + 0) * var7];
                int var15 = var9[x + 0 + (z + 2) * var7];
                int var16 = var9[x + 2 + (z + 2) * var7];
                int thisID = var9[x + 1 + (z + 1) * var7];
                this.initChunkSeed(x + par1, z + par2);


                int var18 = 1;
                int outID = thisID;

                if (this.nextInt(var18++) == 0)
                    outID = var13;

                if (this.nextInt(var18++) == 0)
                    outID = var14;

                if (this.nextInt(var18++) == 0)
                    outID = var15;

                if (this.nextInt(var18++) == 0)
                    outID = var16;

                if (this.nextInt(2) == 0)
                    outCache[x + z * xMax] = outID;
                else
                    outCache[x + z * xMax] = thisID;

            }
        }
        return outCache;
    }
}
