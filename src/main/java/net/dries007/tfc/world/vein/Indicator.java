package net.dries007.tfc.world.vein;

import java.util.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.BlockState;
import net.minecraft.util.JSONUtils;

import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.util.json.TFCJSONUtils;

public class Indicator
{
    private final int maxDepth;
    private final int rarity;
    private final boolean ignoreLiquids;

    private final IWeighted<BlockState> states;
    private final Set<BlockState> underStates;

    public Indicator(int maxDepth, int rarity, boolean ignoreLiquids, IWeighted<BlockState> states, List<BlockState> underStates)
    {
        this.maxDepth = maxDepth;
        this.rarity = rarity;
        this.ignoreLiquids = ignoreLiquids;
        this.states = states;
        this.underStates = new HashSet<>(underStates);
    }

    public BlockState getStateToGenerate(Random random)
    {
        return states.get(random);
    }

    public boolean validUnderState(BlockState state)
    {
        return underStates.isEmpty() || underStates.contains(state);
    }

    public int getMaxDepth()
    {
        return maxDepth;
    }

    public int getRarity()
    {
        return rarity;
    }

    public boolean shouldIgnoreLiquids()
    {
        return ignoreLiquids;
    }

    public enum Serializer
    {
        INSTANCE;

        public Indicator read(JsonElement element)
        {
            JsonObject obj = JSONUtils.getJsonObject(element, "indicator");
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
            IWeighted<BlockState> states = TFCJSONUtils.getWeighted(obj.get("blocks"), TFCJSONUtils::getBlockState);
            if (states.isEmpty())
            {
                throw new JsonParseException("Block states cannot be empty!");
            }
            // todo: replace with block ingredients
            //List<BlockState> underStates = obj.has("blocks_under") ? context.deserialize(obj.get("blocks_under"), new TypeToken<List<BlockState>>() {}.getType()) : Collections.emptyList();
            return new Indicator(maxDepth, rarity, ignoreLiquids, states, Collections.emptyList());
        }
    }
}
