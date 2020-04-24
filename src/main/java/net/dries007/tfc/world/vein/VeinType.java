package net.dries007.tfc.world.vein;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.BlockState;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.util.IResourceNameable;
import net.dries007.tfc.util.collections.IWeighted;

public abstract class VeinType<V extends Vein<?>> implements IResourceNameable
{
    protected final IWeighted<Indicator> indicator;
    protected final int size;
    protected final float density;
    protected final int rarity;
    protected final int minY;
    protected final int maxY;
    protected final Map<BlockState, IWeighted<BlockState>> blocks;
    private ResourceLocation id;

    public VeinType(JsonObject json, JsonDeserializationContext context)
    {
        rarity = JSONUtils.getInt(json, "rarity", 10);
        if (rarity <= 0)
        {
            throw new JsonParseException("Rarity must be > 0.");
        }
        minY = JSONUtils.getInt(json, "min_y", 16);
        maxY = JSONUtils.getInt(json, "max_y", 128);
        if (minY < 0 || maxY > 256 || minY > maxY)
        {
            throw new JsonParseException("Min Y and Max Y must be within [0, 256], and Min Y must be <= Max Y.");
        }
        size = JSONUtils.getInt(json, "size", 8);
        if (size <= 0)
        {
            throw new JsonParseException("Vertical Size must be > 0.");
        }
        density = JSONUtils.getInt(json, "density", 20);
        if (density <= 0)
        {
            throw new JsonParseException("Density must be > 0.");
        }

        blocks = new HashMap<>();
        JsonArray blocksJson = JSONUtils.getJsonArray(json, "blocks");
        for (JsonElement blocksElement : blocksJson)
        {
            // Parse each element of blocks
            JsonObject blockJson = JSONUtils.getJsonObject(blocksElement, "blocks");
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
        indicator = json.has("indicator") ? context.deserialize(json.get("indicator"), new TypeToken<IWeighted<Indicator>>() {}.getType()) : IWeighted.empty();
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public void setId(ResourceLocation id)
    {
        this.id = id;
    }

    public int getRarity()
    {
        return rarity;
    }

    public int getMinY()
    {
        return minY;
    }

    public int getMaxY()
    {
        return maxY;
    }

    /**
     * Gets an indicator for this vein type
     *
     * @param random A random to use to select an indicator
     * @return An Indicator if it exists, or null if not
     */
    @Nullable
    public Indicator getIndicator(Random random)
    {
        return indicator != null ? indicator.get(random) : null;
    }

    public int getChunkRadius()
    {
        return 1 + (size >> 4);
    }

    public boolean isValidState(BlockState state)
    {
        return blocks.containsKey(state);
    }

    public BlockState getStateToGenerate(BlockState stoneState, Random random)
    {
        return blocks.get(stoneState).get(random);
    }

    public Collection<BlockState> getOreStates()
    {
        return blocks.values().stream().flatMap(weighted -> weighted.values().stream()).collect(Collectors.toList());
    }

    /**
     * Creates an instance of a vein
     *
     * @param chunkXStart The chunk X start
     * @param chunkZStart The chunk Z start
     * @param rand        a random to use in generation
     * @return a new vein instance
     */
    public abstract V createVein(int chunkXStart, int chunkZStart, Random rand);

    /**
     * Checks if the vein is in range of a point.
     *
     * @param x 0-centered x position
     * @param z 0-centered z position
     * @return if the vein can generate at any y position in that column
     */
    public abstract boolean inRange(V vein, int x, int z);

    /**
     * Gets the chance to generate at a position
     * This should typically call {@code getType().getChanceToGenerate()}
     *
     * @param x 0-centered x position
     * @param y 0-centered y position
     * @param z 0-centered z position
     * @return a chance, with <= 0 meaning no chance, >= 1 indicating 100% chance
     */
    public abstract float getChanceToGenerate(V vein, int x, int y, int z);

    protected int defaultYPos(int verticalShrinkRange, Random rand)
    {
        int actualRange = maxY - minY - 2 * verticalShrinkRange;
        int yPos;
        if (actualRange > 0)
        {
            yPos = minY + verticalShrinkRange + rand.nextInt(actualRange);
        }
        else
        {
            yPos = (minY + maxY) / 2;
        }
        return yPos;
    }
}
