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

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.RockManager;
import net.dries007.tfc.util.Helpers;

public class RockData implements INBTSerializable<CompoundNBT>
{
    public static final RockData EMPTY = new Immutable();

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
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();

        // Record a map from bytes -> rocks (pallet, similar to vanilla world save format)
        // This should really be shorts (but NBT does not have a short array, only ints), since three rock layers *technically* can use up to 3 * 256 unique rocks. However I think it's probably safe to assume there will never be (in chunk data), more than 256 rocks per chunk.
        // However, at that point it's not actually more efficient to store a pallet, as the int ID of the rock is probably shorter.
        // But, it does safeguard this chunk against changing rocks in the future, which is important.
        List<Rock> uniqueRocks = Stream.of(bottomLayer, middleLayer, topLayer).flatMap(Arrays::stream).distinct().collect(Collectors.toList());
        ListNBT pallet = new ListNBT();
        byte index = 0;
        for (Rock rock : uniqueRocks)
        {
            pallet.add(index, StringNBT.valueOf(rock.getId().toString()));
        }
        nbt.put("pallet", pallet);

        // Record the raw byte values
        nbt.putByteArray("bottomLayer", Helpers.createByteArray(topLayer, r -> (byte) uniqueRocks.indexOf(r)));
        nbt.putByteArray("middleLayer", Helpers.createByteArray(middleLayer, r -> (byte) uniqueRocks.indexOf(r)));
        nbt.putByteArray("topLayer", Helpers.createByteArray(bottomLayer, r -> (byte) uniqueRocks.indexOf(r)));

        nbt.putIntArray("height", rockLayerHeight);
        if (surfaceHeight != null)
        {
            nbt.putIntArray("surface_height", surfaceHeight);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nullable CompoundNBT nbt)
    {
        if (nbt != null)
        {
            // Build pallet
            ListNBT pallet = nbt.getList("pallet", Constants.NBT.TAG_STRING);
            List<Rock> uniqueRocks = new ArrayList<>(pallet.size());
            for (int i = 0; i < pallet.size(); i++)
            {
                uniqueRocks.add(RockManager.INSTANCE.getOrDefault(new ResourceLocation(pallet.getString(i))));
            }

            Helpers.createArrayFromBytes(nbt.getByteArray("bottomLayer"), bottomLayer, uniqueRocks::get);
            Helpers.createArrayFromBytes(nbt.getByteArray("middleLayer"), middleLayer, uniqueRocks::get);
            Helpers.createArrayFromBytes(nbt.getByteArray("topLayer"), topLayer, uniqueRocks::get);

            rockLayerHeight = nbt.getIntArray("height");
            if (nbt.contains("surface_height"))
            {
                surfaceHeight = nbt.getIntArray("surface_height");
            }
        }
    }

    private static class Immutable extends RockData
    {
        @SuppressWarnings("ConstantConditions")
        Immutable()
        {
            // This will crash, but it will crash in expected locations, and we don't have much to add to the crash if we overrode the methods here anyway
            super(null, null, null, null);
        }

        @Override
        public void setSurfaceHeight(int[] surfaceHeightMap)
        {
            throw new UnsupportedOperationException("Tried to modify immutable rock data");
        }

        @Override
        public CompoundNBT serializeNBT()
        {
            return new CompoundNBT();
        }

        @Override
        public void deserializeNBT(@Nullable CompoundNBT nbt) {}
    }
}