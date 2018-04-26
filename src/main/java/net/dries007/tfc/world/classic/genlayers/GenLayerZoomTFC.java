/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers;

import net.minecraft.world.gen.layer.IntCache;

public class GenLayerZoomTFC extends GenLayerTFC
{
    public static GenLayerTFC magnify(long par0, GenLayerTFC par2GenLayer, int par3)
    {
        Object var4 = par2GenLayer;
        for (int var5 = 0; var5 < par3; ++var5)
            var4 = new GenLayerZoomTFC(par0 + var5, (GenLayerTFC) var4);
        return (GenLayerTFC) var4;
    }

    public GenLayerZoomTFC(long seed, GenLayerTFC par3GenLayer)
    {
        super(seed);
        super.parent = par3GenLayer;
    }

    @Override
    public int[] getInts(int xPos, int zPos, int xSize, int zSize)
    {
        int xCoord = xPos >> 1;
        int zCoord = zPos >> 1;
        int newXSize = (xSize >> 1) + 2;
        int newZSize = (zSize >> 1) + 2;
        int[] parentCache = this.parent.getInts(xCoord, zCoord, newXSize, newZSize);
        int i2 = newXSize - 1 << 1;
        int j2 = newZSize - 1 << 1;
        int[] out = IntCache.getIntCache(i2 * j2);
        int l2;

        for (int z = 0; z < newZSize - 1; ++z)
        {
            l2 = (z << 1) * i2;
            int i3 = 0;
            int thisID = parentCache[i3 + 0 + (z + 0) * newXSize];

            for (int x = parentCache[i3 + 0 + (z + 1) * newXSize]; i3 < newXSize - 1; ++i3)
            {
                this.initChunkSeed(i3 + xCoord << 1, z + zCoord << 1);
                int rightID = parentCache[i3 + 1 + (z + 0) * newXSize];
                int upRightID = parentCache[i3 + 1 + (z + 1) * newXSize];
                out[l2] = thisID;
                out[l2++ + i2] = this.selectRandom(thisID, x);
                out[l2] = this.selectRandom(thisID, rightID);
                out[l2++ + i2] = this.selectModeOrRandom(thisID, rightID, x, upRightID);
                thisID = rightID;
                x = upRightID;
            }
        }

        int[] outCache = IntCache.getIntCache(xSize * zSize);

        for (int zoom = 0; zoom < zSize; ++zoom)
        {
            int srcPos = (zoom + (zPos & 1)) * i2 + (xPos & 1); //NOPMD
            System.arraycopy(out, srcPos, outCache, zoom * xSize, xSize);
        }

        return outCache;
    }

    /**
     * Chooses one of the two inputs randomly.
     */
    protected int choose(int par1, int par2)
    {
        return this.nextInt(2) == 0 ? par1 : par2;
    }

    protected int choose4(int id0, int id1, int id2, int id3)
    {
        if (id1 == id2 && id2 == id3)
            return id1;
        else if (id0 == id1 && id0 == id2)
            return id0;
        else if (id0 == id1 && id0 == id3)
            return id0;
        else if (id0 == id2 && id0 == id3)
            return id0;
        else if (id0 == id1 && id2 != id3)
            return id0;
        else if (id0 == id2 && id1 != id3)
            return id0;
        else if (id0 == id3 && id1 != id2)
            return id0;
        else if (id1 == id0 && id2 != id3)
            return id1;
        else if (id1 == id2 && id0 != id3)
            return id1;
        else if (id1 == id3 && id0 != id2)
            return id1;
        else if (id2 == id0 && id1 != id3)
            return id2;
        else if (id2 == id1 && id0 != id3)
            return id2;
        else if (id2 == id3 && id0 != id1)
            return id2;
        else if (id3 == id0 && id1 != id2)
            return id2;
        else if (id3 == id1 && id0 != id2)
            return id2;
        else if (id3 == id2 && id0 != id1)
            return id2;
        else
        {
            int rand = this.nextInt(4);
            return rand == 0 ? id0 : rand == 1 ? id1 : rand == 2 ? id2 : id3;
        }
    }
}
