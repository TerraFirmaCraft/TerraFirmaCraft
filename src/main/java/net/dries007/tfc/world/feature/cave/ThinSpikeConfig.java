/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.cave;

import java.util.Random;
import java.util.function.Function;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public record ThinSpikeConfig(BlockState state, int radius, int tries, int minHeight, int maxHeight) implements FeatureConfiguration
{
    public static final Codec<ThinSpikeConfig> CODEC = RecordCodecBuilder.<ThinSpikeConfig>create(instance -> instance.group(
        Codecs.BLOCK_STATE.fieldOf("state").forGetter(c -> c.state),
        Codec.intRange(1, 16).fieldOf("radius").forGetter(c -> c.radius),
        Codecs.POSITIVE_INT.fieldOf("tries").forGetter(c -> c.tries),
        Codecs.POSITIVE_INT.fieldOf("min_height").forGetter(c -> c.minHeight),
        Codecs.POSITIVE_INT.fieldOf("max_height").forGetter(c -> c.maxHeight)
    ).apply(instance, ThinSpikeConfig::new)).comapFlatMap(c -> {
        if (c.maxHeight < c.minHeight)
        {
            return DataResult.error("maxHeight (" + c.minHeight + ") must be greater or equal to minHeight (" + c.maxHeight + ')');
        }
        return DataResult.success(c);
    }, Function.identity());

    public int getHeight(Random random)
    {
        if (minHeight == maxHeight)
        {
            return minHeight;
        }
        return minHeight + random.nextInt(maxHeight - minHeight);
    }
}
