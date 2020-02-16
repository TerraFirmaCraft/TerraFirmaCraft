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
import net.dries007.tfc.objects.blocks.soil.SandBlockType;
import net.dries007.tfc.objects.blocks.soil.SoilBlockType;
import net.dries007.tfc.types.TFCTypeManager;

public class RockData implements INBTSerializable<CompoundNBT>
{
    private Rock[] bottomLayer, topLayer;
    private SoilBlockType[] soilLayer;
    private SandBlockType[] sandLayer;

    public RockData(Rock[] bottomLayer, Rock[] topLayer, SoilBlockType[] soilLayer, SandBlockType[] sandLayer)
    {
        this.bottomLayer = bottomLayer;
        this.topLayer = topLayer;
        this.soilLayer = soilLayer;
        this.sandLayer = sandLayer;
    }

    public Rock getTopRock(int x, int z)
    {
        return topLayer[x + 16 * z];
    }

    public Rock getBottomRock(int x, int z)
    {
        return bottomLayer[x + 16 * z];
    }

    public SoilBlockType getSoil(int x, int z)
    {
        return soilLayer[x + 16 * z];
    }

    public SandBlockType getSand(int x, int z)
    {
        return sandLayer[x + 16 * z];
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();

        // Record a map from bytes -> rocks (pallet, similar to vanilla world save format)
        List<Rock> uniqueRocks = Stream.of(bottomLayer, topLayer).flatMap(Arrays::stream).distinct().collect(Collectors.toList());
        ListNBT pallet = new ListNBT();
        byte index = 0;
        for (Rock rock : uniqueRocks)
        {
            pallet.add(index, new StringNBT(rock.getName().toString()));
        }
        nbt.put("pallet", pallet);

        // Record the raw byte values
        nbt.putByteArray("bottomLayer", createByteArray(topLayer, r -> (byte) uniqueRocks.indexOf(r)));
        nbt.putByteArray("topLayer", createByteArray(bottomLayer, r -> (byte) uniqueRocks.indexOf(r)));

        // Record byte values for soil and sand
        nbt.putByteArray("soilLayer", createByteArray(soilLayer, e -> (byte) e.ordinal()));
        nbt.putByteArray("sandLayer", createByteArray(sandLayer, e -> (byte) e.ordinal()));

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

            createArrayFromBytes(nbt.getByteArray("bottomLayer"), bottomLayer, uniqueRocks::get);
            createArrayFromBytes(nbt.getByteArray("topLayer"), topLayer, uniqueRocks::get);

            createArrayFromBytes(nbt.getByteArray("soilLayer"), soilLayer, SoilBlockType::valueOf);
            createArrayFromBytes(nbt.getByteArray("sandLayer"), sandLayer, SandBlockType::valueOf);
        }
    }

    private <T> byte[] createByteArray(T[] array, ToByteFunction<T> byteConverter)
    {
        byte[] bytes = new byte[array.length];
        for (int i = 0; i < array.length; i++)
        {
            bytes[i] = byteConverter.get(array[i]);
        }
        return bytes;
    }

    private <T> void createArrayFromBytes(byte[] byteArray, T[] array, FromByteFunction<T> byteConverter)
    {
        for (int i = 0; i < byteArray.length; i++)
        {
            array[i] = byteConverter.get(byteArray[i]);
        }
    }

    @FunctionalInterface
    interface ToByteFunction<T>
    {
        byte get(T t);
    }

    @FunctionalInterface
    interface FromByteFunction<T>
    {
        T get(byte b);
    }
}
