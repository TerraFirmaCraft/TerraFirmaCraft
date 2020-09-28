/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.flora;

import java.util.Random;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.BlockState;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;

import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.util.json.TFCJSONUtils;

public class PlantFloraType extends FloraType
{
    protected final float density;
    private final IWeighted<BlockState> blocks;

    public PlantFloraType(ResourceLocation id, JsonObject json)
    {
        super(id, json);
        JsonElement blocksElement = json.get("blocks");
        if (blocksElement == null)
        {
            throw new JsonParseException("Missing json element blocks");
        }
        blocks = TFCJSONUtils.getWeighted(blocksElement, TFCJSONUtils::getBlockState);
        if (blocks.isEmpty())
        {
            throw new JsonParseException("Block states cannot be empty.");
        }
        density = JSONUtils.getAsInt(json, "density", 20);
        if (density <= 0)
        {
            throw new JsonParseException("Density must be higher than 0.");
        }
    }

    @Override
    public void generate(IWorld world, BlockPos chunkStart, Random random)
    {
        for (int i = 0; i < density; i++)
        {
            int x = chunkStart.getX() + random.nextInt(16);
            int z = chunkStart.getZ() + random.nextInt(16);
            BlockState state = blocks.get(random);
            BlockPos aboveGround = world.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, new BlockPos(x, 0, z));
            if (world.getBlockState(aboveGround).getMaterial().isReplaceable() && state.canSurvive(world, aboveGround))
            {
                world.setBlock(aboveGround, state, 3);
            }
        }
    }
}
