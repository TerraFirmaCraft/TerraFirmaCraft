/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.rock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.types.TFCTypeManager;

public class RockData implements INBTSerializable<CompoundNBT>
{
    private Rock[] bottomLayer;
    private Rock[] middleLayer;
    private Rock[] topLayer;

    public RockData(Rock[] bottomLayer, Rock[] middleLayer, Rock[] topLayer)
    {
        this.bottomLayer = bottomLayer;
        this.middleLayer = middleLayer;
        this.topLayer = topLayer;
    }

    public Rock getTopLayer(int x, int z)
    {
        return topLayer[x + 16 * z];
    }

    public Rock getMiddleLayer(int x, int z)
    {
        return middleLayer[x + 16 * z];
    }

    public Rock getBottomLayer(int x, int z)
    {
        return bottomLayer[x + 16 * z];
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();

        // Record a map from bytes -> rocks (pallet, similar to vanilla world save format)
        List<Rock> uniqueRocks = Stream.of(bottomLayer, middleLayer, topLayer).flatMap(Arrays::stream).distinct().collect(Collectors.toList());
        ListNBT pallet = new ListNBT();
        byte index = 0;
        for (Rock rock : uniqueRocks)
        {
            pallet.add(index, new StringNBT(rock.getId().toString()));
        }
        nbt.put("pallet", pallet);

        // Record the raw byte values
        nbt.putByteArray("bottomLayer", createRockByteArray(topLayer, uniqueRocks));
        nbt.putByteArray("middleLayer", createRockByteArray(middleLayer, uniqueRocks));
        nbt.putByteArray("topLayer", createRockByteArray(bottomLayer, uniqueRocks));

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        if (nbt != null)
        {
            // Build pallet
            ListNBT pallet = nbt.getList("pallet", 8 /* String */);
            List<Rock> uniqueRocks = new ArrayList<>(pallet.size());
            for (int i = 0; i < pallet.size(); i++)
            {
                uniqueRocks.add(TFCTypeManager.ROCKS.get(new ResourceLocation(pallet.getString(i))));
            }

            bottomLayer = createRockArray(nbt.getByteArray("bottomLayer"), uniqueRocks);
            middleLayer = createRockArray(nbt.getByteArray("middleLayer"), uniqueRocks);
            topLayer = createRockArray(nbt.getByteArray("topLayer"), uniqueRocks);
        }
    }

    private byte[] createRockByteArray(Rock[] rocks, List<Rock> uniqueRocks)
    {
        byte[] bytes = new byte[rocks.length];
        for (int i = 0; i < rocks.length; i++)
        {
            bytes[i] = (byte) uniqueRocks.indexOf(rocks[i]);
        }
        return bytes;
    }

    private Rock[] createRockArray(byte[] bytes, List<Rock> pallet)
    {
        Rock[] rocks = new Rock[bytes.length];
        for (int i = 0; i < bytes.length; i++)
        {
            rocks[i] = pallet.get(bytes[i]);
        }
        return rocks;
    }
}
