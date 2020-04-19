package net.dries007.tfc.objects.types;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import net.dries007.tfc.api.Rock;
import net.dries007.tfc.util.json.BlockStateDeserializer;
import net.dries007.tfc.util.json.GenericJsonReloadListener;
import net.dries007.tfc.util.json.RockDeserializer;

public class RockManager extends GenericJsonReloadListener<Rock>
{
    public static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(Rock.class, RockDeserializer.INSTANCE)
        .registerTypeAdapter(BlockState.class, BlockStateDeserializer.INSTANCE)
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create();

    public static final RockManager INSTANCE = new RockManager();

    private final Map<Block, Rock> rockBlocks;

    private RockManager()
    {
        super(GSON, "rocks", Rock.class, "rock");

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
    }
}
