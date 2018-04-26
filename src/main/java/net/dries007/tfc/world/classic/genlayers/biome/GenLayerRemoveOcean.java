/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.biome;

import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerRemoveOcean extends GenLayerTFC
{
    private final int chance;
    private final boolean checkType;

    public GenLayerRemoveOcean(long par1, GenLayerTFC parent, int chance, boolean ct)
    {
        super(par1);
        this.parent = parent;
        this.chance = chance;
        this.checkType = ct;
    }

    @Override
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        int i1 = par1 - 1;
        int j1 = par2 - 1;
        int k1 = par3 + 2;
        int l1 = par4 + 2;
        int[] biomes = this.parent.getInts(i1, j1, k1, l1);
        int[] out = IntCache.getIntCache(par3 * par4);

        for (int i2 = 0; i2 < par4; ++i2)
        {
            for (int j2 = 0; j2 < par3; ++j2)
            {
                int k2 = biomes[j2 + 1 + (i2 + 1 - 1) * (par3 + 2)];
                int l2 = biomes[j2 + 1 + 1 + (i2 + 1) * (par3 + 2)];
                int i3 = biomes[j2 + 1 - 1 + (i2 + 1) * (par3 + 2)];
                int j3 = biomes[j2 + 1 + (i2 + 1 + 1) * (par3 + 2)];
                int biome = biomes[j2 + 1 + (i2 + 1) * k1];
                out[j2 + i2 * par3] = biome;
                this.initChunkSeed(j2 + par1, i2 + par2);

                if (checkType && biome == 0 && k2 == 0 && l2 == 0 && i3 == 0 && j3 == 0 && this.nextInt(chance) == 0)
                {
                    out[j2 + i2 * par3] = 1;
                } else if (!checkType && biome == 0 && k2 != 0 && l2 != 0 && i3 != 0 && j3 != 0)
                {
                    out[j2 + i2 * par3] = 1;
                }
            }
        }

        return out;
    }
}
