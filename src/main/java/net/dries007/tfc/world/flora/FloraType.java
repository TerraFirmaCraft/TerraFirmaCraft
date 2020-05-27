/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.flora;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.world.biome.TFCBiome;
import net.dries007.tfc.world.biome.TFCBiomes;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public abstract class FloraType
{
    private final ResourceLocation id;
    private final Set<Biome> biomes;
    private final int rarity;

    public FloraType(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        biomes = new HashSet<>();
        JsonArray biomesArray = JSONUtils.getJsonArray(json, "biomes");
        for (JsonElement biomeJson : biomesArray)
        {
            ResourceLocation biomeId = new ResourceLocation(biomeJson.getAsString());
            Biome biome = ForgeRegistries.BIOMES.getValue(biomeId);
            if (biome == null)
            {
                boolean error = true;
                if (biomeId.getNamespace().equals(MOD_ID))
                {
                    // Search for TFC variants
                    for (TFCBiome biomeVariant : TFCBiomes.getBiomeVariants(biomeId.getPath()))
                    {
                        error = false;
                        biomes.add(biomeVariant);
                    }
                }
                if (error)
                {
                    throw new JsonParseException("Invalid biome specified: " + biomeId);
                }
            }
            else
            {
                biomes.add(biome);
            }
        }
        rarity = JSONUtils.getInt(json, "rarity");
        if (rarity <= 0)
        {
            throw new JsonParseException("Rarity must be higher than 0.");
        }
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public boolean isValidBiome(Biome biome)
    {
        return biomes.contains(biome);
    }

    public abstract void generate(IWorld world, BlockPos chunkStart, Random random);

    public int getRarity()
    {
        return rarity;
    }
}
