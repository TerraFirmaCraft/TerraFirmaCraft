/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.river;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerRiverTFC extends GenLayerTFC
{
    public GenLayerRiverTFC(long par1, GenLayer par3GenLayer)
    {
        super(par1);
        super.parent = par3GenLayer;
    }

    @Override
    public int[] getInts(int x, int z, int sizeX, int sizeZ)
    {
        int sizeX2 = sizeX + 2;
        int sizeZ2 = sizeZ + 2;
        int[] ints = this.parent.getInts(x - 1, z - 1, sizeX2, sizeZ2);
        int[] out = IntCache.getIntCache(sizeX * sizeZ);

        for (int zz = 0; zz < sizeZ; ++zz)
        {
            for (int xx = 0; xx < sizeX; ++xx)
            {
                int k2 = this.calcWidth(ints[xx + 0 + (zz + 1) * sizeX2]);
                int l2 = this.calcWidth(ints[xx + 2 + (zz + 1) * sizeX2]);
                int i3 = this.calcWidth(ints[xx + 1 + (zz + 0) * sizeX2]);
                int j3 = this.calcWidth(ints[xx + 1 + (zz + 2) * sizeX2]);
                int k3 = this.calcWidth(ints[xx + 1 + (zz + 1) * sizeX2]);

                if (k3 == k2 && k3 == i3 && k3 == l2 && k3 == j3)
                {
                    out[xx + zz * sizeX] = plainsID;
                }
                else
                {
                    out[xx + zz * sizeX] = riverID;
                }
            }
        }

        return out;
    }

    private int calcWidth(int i)
    {
        return i >= 2 ? 2 + (i & 1) : i; // Spits back 2 for even numbers >= 2 and 3 for odd numbers.
    }
}
