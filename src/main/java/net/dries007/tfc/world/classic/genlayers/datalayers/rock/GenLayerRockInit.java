/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.datalayers.rock;

import javax.annotation.Nonnull;

import net.minecraft.world.gen.layer.IntCache;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

public class GenLayerRockInit extends GenLayerTFC
{
    private Rock[] layerRocks;

    public GenLayerRockInit(long par1, Rock[] rocks)
    {
        super(par1);
        layerRocks = rocks.clone();
    }

    @Override
    @Nonnull
    public int[] getInts(int par1, int par2, int maxX, int maxZ)
    {
        int[] cache = IntCache.getIntCache(maxX * maxZ);

        for (int z = 0; z < maxZ; ++z)
        {
            for (int x = 0; x < maxX; ++x)
            {
                this.initChunkSeed(par1 + x, par2 + z);
                cache[x + z * maxX] = layerRocks[this.nextInt(layerRocks.length)].getId();
            }
        }

        return cache;
    }
}
