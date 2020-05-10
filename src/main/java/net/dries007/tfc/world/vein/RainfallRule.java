package net.dries007.tfc.world.vein;

import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;

import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

public class RainfallRule implements IVeinRule
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
            float rainfall = provider.get(pos).getRainfall();
            return rainfall >= minimum && rainfall <= maximum;
        }).orElse(true);
    }
}
