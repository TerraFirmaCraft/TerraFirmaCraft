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

public class GenLayerBiomeEdge extends GenLayerTFC
{
    public GenLayerBiomeEdge(long seed, GenLayer parent)
    {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int par1, int par2, int xSize, int zSize)
    {
        int[] inCache = this.parent.getInts(par1 - 1, par2 - 1, xSize + 2, zSize + 2);
//        validateIntArray(inCache, xSize + 2, zSize + 2);
        int[] outCache = IntCache.getIntCache(xSize * zSize);
        int var10;
        int var11;
        int var12;
        int var13;

        for (int z = 0; z < zSize; ++z)
        {
            for (int x = 0; x < xSize; ++x)
            {
                this.initChunkSeed(x + par1, z + par2);
                int thisID = inCache[x + 1 + (z + 1) * (xSize + 2)];

                var10 = inCache[x + 1 + (z + 1 - 1) * (xSize + 2)];
                var11 = inCache[x + 1 + 1 + (z + 1) * (xSize + 2)];
                var12 = inCache[x + 1 - 1 + (z + 1) * (xSize + 2)];
                var13 = inCache[x + 1 + (z + 1 + 1) * (xSize + 2)];

                if (thisID == Biome.getIdForBiome(BiomesTFC.HIGH_HILLS))
                {
                    if (var10 == Biome.getIdForBiome(BiomesTFC.HIGH_HILLS) && var11 == Biome.getIdForBiome(BiomesTFC.HIGH_HILLS) && var12 == Biome.getIdForBiome(BiomesTFC.HIGH_HILLS) && var13 == Biome.getIdForBiome(BiomesTFC.HIGH_HILLS))
                        outCache[x + z * xSize] = thisID;
                    else
                        outCache[x + z * xSize] = Biome.getIdForBiome(BiomesTFC.HIGH_HILLS_EDGE);
                }
                else if (thisID == Biome.getIdForBiome(BiomesTFC.MOUNTAINS))
                {
                    if (var10 == Biome.getIdForBiome(BiomesTFC.MOUNTAINS) && var11 == Biome.getIdForBiome(BiomesTFC.MOUNTAINS) && var12 == Biome.getIdForBiome(BiomesTFC.MOUNTAINS) && var13 == Biome.getIdForBiome(BiomesTFC.MOUNTAINS))
                        outCache[x + z * xSize] = thisID;
                    else
                        outCache[x + z * xSize] = Biome.getIdForBiome(BiomesTFC.MOUNTAINS_EDGE);
                }
                else if (thisID == Biome.getIdForBiome(BiomesTFC.SWAMPLAND))
                {
                    if (var10 == Biome.getIdForBiome(BiomesTFC.SWAMPLAND) && var11 == Biome.getIdForBiome(BiomesTFC.SWAMPLAND) && var12 == Biome.getIdForBiome(BiomesTFC.SWAMPLAND) && var13 == Biome.getIdForBiome(BiomesTFC.SWAMPLAND))
                        outCache[x + z * xSize] = thisID;
                    else
                        outCache[x + z * xSize] = Biome.getIdForBiome(BiomesTFC.PLAINS);
                }
                else if (thisID == Biome.getIdForBiome(BiomesTFC.HIGH_PLAINS))
                {
                    if (var10 == Biome.getIdForBiome(BiomesTFC.HIGH_PLAINS) && var11 == Biome.getIdForBiome(BiomesTFC.HIGH_PLAINS) && var12 == Biome.getIdForBiome(BiomesTFC.HIGH_PLAINS) && var13 == Biome.getIdForBiome(BiomesTFC.HIGH_PLAINS))
                        outCache[x + z * xSize] = thisID;
                    else
                        outCache[x + z * xSize] = Biome.getIdForBiome(BiomesTFC.PLAINS);
                }
                else
                {
                    outCache[x + z * xSize] = thisID;
                }
            }
        }
        return outCache;
    }
}
