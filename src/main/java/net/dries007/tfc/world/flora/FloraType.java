/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.flora;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;

import net.dries007.tfc.util.json.IChunkRule;
import net.dries007.tfc.util.json.TFCJSONUtils;

public abstract class FloraType
{
    protected final ResourceLocation id;
    protected final List<IChunkRule> rules;
    protected final int rarity;

    public FloraType(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        rarity = JSONUtils.getInt(json, "rarity");
        if (rarity <= 0)
        {
            throw new JsonParseException("Rarity must be higher than 0.");
        }
        rules = json.has("rules") ? TFCJSONUtils.getListLenient(json.get("rules"), IChunkRule.Serializer.INSTANCE::read) : Collections.emptyList();
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public boolean canGenerate(IWorld world, ChunkPos pos)
    {
        for (IChunkRule rule : rules)
        {
            if (!rule.test(world, pos))
            {
                return false;
            }
        }
        return true;
    }

    public abstract void generate(IWorld world, BlockPos chunkStart, Random random);

    public int getRarity()
    {
        return rarity;
    }
}
