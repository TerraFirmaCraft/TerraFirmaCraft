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

public class TemperatureRule implements IPlacementRule
{
    private final float minimum, maximum;

    public TemperatureRule(JsonObject json)
    {
        minimum = JSONUtils.getFloat(json, "minimum", Float.MIN_VALUE);
        maximum = JSONUtils.getFloat(json, "maximum", Float.MAX_VALUE);
    }

    @Override
    public boolean test(IWorld world, BlockPos pos)
    {
        float temperature = ChunkData.get(world, pos, ChunkData.Status.CLIMATE, false).getAverageTemp(pos);
        return temperature >= minimum && temperature <= maximum;
    }
}
