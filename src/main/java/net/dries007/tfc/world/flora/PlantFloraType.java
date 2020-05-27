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
    protected final int size;
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
        size = JSONUtils.getInt(json, "size", 8);
        if (size <= 0 || size > 16)
        {
            throw new JsonParseException("Size must be in [1, 16].");
        }
        density = JSONUtils.getInt(json, "density", 20) / 100f;
        if (density <= 0 || density > 1)
        {
            throw new JsonParseException("Density must be in [1, 100]");
        }
    }

    @Override
    public void generate(IWorld world, BlockPos chunkStart, Random random)
    {
        int minX = chunkStart.getX();
        int maxX = chunkStart.getX() + 15;
        int minZ = chunkStart.getZ();
        int maxZ = chunkStart.getZ() + 15;
        // Larger sizes are kept more to the center, so it will fully generate
        int centerX = minX + 8 - random.nextInt(8 - (size - 1) / 2) + random.nextInt(8 - (size - 1) / 2);
        int centerZ = minZ + 8 - random.nextInt(8 - (size - 1) / 2) + random.nextInt(8 - (size - 1) / 2);
        for (int x = Math.max(minX, centerX - size / 2); x < Math.min(maxX, centerX + size / 2); x++)
        {
            for (int z = Math.max(minZ, centerZ - size / 2); z < Math.min(maxZ, centerZ + size / 2); z++)
            {
                if (random.nextDouble() < density)
                {
                    BlockState state = blocks.get(random);
                    BlockPos aboveGround = world.getHeight(Heightmap.Type.WORLD_SURFACE, new BlockPos(x, 0, z));
                    if (world.getBlockState(aboveGround).getMaterial().isReplaceable() && state.isValidPosition(world, aboveGround))
                    {
                        world.setBlockState(aboveGround, state, 3);
                    }
                }
            }
        }
    }
}
