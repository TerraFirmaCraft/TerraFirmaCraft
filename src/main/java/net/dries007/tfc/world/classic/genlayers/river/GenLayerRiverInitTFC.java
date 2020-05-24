/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.river;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerRiverInitTFC extends GenLayerTFC
{
    public GenLayerRiverInitTFC(long par1, GenLayer par3GenLayer)
    {
        super(par1);
        this.parent = par3GenLayer;
    }

    @Override
    public int[] getInts(int xCoord, int zCoord, int xSize, int zSize)
    {
        int[] parentCache = this.parent.getInts(xCoord, zCoord, xSize, zSize);
        int[] outCache = IntCache.getIntCache(xSize * zSize);

        for (int z = 0; z < zSize; ++z)
        {
            for (int x = 0; x < xSize; ++x)
            {
                this.initChunkSeed(x + xCoord, z + zCoord);
                int index = x + z * xSize;
                int id = parentCache[index];
                outCache[index] = !isOceanicBiome(id) && !isMountainBiome(id) ? 1 : 0;
            }
        }
        return outCache;
    }
}
