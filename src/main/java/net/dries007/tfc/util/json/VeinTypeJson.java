/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.json;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;

public class VeinTypeJson implements JsonDeserializer<VeinType>
{
    @SuppressWarnings("deprecation")
    @Override
    public VeinType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = JsonUtils.getJsonObject(json, "vein");

        int minY = JsonUtils.getInt(jsonObject, "minimum_height");
        int maxY = JsonUtils.getInt(jsonObject, "maximum_height");
        if (minY > maxY)
        {
            throw new JsonParseException("Minimum height cannot be greater than maximum height!");
        }
        int rarity = JsonUtils.getInt(jsonObject, "rarity");
        int density = JsonUtils.getInt(jsonObject, "density");
        int width = JsonUtils.getInt(jsonObject, "width");
        int height = JsonUtils.getInt(jsonObject, "height");

        VeinType.Shape shape = VeinType.Shape.valueOf(JsonUtils.getString(jsonObject, "shape").toUpperCase());

        JsonArray rocks = JsonUtils.getJsonArray(jsonObject, "base_rocks");
        Set<Rock> blocks = new HashSet<>();
        rocks.forEach(e -> {
            ResourceLocation name = new ResourceLocation(e.getAsString());
            Rock rock = TFCRegistries.ROCKS.getValue(name);
            if (rock == null)
            {
                RockCategory category = TFCRegistries.ROCK_CATEGORIES.getValue(name);
                if (category == null)
                {
                    TerraFirmaCraft.getLog().warn("Problem parsing ore entry '{}'. Rock / Rock Category '{}' is not defined. Skipping.", name, e);
                }
                else
                {
                    blocks.addAll(TFCRegistries.ROCKS.getValuesCollection().stream().filter(e1 -> e1.getRockCategory() == category).collect(Collectors.toList()));
                }
            }
            else
            {
                blocks.add(rock);
            }
        });

        ResourceLocation oreName = new ResourceLocation(JsonUtils.getString(jsonObject, "ore"));
        Ore ore = TFCRegistries.ORES.getValue(oreName);
        if (ore == null)
        {
            Block block = ForgeRegistries.BLOCKS.getValue(oreName);
            if (block != null)
            {
                //todo: remove metadata in 1.13
                int meta = JsonUtils.getInt(jsonObject, "meta", 0);
                IBlockState oreState;
                try
                {
                    oreState = block.getStateFromMeta(meta);
                }
                catch (RuntimeException e)
                {
                    throw new JsonParseException("Unable to find a matching IBlockState for block " + oreName + " and metadata: " + meta);
                }
                return new VeinType.CustomVeinType(oreState, blocks, shape, width, height, rarity, minY, maxY, density);
            }
            else
            {
                throw new JsonParseException("Unrecognized ore '" + oreName + "'");
            }
        }
        return new VeinType(ore, blocks, shape, width, height, rarity, minY, maxY, density);
    }
}
