package net.dries007.tfc.util.json;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.api.Rock;
import net.dries007.tfc.api.RockCategory;
import net.dries007.tfc.util.Helpers;

public enum RockDeserializer implements JsonDeserializer<Rock>
{
    INSTANCE;

    @Override
    public Rock deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject obj = JSONUtils.getJsonObject(json, "rock");

        // Rock category
        String rockCategoryName = JSONUtils.getString(obj, "category");
        RockCategory category;
        try
        {
            category = RockCategory.valueOf(rockCategoryName.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            throw new JsonParseException("Unknown rock category " + rockCategoryName);
        }

        // Rock blocks
        Map<Rock.BlockType, Block> blockVariants = Helpers.findRegistryObjects(obj, "blocks", ForgeRegistries.BLOCKS, Arrays.asList(Rock.BlockType.values()), type -> type.name().toLowerCase());
        return new Rock(category, blockVariants);
    }
}
