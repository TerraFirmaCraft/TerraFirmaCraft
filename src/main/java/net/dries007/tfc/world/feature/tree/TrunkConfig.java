/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Random;
import java.util.function.Function;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TrunkConfig(BlockState state, int minHeight, int maxHeight, int width)
{
    public static final Codec<TrunkConfig> CODEC = RecordCodecBuilder.<TrunkConfig>create(instance -> instance.group(
        BlockState.CODEC.fieldOf("state").forGetter(c -> c.state),
        Codec.INT.fieldOf("min_height").forGetter(c -> c.minHeight),
        Codec.INT.fieldOf("max_height").forGetter(c -> c.maxHeight),
        Codec.INT.fieldOf("width").forGetter(c -> c.width)
    ).apply(instance, TrunkConfig::new)).comapFlatMap(c -> {
        if (c.minHeight >= c.maxHeight)
        {
            return DataResult.error("Min height (provided = " + c.minHeight + ") must not be greater or equal to max height (provided = " + c.maxHeight + ")");
        }
        return DataResult.success(c);
    }, Function.identity());

    public int getHeight(Random random)
    {
        if (maxHeight == minHeight)
        {
            return minHeight;
        }
        return minHeight + random.nextInt(1 + maxHeight - minHeight);
    }
}
