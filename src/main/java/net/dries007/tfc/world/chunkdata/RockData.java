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

import net.minecraft.nbt.ByteArrayNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.RockManager;
import net.dries007.tfc.util.Helpers;

public class RockData implements INBTSerializable<CompoundTag>
{
    private static final int SIZE = 16 * 16;

    private static int index(int x, int z)
    {
        return (x & 15) | ((z & 15) << 4);
    }

    private final Rock[] bottomLayer;
    private final Rock[] middleLayer;
    private final Rock[] topLayer;
    private int[] rockLayerHeight;
    private int[] surfaceHeight;

    public RockData(Rock[] bottomLayer, Rock[] middleLayer, Rock[] topLayer, int[] rockLayerHeight)
    {
        this.bottomLayer = bottomLayer;
        this.middleLayer = middleLayer;
        this.topLayer = topLayer;
        this.rockLayerHeight = rockLayerHeight;
        this.surfaceHeight = null;
    }

    @SuppressWarnings("ConstantConditions")
    public RockData(CompoundTag nbt)
    {
        // The null rock layer height is replaced immediately after in deserializeNBT(), as opposed to the final fields which require them to be pre-initialized to the correct length
        this(new Rock[SIZE], new Rock[SIZE], new Rock[SIZE], null);
        deserializeNBT(nbt);
    }

    public Rock getRock(BlockPos pos)
    {
        return getRock(pos.getX(), pos.getY(), pos.getZ());
    }

    public Rock getRock(int x, int y, int z)
    {
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

    public Rock getTopRock(int x, int z)
    {
        return topLayer[index(x, z)];
    }

    public Rock getMidRock(int x, int z)
    {
        return middleLayer[index(x, z)];
    }

    public Rock getBottomRock(int x, int z)
    {
        return bottomLayer[index(x, z)];
    }

    public void setSurfaceHeight(int[] surfaceHeightMap)
    {
        this.surfaceHeight = surfaceHeightMap;
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();

        // Record a map from bytes -> rocks (pallet, similar to vanilla world save format)
        // This should really be shorts (but NBT does not have a short array, only ints), since three rock layers *technically* can use up to 3 * 256 unique rocks. However I think it's probably safe to assume there will never be (in chunk data), more than 256 rocks per chunk.
        // However, at that point it's not actually more efficient to store a pallet, as the int ID of the rock is probably shorter.
        // But, it does safeguard this chunk against changing rocks in the future, which is important.
        final List<Rock> uniqueRocks = Stream.of(bottomLayer, middleLayer, topLayer).flatMap(Arrays::stream).distinct().collect(Collectors.toList());
        final ListTag pallet = new ListTag();
        for (Rock rock : uniqueRocks)
        {
            pallet.add(StringTag.valueOf(rock.getId().toString()));
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

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        // Build pallet
        final ListTag pallet = nbt.getList("pallet", Constants.NBT.TAG_STRING);
        final List<Rock> uniqueRocks = new ArrayList<>(pallet.size());
        for (int i = 0; i < pallet.size(); i++)
        {
            uniqueRocks.add(RockManager.INSTANCE.getOrDefault(new ResourceLocation(pallet.getString(i))));
        }

        fromByteArray(bottomLayer, nbt.getByteArray("bottomLayer"), uniqueRocks);
        fromByteArray(middleLayer, nbt.getByteArray("middleLayer"), uniqueRocks);
        fromByteArray(topLayer, nbt.getByteArray("topLayer"), uniqueRocks);

        rockLayerHeight = nbt.getIntArray("height");
        surfaceHeight = nbt.contains("surfaceHeight") ? nbt.getIntArray("surfaceHeight") : null;
    }

    private byte[] toByteArray(Rock[] layer, List<Rock> palette)
    {
        byte[] array = new byte[SIZE];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = (byte) palette.indexOf(layer[i]); // indexOf() is O(n) but should be fast enough for our purposes, this isn't called that often
        }
        return array;
    }

    private void fromByteArray(Rock[] layer, byte[] data, List<Rock> palette)
    {
        assert data.length == SIZE;
        for (int i = 0; i < data.length; i++)
        {
            layer[i] = palette.get(data[i]);
        }
    }
}