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

public class RainfallRule implements IPlacementRule
{
    private final float minimum, maximum;

    public RainfallRule(JsonObject json)
    {
        minimum = JSONUtils.getAsFloat(json, "minimum", 0);
        maximum = JSONUtils.getAsFloat(json, "maximum", 500);
    }

    @Override
    public boolean test(IWorld world, BlockPos pos)
    {
        ChunkData chunkData = ChunkDataProvider.get(world).map(provider -> provider.get(new ChunkPos(pos), ChunkData.Status.CLIMATE)).orElseThrow(() -> new IllegalStateException("Invalid chunk data"));
        float rainfall = chunkData.getRainfall(pos);
        return rainfall >= minimum && rainfall <= maximum;
    }
}