/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.placement;

import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import net.dries007.tfc.world.chunkdata.ChunkData;

public class RainfallRule implements IPlacementRule
{
    private final float minimum, maximum;

    public RainfallRule(JsonObject json)
    {
        minimum = JSONUtils.getFloat(json, "minimum", 0);
        maximum = JSONUtils.getFloat(json, "maximum", 500);
    }

    @Override
    public boolean test(IWorld world, BlockPos pos)
    {
        float rainfall = ChunkData.get(world, pos, ChunkData.Status.CLIMATE, false).getRainfall(pos);
        return rainfall >= minimum && rainfall <= maximum;
    }
}
