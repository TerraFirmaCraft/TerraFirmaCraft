package net.dries007.tfc.world.classic.genlayers.datalayers.tree;

import net.dries007.tfc.world.classic.DataLayer;
import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerTreeInit extends GenLayerTFC
{
    private DataLayer[] layerTrees;

    public GenLayerTreeInit(long par1, DataLayer[] trees)
    {
        super(par1);
        layerTrees = trees.clone();
    }

    @Override
    public int[] getInts(int par1, int par2, int maxX, int maxZ)
    {
        int[] cache = IntCache.getIntCache(maxX * maxZ);

        for (int z = 0; z < maxZ; ++z)
        {
            for (int x = 0; x < maxX; ++x)
            {
                this.initChunkSeed(par1 + x, par2 + z);
                cache[x + z * maxX] = layerTrees[this.nextInt(layerTrees.length)].layerID;
            }
        }

        return cache;
    }
}
