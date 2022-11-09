/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.List;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.settings.RockLayerSettings;
import net.dries007.tfc.world.settings.RockSettings;

import static net.dries007.tfc.world.TFCChunkGenerator.SEA_LEVEL_Y;

public class RockData
{
    private static final int SIZE = 16 * 16;

    private static int index(int x, int z)
    {
        return (x & 15) | ((z & 15) << 4);
    }

    private final RockSettings[] bottomLayer;
    private final RockSettings[] middleLayer;
    private final RockSettings[] topLayer;
    private final int[] rockLayerHeight;

    @Nullable private int[] surfaceHeight;

    public RockData(RockSettings[] bottomLayer, RockSettings[] middleLayer, RockSettings[] topLayer, int[] rockLayerHeight)
    {
        this.bottomLayer = bottomLayer;
        this.middleLayer = middleLayer;
        this.topLayer = topLayer;
        this.rockLayerHeight = rockLayerHeight;
        this.surfaceHeight = null;
    }

    public RockData(CompoundTag nbt, RockLayerSettings settings)
    {
        this.bottomLayer = new RockSettings[SIZE];
        this.middleLayer = new RockSettings[SIZE];
        this.topLayer = new RockSettings[SIZE];

        read(bottomLayer, nbt.getIntArray("bottomLayer"), settings);
        read(middleLayer, nbt.getIntArray("middleLayer"), settings);
        read(topLayer, nbt.getIntArray("topLayer"), settings);

        rockLayerHeight = nbt.getIntArray("height");
        surfaceHeight = nbt.contains("surfaceHeight") ? nbt.getIntArray("surfaceHeight") : null;
    }

    public RockSettings getRock(BlockPos pos)
    {
        return getRock(pos.getX(), pos.getY(), pos.getZ());
    }

    public RockSettings getRock(int x, int y, int z)
    {
        assert surfaceHeight != null;

        final int i = index(x, z);
        final int sh = surfaceHeight[i];
        final int rh = rockLayerHeight[i];
        if (y > (int) (SEA_LEVEL_Y + 46 - 0.2 * sh + rh)) // todo: un-hardcode these, keep a sea level reference held by the rock data instance.
        {
            return topLayer[i];
        }
        else if (y > (int) (SEA_LEVEL_Y - 34 - 0.2 * sh + rh))
        {
            return middleLayer[i];
        }
        else
        {
            return bottomLayer[i];
        }
    }

    public RockSettings getTopRock(int x, int z)
    {
        return bottomLayer[index(x, z)];
    }

    public RockSettings getMiddleRock(int x, int z)
    {
        return bottomLayer[index(x, z)];
    }

    public RockSettings getBottomRock(int x, int z)
    {
        return bottomLayer[index(x, z)];
    }

    public void setSurfaceHeight(int[] surfaceHeightMap)
    {
        this.surfaceHeight = surfaceHeightMap;
    }

    public CompoundTag write(RockLayerSettings settings)
    {
        final CompoundTag nbt = new CompoundTag();

        // Record the raw byte values
        nbt.putIntArray("bottomLayer", write(bottomLayer, settings));
        nbt.putIntArray("middleLayer", write(middleLayer, settings));
        nbt.putIntArray("topLayer", write(topLayer, settings));

        nbt.putIntArray("height", rockLayerHeight);
        if (surfaceHeight != null)
        {
            nbt.putIntArray("surfaceHeight", surfaceHeight);
        }
        return nbt;
    }

    private int[] write(RockSettings[] layer, RockLayerSettings settings)
    {
        final int[] array = new int[SIZE];
        final List<RockSettings> palette = settings.getRocks();
        for (int i = 0; i < array.length; i++)
        {
            array[i] = palette.indexOf(layer[i]); // indexOf() is O(n) but should be fast enough for our purposes, this isn't called that often
        }
        return array;
    }

    private void read(RockSettings[] layer, int[] data, RockLayerSettings settings)
    {
        assert data.length == SIZE;

        final List<RockSettings> palette = settings.getRocks();
        for (int i = 0; i < data.length; i++)
        {
            layer[i] = palette.get(data[i]);
        }
    }
}