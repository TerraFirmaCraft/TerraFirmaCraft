/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.rock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistryEntry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Rock;

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

        // Record the raw int values
        nbt.putIntArray("bottomLayer", Arrays.stream(bottomLayer).mapToInt(TFCRegistries.ROCKS::getID).toArray());
        nbt.putIntArray("middleLayer", Arrays.stream(middleLayer).mapToInt(TFCRegistries.ROCKS::getID).toArray());
        nbt.putIntArray("topLayer", Arrays.stream(topLayer).mapToInt(TFCRegistries.ROCKS::getID).toArray());

        // Record a map from registry name -> int for loading, since int ids aren't stable
        Set<ResourceLocation> uniqueRocks = Stream.of(bottomLayer, middleLayer, topLayer).flatMap(Arrays::stream).map(ForgeRegistryEntry::getRegistryName).collect(Collectors.toSet());
        ListNBT pallet = new ListNBT();
        int index = 0;
        for (ResourceLocation rock : uniqueRocks)
        {
            CompoundNBT entry = new CompoundNBT();
            entry.putInt("key", TFCRegistries.ROCKS.getID(rock));
            entry.putString("val", rock.toString());
            pallet.add(index++, entry);
        }
        nbt.put("pallet", pallet);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        if (nbt != null)
        {
            // Build pallet
            ListNBT pallet = nbt.getList("pallet", 10);
            Map<Integer, Rock> palletMap = new HashMap<>();
            for (int i = 0; i < pallet.size(); i++)
            {
                CompoundNBT entry = pallet.getCompound(i);
                palletMap.put(entry.getInt("key"), TFCRegistries.ROCKS.getValue(new ResourceLocation(entry.getString("val"))));
            }

            bottomLayer = Arrays.stream(nbt.getIntArray("bottomLayer")).mapToObj(palletMap::get).toArray(Rock[]::new);
            middleLayer = Arrays.stream(nbt.getIntArray("middleLayer")).mapToObj(palletMap::get).toArray(Rock[]::new);
            topLayer = Arrays.stream(nbt.getIntArray("topLayer")).mapToObj(palletMap::get).toArray(Rock[]::new);
        }
    }
}
