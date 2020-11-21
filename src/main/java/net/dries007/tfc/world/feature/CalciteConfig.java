package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class CalciteConfig implements IFeatureConfig
{
    public static final Codec<CalciteConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.intRange(1, 16).fieldOf("radius").forGetter(CalciteConfig::getRadius),
        Codecs.POSITIVE_INT.fieldOf("tries").forGetter(CalciteConfig::getTries),
        Codec.intRange(1, 256).fieldOf("min_height").forGetter(c -> c.minHeight),
        Codec.intRange(1, 256).fieldOf("max_height").forGetter(c -> c.maxHeight)
    ).apply(instance, CalciteConfig::new));

    private final int radius;
    private final int tries;
    private final int minHeight;
    private final int maxHeight;

    public CalciteConfig(int radius, int tries, int minHeight, int maxHeight)
    {
        this.radius = radius;
        this.tries = tries;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;

        if (maxHeight < minHeight)
        {
            throw new IllegalStateException("maxHeight (" + minHeight + ") must be greater or equal to minHeight (" + maxHeight + ')');
        }
    }

    public int getRadius()
    {
        return radius;
    }

    public int getTries()
    {
        return tries;
    }

    public int getHeight(Random random)
    {
        if (minHeight == maxHeight)
        {
            return minHeight;
        }
        return minHeight + random.nextInt(maxHeight - minHeight);
    }
}
