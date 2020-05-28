/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.json;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.world.biome.TFCBiome;
import net.dries007.tfc.world.biome.TFCBiomes;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class BiomeRule implements IChunkRule
{
    private final Set<Biome> biomes;
    private final Set<BiomeDictionary.Type> types;

    public BiomeRule(JsonObject json)
    {
        biomes = new HashSet<>();
        types = new HashSet<>();
        JsonArray array = JSONUtils.getJsonArray(json, "list");
        for (JsonElement element : array)
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();
                if (obj.has("tag"))
                {
                    types.add(BiomeDictionary.Type.getType(obj.get("tag").getAsString()));
                }
                else if (obj.has("biome"))
                {
                    ResourceLocation id = new ResourceLocation(obj.get("biome").getAsString());
                    addBiome(id);
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
    public boolean test(IWorld world, ChunkPos pos)
    {
        Biome biome = world.getBiome(pos.asBlockPos());
        return biomes.contains(biome) || BiomeDictionary.getTypes(biome).stream().anyMatch(types::contains);
    }

    private void addBiome(ResourceLocation id)
    {

        Biome biome = ForgeRegistries.BIOMES.getValue(id);
        if (biome == null)
        {
            boolean error = true;
            if (id.getNamespace().equals(MOD_ID))
            {
                // Search for TFC variants
                for (TFCBiome biomeVariant : TFCBiomes.getBiomeVariants(id.getPath()))
                {
                    error = false;
                    biomes.add(biomeVariant);
                }
            }
            if (error)
            {
                throw new JsonParseException("Invalid biome specified: " + id);
            }
        }
        else
        {
            biomes.add(biome);
        }
    }
}
