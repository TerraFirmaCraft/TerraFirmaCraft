/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class VineConfig implements IFeatureConfig
{
    public static final Codec<VineConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.LENIENT_BLOCKSTATE.fieldOf("state").forGetter(VineConfig::getState),
        Codec.intRange(1, 128).fieldOf("tries").forGetter(c -> c.tries),
        Codec.intRange(1, 16).fieldOf("radius").forGetter(c -> c.radius),
        Codec.intRange(1, 255).fieldOf("minHeight").forGetter(c -> c.minHeight),
        Codec.intRange(1, 255).fieldOf("maxHeight").forGetter(c -> c.maxHeight)
    ).apply(instance, VineConfig::new));

    private final BlockState state;
    private final int tries;
    private final int radius;
    private final int minHeight;
    private final int maxHeight;

    public VineConfig(BlockState state, int tries, int radius, int minHeight, int maxHeight)
    {
        this.state = state;
        this.tries = tries;
        this.radius = radius;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    public BlockState getState()
    {
        return state;
    }

    public int getTries()
    {
        return tries;
    }

    public int getRadius()
    {
        return radius;
    }

    public int getMinHeight()
    {
        return minHeight;
    }

    public int getMaxHeight()
    {
        return maxHeight;
    }
}
