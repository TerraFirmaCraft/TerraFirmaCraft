/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.json;

import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;

import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

public class RainfallRule implements IChunkRule
{
    private final float minimum, maximum;

    public RainfallRule(JsonObject json)
    {
        minimum = JSONUtils.getFloat(json, "minimum", 0);
        maximum = JSONUtils.getFloat(json, "maximum", 500);
    }

    @Override
    public boolean test(IWorld world, ChunkPos pos)
    {
        return ChunkDataProvider.get(world).map(provider -> {
            float rainfall = provider.get(pos, ChunkData.Status.CLIMATE, false).getRainfall();
            return rainfall >= minimum && rainfall <= maximum;
        }).orElse(true);
    }
}
