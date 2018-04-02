package net.dries007.tfc.world.classic.genlayers.datalayers.rock;

import net.dries007.tfc.world.classic.DataLayer;
import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerRockInit extends GenLayerTFC
{
    private DataLayer[] layerRocks;

    public GenLayerRockInit(long par1, DataLayer[] rocks)
    {
        super(par1);
        layerRocks = rocks.clone();
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
                cache[x + z * maxX] = layerRocks[this.nextInt(layerRocks.length)].layerID;
            }
        }

        return cache;
    }
}
