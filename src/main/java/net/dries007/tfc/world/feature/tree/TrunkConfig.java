/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.function.Function;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public record TrunkConfig(BlockState state, int minHeight, int maxHeight, boolean wide)
{
    public static final Codec<TrunkConfig> CODEC = RecordCodecBuilder.<TrunkConfig>create(instance -> instance.group(
        BlockState.CODEC.fieldOf("state").forGetter(c -> c.state),
        Codec.INT.fieldOf("min_height").forGetter(c -> c.minHeight),
        Codec.INT.fieldOf("max_height").forGetter(c -> c.maxHeight),
        Codec.BOOL.fieldOf("wide").forGetter(c -> c.wide)
    ).apply(instance, TrunkConfig::new)).comapFlatMap(c -> {
        if (c.minHeight >= c.maxHeight)
        {
            return DataResult.error(() -> "Min height (provided = " + c.minHeight + ") must not be greater or equal to max height (provided = " + c.maxHeight + ")");
        }
        return DataResult.success(c);
    }, Function.identity());

    public int getHeight(RandomSource random)
    {
        if (maxHeight == minHeight)
        {
            return minHeight;
        }
        return minHeight + random.nextInt(1 + maxHeight - minHeight);
    }
}
