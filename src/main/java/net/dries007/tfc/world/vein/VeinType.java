/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.vein;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.BlockState;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;

import net.dries007.tfc.objects.recipes.IBlockIngredient;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.util.json.TFCJSONUtils;

public abstract class VeinType<V extends Vein<?>>
{
    protected final IWeighted<Indicator> indicator;
    protected final int size;
    protected final float density;
    protected final int rarity;
    protected final int minY;
    protected final int maxY;
    protected final Map<IBlockIngredient, IWeighted<BlockState>> blocks;
    protected final List<IVeinRule> rules;
    private final ResourceLocation id;

    public VeinType(ResourceLocation id, JsonObject json)
    {
        this.id = id;
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
        density = JSONUtils.getInt(json, "density", 20) / 100f;
        if (density <= 0 || density > 100)
        {
            throw new JsonParseException("Density must be in [1, 100]");
        }

        blocks = new HashMap<>();
        JsonArray blocksJson = JSONUtils.getJsonArray(json, "blocks");
        for (JsonElement blocksElement : blocksJson)
        {
            // Parse each element of blocks
            JsonObject blockJson = JSONUtils.getJsonObject(blocksElement, "blocks");
            IBlockIngredient stoneStates = IBlockIngredient.Serializer.INSTANCE.read(blockJson.get("stone"));
            IWeighted<BlockState> oreStates = TFCJSONUtils.getWeighted(blockJson.get("ore"), TFCJSONUtils::getBlockState);
            if (oreStates.isEmpty())
            {
                throw new JsonParseException("Ore states cannot be empty.");
            }

            blocks.put(stoneStates, oreStates);
        }
        indicator = json.has("indicator") ? TFCJSONUtils.getWeighted(json.get("indicator"), Indicator.Serializer.INSTANCE::read) : IWeighted.empty();
        rules = json.has("rules") ? TFCJSONUtils.getListLenient(json.get("rules"), IVeinRule.Serializer.INSTANCE::read) : Collections.emptyList();
    }

    public ResourceLocation getId()
    {
        return id;
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

    public Optional<BlockState> getStateToGenerate(BlockState stoneState, Random random)
    {
        return blocks.entrySet().stream().filter(entry -> entry.getKey().test(stoneState)).map(entry -> entry.getValue().get(random)).findFirst();
    }

    public Collection<BlockState> getOreStates()
    {
        return blocks.values().stream().flatMap(weighted -> weighted.values().stream()).collect(Collectors.toList());
    }

    public boolean canGenerateVein(IWorld world, ChunkPos pos)
    {
        for (IVeinRule rule : rules)
        {
            if (!rule.test(world, pos))
            {
                return false;
            }
        }
        return true;
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
