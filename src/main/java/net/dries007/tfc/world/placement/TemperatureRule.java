/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.placement;

import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;

import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

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
        ChunkData chunkData = ChunkDataProvider.get(world).map(provider -> provider.get(new ChunkPos(pos), ChunkData.Status.CLIMATE)).orElseThrow(() -> new IllegalStateException("Invalid chunk data!"));
        float temperature = chunkData.getAverageTemp(pos);
        return temperature >= minimum && temperature <= maximum;
    }
}
