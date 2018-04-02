package net.dries007.tfc.world.classic.genlayers.datalayers.drainage;

import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerDrainageInit extends GenLayerTFC
{
    public GenLayerDrainageInit(long par1)
    {
        super(par1);
    }

    @Override
    public int[] getInts(int par1, int par2, int maxX, int maxZ)
    {
        int[] outCache = IntCache.getIntCache(maxX * maxZ);

        for (int z = 0; z < maxZ; ++z)
        {
            for (int x = 0; x < maxX; ++x)
            {
                this.initChunkSeed(par1 + x, par2 + z);
                int out = GenDrainageLayer.MIN + this.nextInt(5);
                outCache[x + z * maxX] = out;
            }
        }

        return outCache;
    }
}
