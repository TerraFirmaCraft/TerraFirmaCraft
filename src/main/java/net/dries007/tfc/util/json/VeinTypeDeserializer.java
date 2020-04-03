package net.dries007.tfc.util.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.BlockState;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.vein.VeinType;

public enum VeinTypeDeserializer implements JsonDeserializer<VeinType>
{
    INSTANCE;

    @Override
    public VeinType deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject json = element.getAsJsonObject();

        ResourceLocation groupName = null;
        int groupWeight = 0;
        if (json.has("group"))
        {
            JsonObject groupJson = JSONUtils.getJsonObject(json, "group");
            groupName = new ResourceLocation(JSONUtils.getString(groupJson, "name"));
            groupWeight = JSONUtils.getInt(groupJson, "weight");
        }

        int rarity = JSONUtils.getInt(json, "rarity", 10);
        if (rarity <= 0)
        {
            throw new JsonParseException("Rarity must be > 0.");
        }
        int minY = JSONUtils.getInt(json, "min_y", 16);
        int maxY = JSONUtils.getInt(json, "max_y", 128);
        if (minY < 0 || maxY > 256 || minY > maxY)
        {
            throw new JsonParseException("Min Y and Max Y must be within [0, 256], and Min Y must be <= Max Y.");
        }
        int verticalSize = JSONUtils.getInt(json, "vertical_size", 8);
        if (verticalSize <= 0)
        {
            throw new JsonParseException("Vertical Size must be > 0.");
        }
        int horizontalSize = JSONUtils.getInt(json, "horizontal_size", 15);
        if (horizontalSize <= 0)
        {
            throw new JsonParseException("Horizontal Size must be > 0.");
        }
        int density = JSONUtils.getInt(json, "density", 20);
        if (density <= 0)
        {
            throw new JsonParseException("Density must be > 0.");
        }

        Map<BlockState, IWeighted<BlockState>> blocks = new HashMap<>();
        JsonArray blocksJson = JSONUtils.getJsonArray(json, "blocks");
        for (JsonElement blocksElement : blocksJson)
        {
            // Parse each element of blocks
            JsonObject blockJson = blocksElement.getAsJsonObject();
            List<BlockState> stoneStates = context.deserialize(blockJson.get("stone"), new TypeToken<List<BlockState>>() {}.getType());
            if (stoneStates.isEmpty())
            {
                throw new JsonParseException("Stone states cannot be empty.");
            }
            IWeighted<BlockState> oreStates = context.deserialize(blockJson.get("ore"), new TypeToken<IWeighted<BlockState>>() {}.getType());
            if (oreStates.isEmpty())
            {
                throw new JsonParseException("Ore states cannot be empty.");
            }

            for (BlockState stoneState : stoneStates)
            {
                blocks.put(stoneState, oreStates);
            }

        }
        IWeighted<VeinType.Indicator> indicator = json.has("indicator") ? context.deserialize(json.get("indicator"), new TypeToken<IWeighted<VeinType.Indicator>>() {}.getType()) : IWeighted.empty();

        return new VeinType(groupName, groupWeight, blocks, indicator, rarity, minY, maxY, verticalSize, horizontalSize, density);
    }
}
