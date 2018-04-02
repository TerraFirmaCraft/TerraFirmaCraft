package net.dries007.tfc.world.classic.genlayers.datalayers.evt;

import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerEVTInit extends GenLayerTFC
{
    public GenLayerEVTInit(long par1)
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
                int out = GenEVTLayer.LOW + this.nextInt(4);
                /*
                 * We want to make High EVT areas slightly more rare so that there is more vegetation than not
                 * so we hide it behind another rand
                 * */
                if (out == GenEVTLayer.LOW && this.nextInt(4) == 0)
                    out += 1 + this.nextInt(2);

                if (out == GenEVTLayer.LOW && this.nextInt(12) == 0)
                    out--;
                if (out == GenEVTLayer.HIGH && this.nextInt(12) == 0)
                    out++;

                outCache[x + z * maxX] = out;
            }
        }

        return outCache;
    }
}
