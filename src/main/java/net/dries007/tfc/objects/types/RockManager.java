package net.dries007.tfc.objects.types;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.Rock;
import net.dries007.tfc.util.data.DataManager;

public class RockManager extends DataManager<Rock>
{
    public static final RockManager INSTANCE = new RockManager();

    private final Map<Block, Rock> rockBlocks;

    private RockManager()
    {
        super(new GsonBuilder().create(), "rocks", "rock");

        rockBlocks = new HashMap<>();
    }

    /**
     * Gets the rock from a block from a O(1) lookup, rather than iterating the list of rocks
     */
    @Nullable
    public Rock getRock(Block block)
    {
        return rockBlocks.get(block);
    }

    @Override
    protected Rock read(ResourceLocation id, JsonObject obj)
    {
        return new Rock(id, obj);
    }

    @Override
    protected void postProcess()
    {
        rockBlocks.clear();
        for (Rock rock : types.values())
        {
            for (Rock.BlockType blockType : Rock.BlockType.values())
            {
                rockBlocks.put(rock.getBlock(blockType), rock);
            }
        }

        super.postProcess();

        if (getValues().isEmpty())
        {
            throw new IllegalStateException("Something went badly wrong... There are no rocks. This cannot be.");
        }
    }
}
