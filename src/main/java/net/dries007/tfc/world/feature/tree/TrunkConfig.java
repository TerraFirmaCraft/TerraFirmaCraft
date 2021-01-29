/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Random;

import net.minecraft.block.BlockState;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TrunkConfig
{
    public static final Codec<TrunkConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockState.CODEC.fieldOf("state").forGetter(c -> c.state),
        Codec.INT.fieldOf("min_height").forGetter(c -> c.minHeight),
        Codec.INT.fieldOf("max_height").forGetter(c -> c.maxHeight),
        Codec.INT.fieldOf("width").forGetter(c -> c.width)
    ).apply(instance, TrunkConfig::new));

    public final BlockState state;
    public final int width;
    private final int minHeight;
    private final int maxHeight;

    public TrunkConfig(BlockState state, int minHeight, int maxHeight, int width)
    {
        this.state = state;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.width = width;
        if (minHeight > maxHeight)
        {
            throw new IllegalStateException("max height must be >= min height");
        }
    }

    public int getHeight(Random random)
    {
        if (maxHeight == minHeight)
        {
            return minHeight;
        }
        return minHeight + random.nextInt(1 + maxHeight - minHeight);
    }
}
