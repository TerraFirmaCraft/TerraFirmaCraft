/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.river;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.world.classic.biomes.BiomesTFC;
import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerRiverTFC extends GenLayerTFC
{
    public GenLayerRiverTFC(long par1, GenLayer par3GenLayer)
    {
        super(par1);
        super.parent = par3GenLayer;
    }

    @Override
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        int i1 = par1 - 1;
        int j1 = par2 - 1;
        int k1 = par3 + 2;
        int l1 = par4 + 2;
        int[] aint = this.parent.getInts(i1, j1, k1, l1);
        int[] aint1 = IntCache.getIntCache(par3 * par4);

        for (int i2 = 0; i2 < par4; ++i2)
        {
            for (int j2 = 0; j2 < par3; ++j2)
            {
                int k2 = this.calcWidth(aint[j2 + 0 + (i2 + 1) * k1]);
                int l2 = this.calcWidth(aint[j2 + 2 + (i2 + 1) * k1]);
                int i3 = this.calcWidth(aint[j2 + 1 + (i2 + 0) * k1]);
                int j3 = this.calcWidth(aint[j2 + 1 + (i2 + 2) * k1]);
                int k3 = this.calcWidth(aint[j2 + 1 + (i2 + 1) * k1]);

                if (k3 == k2 && k3 == i3 && k3 == l2 && k3 == j3)
                {
                    aint1[j2 + i2 * par3] = 0;
                }
                else
                {
                    aint1[j2 + i2 * par3] = Biome.getIdForBiome(BiomesTFC.RIVER);
                }
            }
        }

        return aint1;
    }

    private int calcWidth(int i)
    {
        return i >= 2 ? 2 + (i & 1) : i; // Spits back 2 for even numbers >= 2 and 3 for odd numbers.
    }
}
