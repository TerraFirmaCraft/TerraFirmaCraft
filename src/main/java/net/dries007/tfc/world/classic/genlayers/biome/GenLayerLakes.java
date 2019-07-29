/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.world.classic.biomes.BiomesTFC;
import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerLakes extends GenLayerTFC
{
    public GenLayerLakes(long seed, GenLayerTFC parent)
    {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        int[] var5 = this.parent.getInts(par1 - 1, par2 - 1, par3 + 2, par4 + 2);
        int[] var6 = IntCache.getIntCache(par3 * par4);
        int var10;
        int var11;
        int var12;
        int var13;

        for (int var7 = 0; var7 < par4; ++var7)
        {
            for (int var8 = 0; var8 < par3; ++var8)
            {
                this.initChunkSeed(var8 + par1, var7 + par2);
                int var9 = var5[var8 + 1 + (var7 + 1) * (par3 + 2)];


                var10 = var5[var8 + 1 + (var7 + 1 - 1) * (par3 + 2)];
                var11 = var5[var8 + 1 + 1 + (var7 + 1) * (par3 + 2)];
                var12 = var5[var8 + 1 - 1 + (var7 + 1) * (par3 + 2)];
                var13 = var5[var8 + 1 + (var7 + 1 + 1) * (par3 + 2)];

                if (BiomesTFC.isOceanicBiome(var9))
                {
                    if (!BiomesTFC.isOceanicBiome(var10) && !BiomesTFC.isOceanicBiome(var11) && !BiomesTFC.isOceanicBiome(var12) && !BiomesTFC.isOceanicBiome(var13))
                        var6[var8 + var7 * par3] = Biome.getIdForBiome(BiomesTFC.LAKE);
                    else
                        var6[var8 + var7 * par3] = var9;
                }
                else
                {
                    var6[var8 + var7 * par3] = var9;
                }
            }
        }
        return var6;
    }
}
