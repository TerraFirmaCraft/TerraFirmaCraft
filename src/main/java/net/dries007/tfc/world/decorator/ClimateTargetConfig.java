package net.dries007.tfc.world.decorator;

import net.minecraft.world.gen.placement.IPlacementConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.chunkdata.ForestType;

public class ClimateTargetConfig implements IPlacementConfig
{
    public static final Codec<ClimateTargetConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("temperature").forGetter(c -> c.temp),
        Codec.FLOAT.fieldOf("rainfall").forGetter(c -> c.rain),
        Codecs.POSITIVE_INT.fieldOf("spread").forGetter(c -> c.spread),
        Codec.BOOL.optionalFieldOf("needs_forest", Boolean.FALSE).forGetter(c -> c.needsForest)
    ).apply(instance, ClimateTargetConfig::new));

    private final float temp;
    private final float rain;
    private final int spread;
    private final boolean needsForest;

    public ClimateTargetConfig(float temp, float rain, int spread, boolean needsForest)
    {
        this.temp = temp;
        this.rain = rain;
        this.spread = spread;
        this.needsForest = needsForest;
    }

    public float getChance(float tempIn, float rainIn, ForestType forestType)
    {
        if (checkForest(forestType))
        {
            double tempSpread = Math.max(0, 1 - Math.abs((tempIn - temp)) / spread);
            double rainSpread = Math.max(0, 1 - Math.abs((rainIn - rain)) / spread / 20);
            return (tempSpread == 0 || rainSpread == 0) ? 0 : (float) ((rainSpread + tempSpread) / 2);
        }
        return 0;
    }

    private boolean checkForest(ForestType forestType)
    {
        if (!needsForest)
        {
            return true;
        }
        else
        {
            return forestType == ForestType.NORMAL || forestType == ForestType.OLD_GROWTH;
        }
    }
}
