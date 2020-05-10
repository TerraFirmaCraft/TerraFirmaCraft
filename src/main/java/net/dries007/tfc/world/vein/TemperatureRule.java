package net.dries007.tfc.world.vein;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;

import net.dries007.tfc.util.function.ToFloatFunction;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

public class TemperatureRule implements IVeinRule
{
    private final float minimum, maximum;
    private final ToFloatFunction<ChunkData> temperatureAccessor;

    public TemperatureRule(JsonObject json)
    {
        minimum = JSONUtils.getFloat(json, "minimum", 0);
        maximum = JSONUtils.getFloat(json, "maximum", 500);
        String temperatureType = JSONUtils.getString(json, "temperature_type", "average");
        if ("average".equals(temperatureType))
        {
            temperatureAccessor = ChunkData::getAvgTemp;
        }
        else if ("regional".equals(temperatureType))
        {
            temperatureAccessor = ChunkData::getRegionalTemp;
        }
        else
        {
            throw new JsonParseException("Unknown temperature type " + temperatureType);
        }
    }

    @Override
    public boolean test(IWorld world, ChunkPos pos)
    {
        return ChunkDataProvider.get(world).map(provider -> {
            float temperature = temperatureAccessor.applyAsFloat(provider.get(pos));
            return temperature >= minimum && temperature <= maximum;
        }).orElse(true);
    }
}
