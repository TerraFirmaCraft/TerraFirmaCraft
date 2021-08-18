/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import net.dries007.tfc.world.settings.RockLayerSettings;
import net.dries007.tfc.world.settings.RockSettings;

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

        // Build pallet
        final ListTag pallet = nbt.getList("pallet", Constants.NBT.TAG_STRING);
        final List<RockSettings> uniqueRocks = new ArrayList<>(pallet.size());
        for (int i = 0; i < pallet.size(); i++)
        {
            uniqueRocks.add(settings.getRock(new ResourceLocation(pallet.getString(i))));
        }

        fromByteArray(bottomLayer, nbt.getByteArray("bottomLayer"), uniqueRocks);
        fromByteArray(middleLayer, nbt.getByteArray("middleLayer"), uniqueRocks);
        fromByteArray(topLayer, nbt.getByteArray("topLayer"), uniqueRocks);

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
        if (y > (int) (142 - 0.2 * sh + rh))
        {
            return topLayer[i];
        }
        else if (y > (int) (82 - 0.2 * sh + rh))
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
        return topLayer[index(x, z)];
    }

    public RockSettings getMidRock(int x, int z)
    {
        return middleLayer[index(x, z)];
    }

    public RockSettings getBottomRock(int x, int z)
    {
        return bottomLayer[index(x, z)];
    }

    public void setSurfaceHeight(int[] surfaceHeightMap)
    {
        this.surfaceHeight = surfaceHeightMap;
    }

    public CompoundTag write()
    {
        CompoundTag nbt = new CompoundTag();

        // Record a map from bytes -> rocks (pallet, similar to vanilla world save format)
        // This should really be shorts (but NBT does not have a short array, only ints), since three rock layers *technically* can use up to 3 * 256 unique rocks. However I think it's probably safe to assume there will never be (in chunk data), more than 256 rocks per chunk.
        // However, at that point it's not actually more efficient to store a pallet, as the int ID of the rock is probably shorter.
        // But, it does safeguard this chunk against changing rocks in the future, which is important.
        final List<RockSettings> uniqueRocks = Stream.of(bottomLayer, middleLayer, topLayer).flatMap(Arrays::stream).distinct().collect(Collectors.toList());
        final ListTag pallet = new ListTag();
        for (RockSettings rock : uniqueRocks)
        {
            pallet.add(StringTag.valueOf(rock.id().toString()));
        }
        nbt.put("pallet", pallet);

        // Record the raw byte values
        nbt.putByteArray("bottomLayer", toByteArray(topLayer, uniqueRocks));
        nbt.putByteArray("middleLayer", toByteArray(middleLayer, uniqueRocks));
        nbt.putByteArray("topLayer", toByteArray(bottomLayer, uniqueRocks));

        nbt.putIntArray("height", rockLayerHeight);
        if (surfaceHeight != null)
        {
            nbt.putIntArray("surfaceHeight", surfaceHeight);
        }
        return nbt;
    }

    private byte[] toByteArray(RockSettings[] layer, List<RockSettings> palette)
    {
        byte[] array = new byte[SIZE];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = (byte) palette.indexOf(layer[i]); // indexOf() is O(n) but should be fast enough for our purposes, this isn't called that often
        }
        return array;
    }

    private void fromByteArray(RockSettings[] layer, byte[] data, List<RockSettings> palette)
    {
        assert data.length == SIZE;
        for (int i = 0; i < data.length; i++)
        {
            layer[i] = palette.get(data[i]);
        }
    }
}