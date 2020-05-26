/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.vein;

import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;

import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

public class TemperatureRule implements IVeinRule
{
    private final float minimum, maximum;

    public TemperatureRule(JsonObject json)
    {
        minimum = JSONUtils.getFloat(json, "minimum", 0);
        maximum = JSONUtils.getFloat(json, "maximum", 500);
    }

    @Override
    public boolean test(IWorld world, ChunkPos pos)
    {
        return ChunkDataProvider.get(world).map(provider -> {
            float temperature = provider.get(pos, ChunkData.Status.CLIMATE, false).getAverageTemp();
            return temperature >= minimum && temperature <= maximum;
        }).orElse(true);
    }
}
