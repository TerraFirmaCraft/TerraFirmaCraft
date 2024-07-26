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

import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ForestType;

public class ClimatePlacement extends PlacementModifier
{
    public static final MapCodec<ClimatePlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.FLOAT.optionalFieldOf("min_temperature", Float.NEGATIVE_INFINITY).forGetter(c -> c.minTemp),
        Codec.FLOAT.optionalFieldOf("max_temperature", Float.POSITIVE_INFINITY).forGetter(c -> c.maxTemp),
        Codec.FLOAT.optionalFieldOf("min_rainfall", Float.NEGATIVE_INFINITY).forGetter(c -> c.minRainfall),
        Codec.FLOAT.optionalFieldOf("max_rainfall", Float.POSITIVE_INFINITY).forGetter(c -> c.maxRainfall),
        Codecs.POSITIVE_INT.optionalFieldOf("min_forest", 0).forGetter(c -> c.minForest),
        Codecs.POSITIVE_INT.optionalFieldOf("max_forest", 4).forGetter(c -> c.maxForest),
        ForestType.CODEC.listOf().optionalFieldOf("forest_types", Collections.emptyList()).forGetter(c -> c.types),
        Codec.BOOL.optionalFieldOf("fuzzy", false).forGetter(c -> c.fuzzy)
    ).apply(instance, ClimatePlacement::new));


    private final float minTemp;
    private final float maxTemp;
    private final List<ForestType> types;
    private final float targetTemp;
    private final float minRainfall;
    private final float maxRainfall;
    private final float targetRainfall;
    private final int minForest;
    private final int maxForest;
    private final boolean fuzzy;

    public ClimatePlacement(float minTemp, float maxTemp, float minRainfall, float maxRainfall, int minForest, int maxForest, List<ForestType> types, boolean fuzzy)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.types = types;
        this.targetTemp = (minTemp + maxTemp) / 2f;
        this.minRainfall = minRainfall;
        this.maxRainfall = maxRainfall;
        this.targetRainfall = (minRainfall + maxRainfall) / 2f;
        this.minForest = minForest;
        this.maxForest = maxForest;
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

    public float getMinRainfall()
    {
        return minRainfall;
    }

    public float getMaxRainfall()
    {
        return maxRainfall;
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return TFCPlacements.CLIMATE.get();
    }

    public boolean isValid(ChunkData data, BlockPos pos, RandomSource random)
    {
        final float temperature = OverworldClimateModel.getAdjustedAverageTempByElevation(pos, data);
        final float rainfall = data.getRainfall(pos);
        final ForestType forestType = data.getForestType();

        if (minTemp <= temperature && temperature <= maxTemp && minRainfall <= rainfall && rainfall <= maxRainfall &&
            minForest <= forestType.getDensity() && forestType.getDensity() <= maxForest)
        {
            if (fuzzy)
            {
                float normTempDelta = Math.abs(temperature - targetTemp) / (maxTemp - minTemp);
                float normRainfallDelta = Math.abs(rainfall - targetRainfall) / (maxRainfall - minRainfall);
                return random.nextFloat() * random.nextFloat() > Math.max(normTempDelta, normRainfallDelta);
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
