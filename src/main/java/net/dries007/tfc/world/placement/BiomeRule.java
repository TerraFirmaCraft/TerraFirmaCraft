/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.placement;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.world.biome.TFCBiomes;

public class BiomeRule implements IPlacementRule
{
    private final Set<Biome> biomes;

    public BiomeRule(JsonObject json)
    {
        biomes = new HashSet<>();
        JsonArray array = JSONUtils.getAsJsonArray(json, "list");
        for (JsonElement element : array)
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();
                if (obj.has("tag"))
                {
                    // todo BiomeDictionary was removed
                    //types.add(BiomeDictionary.Type.getType(obj.get("tag").getAsString()));
                }
                else if (obj.has("biome"))
                {
                    ResourceLocation id = new ResourceLocation(obj.get("biome").getAsString());
                    addBiome(id);
                }
                else if (obj.has("terrain"))
                {
                    String baseName = obj.get("terrain").getAsString();
                    // Search for TFC variants
                    biomes.addAll(TFCBiomes.BIOMES.getEntries().stream()
                        .filter(biome -> biome.getId().getPath().startsWith(baseName))
                        .map(RegistryObject::get)
                        .collect(Collectors.toList()));
                }
            }
            else if (element.isJsonPrimitive())
            {
                ResourceLocation id = new ResourceLocation(element.getAsString());
                addBiome(id);
            }
        }
    }

    @Override
    public boolean test(IWorld world, BlockPos pos)
    {
        return biomes.contains(world.getBiome(pos));
    }

    private void addBiome(ResourceLocation id)
    {
        Biome biome = ForgeRegistries.BIOMES.getValue(id);
        if (biome == null)
        {
            throw new JsonParseException("Invalid biome specified: " + id);
        }
        else
        {
            biomes.add(biome);
        }
    }
}
