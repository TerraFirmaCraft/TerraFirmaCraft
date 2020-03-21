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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
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
        if (rarity <= 0)
        {
            throw new JsonParseException("Rarity cannot be negative or zero!");
        }
        int density = JsonUtils.getInt(jsonObject, "density");
        if (density <= 0)
        {
            throw new JsonParseException("Density cannot be negative or zero!");
        }
        int width = JsonUtils.getInt(jsonObject, "width");
        if (width <= 0)
        {
            throw new JsonParseException("Width cannot be negative or zero!");
        }
        int height = JsonUtils.getInt(jsonObject, "height");
        if (height <= 0)
        {
            throw new JsonParseException("Height cannot be negative or zero!");
        }
        String shapeName = JsonUtils.getString(jsonObject, "shape");
        VeinType.Shape shape;

        try
        {
            shape = VeinType.Shape.valueOf(shapeName.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            // Not crash the game, just inform what is wrong
            throw new JsonParseException("No shape '" + shapeName + "' exists.");
        }

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

        // Parse loose rock / indicator
        ItemStack looseRock = ItemStack.EMPTY;
        if (JsonUtils.hasField(jsonObject, "loose"))
        {
            ResourceLocation looseResource = new ResourceLocation(JsonUtils.getString(jsonObject, "loose"));

            // try parsing small ore first
            Ore looseOre = TFCRegistries.ORES.getValue(looseResource);
            Item smallOre = null;
            if (looseOre != null)
            {
                smallOre = ItemSmallOre.get(looseOre);
            }

            if (smallOre != null)
            {
                // Found Small Ore, using that
                looseRock = new ItemStack(smallOre, 1);
            }
            else
            {
                // Try parsing item/block instead
                // todo remove meta in 1.15+
                int metaLoose = JsonUtils.getInt(jsonObject, "looseMeta", 0);
                if (ForgeRegistries.ITEMS.containsKey(looseResource))
                {
                    //noinspection ConstantConditions
                    looseRock = new ItemStack(ForgeRegistries.ITEMS.getValue(looseResource), 1, metaLoose);
                }
                else if (ForgeRegistries.BLOCKS.containsKey(looseResource))
                {
                    //noinspection ConstantConditions
                    looseRock = new ItemStack(ForgeRegistries.BLOCKS.getValue(looseResource), 1, metaLoose);
                }
                else
                {
                    throw new JsonParseException("Unable to parse loose rock " + looseResource + ". No registered small ore, item or block found.");
                }
            }
        }

        ResourceLocation oreName = new ResourceLocation(JsonUtils.getString(jsonObject, "ore"));
        Ore ore = TFCRegistries.ORES.getValue(oreName);
        if (ore == null)
        {
            if (ForgeRegistries.BLOCKS.containsKey(oreName))
            {
                Block block = ForgeRegistries.BLOCKS.getValue(oreName);
                //todo: remove metadata in 1.13
                int meta = JsonUtils.getInt(jsonObject, "meta", 0);
                IBlockState oreState;
                try
                {
                    //noinspection ConstantConditions
                    oreState = block.getStateFromMeta(meta);
                }
                catch (RuntimeException e)
                {
                    throw new JsonParseException("Unable to find a matching IBlockState for block " + oreName + " and metadata: " + meta);
                }
                return new VeinType.CustomVeinType(oreState, looseRock, blocks, shape, width, height, rarity, minY, maxY, density);
            }
            else
            {
                throw new JsonParseException("Unrecognized ore '" + oreName + "'");
            }
        }
        return new VeinType(ore, looseRock, blocks, shape, width, height, rarity, minY, maxY, density);
    }
}
