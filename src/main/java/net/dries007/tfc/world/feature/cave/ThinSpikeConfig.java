/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.cave;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class ThinSpikeConfig implements IFeatureConfig
{
    public static final Codec<ThinSpikeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.LENIENT_BLOCKSTATE.fieldOf("state").forGetter(ThinSpikeConfig::getState),
        Codec.intRange(1, 16).fieldOf("radius").forGetter(ThinSpikeConfig::getRadius),
        Codecs.POSITIVE_INT.fieldOf("tries").forGetter(ThinSpikeConfig::getTries),
        Codec.intRange(1, 256).fieldOf("min_height").forGetter(c -> c.minHeight),
        Codec.intRange(1, 256).fieldOf("max_height").forGetter(c -> c.maxHeight)
    ).apply(instance, ThinSpikeConfig::new));

    private final BlockState state;
    private final int radius;
    private final int tries;
    private final int minHeight;
    private final int maxHeight;

    public ThinSpikeConfig(BlockState state, int radius, int tries, int minHeight, int maxHeight)
    {
        this.state = state;
        this.radius = radius;
        this.tries = tries;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;

        if (maxHeight < minHeight)
        {
            throw new IllegalStateException("maxHeight (" + minHeight + ") must be greater or equal to minHeight (" + maxHeight + ')');
        }
    }

    public BlockState getState()
    {
        return state;
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
