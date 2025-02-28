/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ForestType;

public class ClimatePlacement extends PlacementModifier
{
    public static final MapCodec<ClimatePlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.FLOAT.optionalFieldOf("min_temperature", Float.NEGATIVE_INFINITY).forGetter(c -> c.minTemp),
        Codec.FLOAT.optionalFieldOf("max_temperature", Float.POSITIVE_INFINITY).forGetter(c -> c.maxTemp),
        Codec.FLOAT.optionalFieldOf("min_groundwater", Float.NEGATIVE_INFINITY).forGetter(c -> c.minGroundwater),
        Codec.FLOAT.optionalFieldOf("max_groundwater", Float.POSITIVE_INFINITY).forGetter(c -> c.maxGroundwater),
        Codec.FLOAT.optionalFieldOf("min_rain_variance", -1f).forGetter(c -> c.minRainVariance),
        Codec.FLOAT.optionalFieldOf("max_rain_variance", 1f).forGetter(c -> c.maxRainVariance),
        Codec.BOOL.optionalFieldOf("rain_variance_absolute", false).forGetter(c -> c.rainVarianceAbsolute),
        Codecs.NON_NEGATIVE_INT.optionalFieldOf("min_forest", 0).forGetter(c -> c.minForest),
        Codecs.NON_NEGATIVE_INT.optionalFieldOf("max_forest", 4).forGetter(c -> c.maxForest),
        ForestType.CODEC.listOf().optionalFieldOf("forest_types", Collections.emptyList()).forGetter(c -> c.types),
        Codec.INT.optionalFieldOf("min_elevation", -64).forGetter(c -> c.minElevation),
        Codec.INT.optionalFieldOf("max_elevation", 320).forGetter(c -> c.maxElevation),
        Codec.BOOL.optionalFieldOf("fuzzy", false).forGetter(c -> c.fuzzy)
    ).apply(instance, ClimatePlacement::new));


    private final float minTemp;
    private final float maxTemp;
    private final List<ForestType> types;
    private final float targetTemp;
    private final float minGroundwater;
    private final float maxGroundwater;
    private final float targetGroundwater;
    private final float minRainVariance;
    private final float maxRainVariance;
    private final boolean rainVarianceAbsolute;
    private final float targetRainVariance;
    private final int minForest;
    private final int maxForest;
    private final int minElevation;
    private final int maxElevation;
    private final boolean fuzzy;

    public ClimatePlacement(float minTemp, float maxTemp, float minGroundwater, float maxGroundwater, float minRainVariance, float maxRainVariance, boolean rainVarianceAbsolute, int minForest, int maxForest, List<ForestType> types, int minElevation, int maxElevation, boolean fuzzy)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.types = types;
        this.targetTemp = (minTemp + maxTemp) / 2f;
        this.minGroundwater = minGroundwater;
        this.maxGroundwater = maxGroundwater;
        this.targetGroundwater = (minGroundwater + maxGroundwater) / 2f;
        this.minRainVariance = minRainVariance;
        this.maxRainVariance = maxRainVariance;
        this.targetRainVariance = (minRainVariance + maxRainVariance) / 2f;
        this.rainVarianceAbsolute = rainVarianceAbsolute;
        this.minForest = minForest;
        this.maxForest = maxForest;
        this.minElevation = minElevation;
        this.maxElevation = maxElevation;
        this.fuzzy = fuzzy;
    }

    public float getMinTemp()
    {
        return minTemp;
    }

    public float getMaxTemp()
    {
        return maxTemp;
    }

    public float getMinGroundwater()
    {
        return minGroundwater;
    }

    public float getMaxGroundwater()
    {
        return maxGroundwater;
    }

    public float getMinRainVariance()
    {
        return minRainVariance;
    }

    public float getMaxRainVariance()
    {
        return maxRainVariance;
    }

    public boolean isRainVarianceAbsolute()
    {
        return rainVarianceAbsolute;
    }

    public int getMinForest()
    {
        return minForest;
    }

    public int getMaxForest()
    {
        return maxForest;
    }

    public int getMinElevation()
    {
        return minElevation;
    }

    public int getMaxElevation()
    {
        return maxElevation;
    }

    public List<ForestType> getTypes()
    {
        return types;
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return TFCPlacements.CLIMATE.get();
    }

    public boolean isValid(ChunkData data, BlockPos pos, RandomSource random)
    {
        final int y = pos.getY();
        final float temperature = EnvironmentHelpers.adjustAvgTempForElev(y, data.getAverageSeaLevelTemp(pos));
        final float groundwater = data.getGroundwater(pos);
        final float rainVar = rainVarianceAbsolute ? Math.abs(data.getRainVariance(pos)) : data.getRainVariance(pos);
        final ForestType forestType = data.getForestType();

        //Empty list of Forest Types defaults to generating everywhere
        if (y >= minElevation && y <= maxElevation && minTemp <= temperature && temperature <= maxTemp && minGroundwater <= groundwater && groundwater <= maxGroundwater &&
            minRainVariance <= rainVar && maxRainVariance >= rainVar &&
            minForest <= forestType.getDensity() && forestType.getDensity() <= maxForest && (types.contains(forestType) || types.isEmpty()))
        {
            if (fuzzy)
            {
                float normTempDelta = Math.abs(temperature - targetTemp) / (maxTemp - minTemp);
                float normGroundwaterDelta = Math.abs(groundwater - targetGroundwater) / (maxGroundwater - minGroundwater);
                float normRainVarDelta = Math.abs(rainVar - targetRainVariance) / (maxRainVariance - minRainVariance);
                return random.nextFloat() * random.nextFloat() > Math.max(normTempDelta, Math.max(normGroundwaterDelta, normRainVarDelta));
            }
            return true;
        }
        return false;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos)
    {
        final ChunkData data = ChunkData.get(context.getLevel(), pos);
        if (isValid(data, pos, random))
        {
            return Stream.of(pos);
        }
        return Stream.empty();
    }
}
