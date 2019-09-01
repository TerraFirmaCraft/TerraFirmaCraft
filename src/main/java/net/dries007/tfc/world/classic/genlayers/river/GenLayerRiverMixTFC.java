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

public class GenLayerRiverMixTFC extends GenLayerTFC
{
    private GenLayer biomePatternGeneratorChain;
    private GenLayer riverPatternGeneratorChain;
    private int[] layerBiomes;
    private int[] layerRivers;
    private int[] layerOut;
    private int xn;
    private int xp;
    private int zn;
    private int zp;

    public GenLayerRiverMixTFC(long par1, GenLayer par3GenLayer, GenLayer par4GenLayer)
    {
        super(par1);
        this.biomePatternGeneratorChain = par3GenLayer;
        this.riverPatternGeneratorChain = par4GenLayer;
    }

    public void removeRiver(int index, int biomeToReplaceWith)
    {
        if (layerOut[index] == Biome.getIdForBiome(BiomesTFC.RIVER))
        {
            if (xn >= 0 && layerBiomes[xn] == biomeToReplaceWith)
            {
                layerOut[index] = biomeToReplaceWith;
            }
            if (zn >= 0 && layerBiomes[zn] == biomeToReplaceWith)
            {
                layerOut[index] = biomeToReplaceWith;
            }
            if (xp < layerBiomes.length && layerBiomes[xp] == biomeToReplaceWith)
            {
                layerOut[index] = biomeToReplaceWith;
            }
            if (zp < layerBiomes.length && layerBiomes[zp] == biomeToReplaceWith)
            {
                layerOut[index] = biomeToReplaceWith;
            }
        }
    }

    public boolean inBounds(int index, int[] array)
    {
        return index < array.length && index >= 0;
    }

    /**
     * Initialize layer's local worldGenSeed based on its own baseSeed and the world's global seed (passed in as an
     * argument).
     */
    @Override
    public void initWorldGenSeed(long par1)
    {
        this.biomePatternGeneratorChain.initWorldGenSeed(par1);
        this.riverPatternGeneratorChain.initWorldGenSeed(par1);
        super.initWorldGenSeed(par1);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public int[] getInts(int x, int z, int xSize, int zSize)
    {
        layerBiomes = this.biomePatternGeneratorChain.getInts(x, z, xSize, zSize);
        layerRivers = this.riverPatternGeneratorChain.getInts(x, z, xSize, zSize);
        layerOut = IntCache.getIntCache(xSize * zSize);

        for (int zElement = 0; zElement < zSize; ++zElement)
        {
            for (int xElement = 0; xElement < xSize; ++xElement)
            {
                int index = xElement + zElement * xSize;
                int b = layerBiomes[index];
                int r = layerRivers[index];

                xn = index - 1;
                xp = index + 1;
                zn = index - zSize;
                zp = index + zSize;

                if (BiomesTFC.isOceanicBiome(b))
                    layerOut[index] = b;
                else if (r > 0)
                {
                    layerOut[index] = r;

                    //Here we make sure that rivers dont run along ocean/beach splits. We turn the river into oceans.
                    if (BiomesTFC.isBeachBiome(b))
                    {
                        layerOut[index] = Biome.getIdForBiome(BiomesTFC.OCEAN);
                        if (inBounds(xn, layerOut) && layerOut[xn] == Biome.getIdForBiome(BiomesTFC.RIVER))
                        {
                            layerOut[xn] = Biome.getIdForBiome(BiomesTFC.OCEAN);
                        }
                        if (inBounds(zn, layerOut) && layerOut[zn] == Biome.getIdForBiome(BiomesTFC.RIVER))
                        {
                            layerOut[zn] = Biome.getIdForBiome(BiomesTFC.OCEAN);
                        }
                        if (inBounds(zp, layerOut) && BiomesTFC.isOceanicBiome(layerBiomes[zp]) && layerRivers[zp] == 0)
                        {
                            layerOut[index] = b;
                        }
                        if (inBounds(zn, layerOut) && BiomesTFC.isOceanicBiome(layerBiomes[zn]) && layerRivers[zn] == 0)
                        {
                            layerOut[index] = b;
                        }
                        if (inBounds(xn, layerOut) && BiomesTFC.isOceanicBiome(layerBiomes[xn]) && layerRivers[xn] == 0)
                        {
                            layerOut[index] = b;
                        }
                        if (inBounds(xp, layerOut) && BiomesTFC.isOceanicBiome(layerBiomes[xp]) && layerRivers[xp] == 0)
                        {
                            layerOut[index] = b;
                        }
                    }
                }
                else
                    layerOut[index] = b;

                //Similar to above, if we're near a lake, we turn the river into lake.
                removeRiver(index, Biome.getIdForBiome(BiomesTFC.LAKE));
            }
        }
        return layerOut.clone();
    }
}
