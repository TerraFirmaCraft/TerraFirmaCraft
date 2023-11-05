/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.ChunkPos;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.region.Units;

public record ChunkRockDataCache(
    ChunkPos pos,
    List<float[]> layerHeight, // Array indexed by index(x, z), list indexed by layer (up to max of eight layers, lazily populated)
    List<float[]> layerSkew // Array indexed by index(x, z) << 1, with x,z components, list indexed by layer (up to max of eight layers, lazily populated)
) {
    public ChunkRockDataCache(ChunkPos pos)
    {
        this(pos, new ArrayList<>(8), new ArrayList<>(8));
    }

    /**
     * @return The number of cached layers, both of height and skew noise generated.
     */
    public int layers()
    {
        return layerHeight.size();
    }

    public void addLayer(float[] layerHeight, float[] layerSkew)
    {
        assert layerHeight.length == 16 * 16;
        assert layerSkew.length == 16 * 16 * 2;

        this.layerHeight.add(layerHeight);
        this.layerSkew.add(layerSkew);
    }

    public float getLayerHeight(int layer, int x, int z)
    {
        return layerHeight.get(layer)[Units.index(x, z)];
    }

    public float getLayerSkewX(int layer, int x, int z)
    {
        return layerSkew.get(layer)[Units.index(x, z) << 1];
    }

    public float getLayerSkewZ(int layer, int x, int z)
    {
        return layerSkew.get(layer)[(Units.index(x, z) << 1) | 0b1];
    }
}
