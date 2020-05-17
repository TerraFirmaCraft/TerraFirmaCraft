/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import net.dries007.tfc.api.Rock;
import net.dries007.tfc.objects.types.RockManager;
import net.dries007.tfc.util.Helpers;

public class RockData implements INBTSerializable<CompoundNBT>
{
    private final Rock[] bottomLayer;
    private final Rock[] middleLayer;
    private final Rock[] topLayer;
    private final int[] rockLayerHeight;

    public RockData()
    {
        this(new Rock[256], new Rock[256], new Rock[256], new int[256]);
    }

    public RockData(Rock[] bottomLayer, Rock[] middleLayer, Rock[] topLayer, int[] rockLayerHeight)
    {
        this.bottomLayer = bottomLayer;
        this.middleLayer = middleLayer;
        this.topLayer = topLayer;
        this.rockLayerHeight = rockLayerHeight;
    }

    public Rock getRock(int x, int y, int z)
    {
        if (y > getRockHeight(x, z))
        {
            return getTopRock(x, z);
        }
        else
        {
            return getBottomRock(x, z);
        }
    }

    public int getRockHeight(int x, int z)
    {
        return rockLayerHeight[(x & 15) + 16 * (z & 15)];
    }

    /**
     * Used as decoration rock in some biomes
     * Sand color used as "inland" sand color (deserts)
     */
    public Rock getTopRock(int x, int z)
    {
        return topLayer[(x & 15) + 16 * (z & 15)];
    }

    /**
     * Actually functions as the topmost rock in most biomes
     * Sand color used as the "water" sand color (beaches, oceans, rivers)
     */
    public Rock getMidRock(int x, int z)
    {
        return middleLayer[(x & 15) + 16 * (z & 15)];
    }

    /**
     * Base rock, does what it says on the tin.
     * Sand color is unused.
     */
    public Rock getBottomRock(int x, int z)
    {
        return bottomLayer[(x & 15) + 16 * (z & 15)];
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

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
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
            Helpers.createArrayFromBytes(nbt.getByteArray("topLayer"), topLayer, uniqueRocks::get);
        }
    }

}
