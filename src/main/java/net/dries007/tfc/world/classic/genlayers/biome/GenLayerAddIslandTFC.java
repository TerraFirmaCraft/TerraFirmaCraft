package net.dries007.tfc.world.classic.genlayers.biome;

import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

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
        int var5 = x - 1;
        int var6 = y - 1;
        int var7 = w + 2;
        int var8 = h + 2;
        int[] var9 = this.parent.getInts(var5, var6, var7, var8);
        int[] var10 = IntCache.getIntCache(w * h);

        for (int var11 = 0; var11 < h; ++var11)
        {
            for (int var12 = 0; var12 < w; ++var12)
            {
                int var13 = var9[var12 + 0 + (var11 + 0) * var7];
                int var14 = var9[var12 + 2 + (var11 + 0) * var7];
                int var15 = var9[var12 + 0 + (var11 + 2) * var7];
                int var16 = var9[var12 + 2 + (var11 + 2) * var7];
                int var17 = var9[var12 + 1 + (var11 + 1) * var7];
                this.initChunkSeed(var12 + x, var11 + y);

                if (var17 == 0 && (var13 != 0 || var14 != 0 || var15 != 0 || var16 != 0))
                {
                    int var18 = 1;
                    int var19 = 1;

                    if (var13 != 0 && this.nextInt(var18++) == 0)
                        var19 = var13;

                    if (var14 != 0 && this.nextInt(var18++) == 0)
                        var19 = var14;

                    if (var15 != 0 && this.nextInt(var18++) == 0)
                        var19 = var15;

                    if (var16 != 0 && this.nextInt(var18++) == 0)
                        var19 = var16;

                    if (this.nextInt(3) == 0)
                        var10[var12 + var11 * w] = var19;
                    else
                        var10[var12 + var11 * w] = 0;
                }
                else if (var17 > 0 && (var13 == 0 || var14 == 0 || var15 == 0 || var16 == 0))
                {
                    if (this.nextInt(5) == 0)
                        var10[var12 + var11 * w] = 0;
                    else
                        var10[var12 + var11 * w] = var17;
                }
                else
                {
                    var10[var12 + var11 * w] = var17;
                }
            }
        }
        return var10;
    }
}
