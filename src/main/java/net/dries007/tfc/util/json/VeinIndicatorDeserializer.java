package net.dries007.tfc.util.json;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.BlockState;
import net.minecraft.util.JSONUtils;

import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.vein.Indicator;

public enum VeinIndicatorDeserializer implements JsonDeserializer<Indicator>
{
    INSTANCE;

    @Override
    public Indicator deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        if (!json.isJsonObject())
        {
            throw new JsonParseException("Indicator must be a JSON Object");
        }
        JsonObject obj = json.getAsJsonObject();
        int maxDepth = JSONUtils.getInt(obj, "max_depth", 32);
        if (maxDepth <= 0)
        {
            throw new JsonParseException("Max depth must be > 0");
        }
        int rarity = JSONUtils.getInt(obj, "rarity", 10);
        if (rarity <= 0)
        {
            throw new JsonParseException("Rarity must be > 0");
        }
        boolean ignoreLiquids = JSONUtils.getBoolean(obj, "ignore_liquids", false);
        IWeighted<BlockState> states = context.deserialize(obj.get("blocks"), new TypeToken<IWeighted<BlockState>>() {}.getType());
        if (states.isEmpty())
        {
            throw new JsonParseException("Block states cannot be empty!");
        }
        List<BlockState> underStates = obj.has("blocks_under") ? context.deserialize(obj.get("blocks_under"), new TypeToken<List<BlockState>>() {}.getType()) : Collections.emptyList();
        return new Indicator(maxDepth, rarity, ignoreLiquids, states, underStates);
    }
}
