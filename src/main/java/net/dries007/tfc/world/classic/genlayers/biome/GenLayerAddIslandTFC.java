/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.biome;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerAddIslandTFC extends GenLayerTFC
{
    public GenLayerAddIslandTFC(long seed, GenLayer parent)
    {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int x, int y, int w, int h)
    {
        int w2 = w + 2;
        int h2 = h + 2;
        int[] ints = this.parent.getInts(x - 1, y - 1, w2, h2);
        int[] out = IntCache.getIntCache(w * h);

        for (int yy = 0; yy < h; ++yy)
        {
            for (int xx = 0; xx < w; ++xx)
            {
                int dl = ints[xx + yy * w2]; // down left
                int dr = ints[xx + 2 + yy * w2];  // down right
                int ul = ints[xx + (yy + 2) * w2];  // up left
                int ur = ints[xx + 2 + (yy + 2) * w2];  // up right
                int us = ints[xx + 1 + (yy + 1) * w2];  // center (us)
                this.initChunkSeed(xx + x, yy + y);

                if (us == oceanID && (dl != oceanID || dr != oceanID || ul != oceanID || ur != oceanID))
                {
                    int countNonOcean = 1;
                    int lastNonOcean = plainsID;

                    if (dl != oceanID && this.nextInt(countNonOcean++) == 0)
                        lastNonOcean = dl;

                    if (dr != oceanID && this.nextInt(countNonOcean++) == 0)
                        lastNonOcean = dr;

                    if (ul != oceanID && this.nextInt(countNonOcean++) == 0)
                        lastNonOcean = ul;

                    if (ur != oceanID && this.nextInt(countNonOcean/*++*/) == 0)
                        lastNonOcean = ur;

                    if (this.nextInt(3) == 0)
                        out[xx + yy * w] = lastNonOcean;
                    else
                        out[xx + yy * w] = oceanID;
                }
                else if (us != oceanID && (dl == oceanID || dr == oceanID || ul == oceanID || ur == oceanID))
                {
                    if (this.nextInt(5) == 0)
                        out[xx + yy * w] = oceanID;
                    else
                        out[xx + yy * w] = us;
                }
                else
                {
                    out[xx + yy * w] = us;
                }
            }
        }
        return out;
    }
}
