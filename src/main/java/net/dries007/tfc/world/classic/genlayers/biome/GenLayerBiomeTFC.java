/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.world.classic.biomes.BiomesTFC;
import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerBiomeTFC extends GenLayerTFC
{
    private final Biome[] biomes;

    public GenLayerBiomeTFC(long seed, GenLayer parent)
    {
        super(seed);
        this.parent = parent;

        biomes = BiomesTFC.getWorldGenBiomes().toArray(new Biome[0]);
    }

    @Override
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        int[] var5 = parent.getInts(par1, par2, par3, par4);
//        validateIntArray(var5, par3, par4);
        int[] var6 = IntCache.getIntCache(par3 * par4);

        for (int var7 = 0; var7 < par4; ++var7)
        {
            for (int var8 = 0; var8 < par3; ++var8)
            {
                initChunkSeed(var8 + par1, var7 + par2);
                int id = var5[var8 + var7 * par3];
                if (BiomesTFC.isOceanicBiome(id)) var6[var8 + var7 * par3] = id;
                else var6[var8 + var7 * par3] = Biome.getIdForBiome(biomes[nextInt(biomes.length)]);
            }
        }
        return var6;
    }
}
