/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class SoilDiscConfig implements IFeatureConfig
{
    @SuppressWarnings("deprecation")
    public static final Codec<SoilDiscConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.mapListCodec(Codecs.recordPairCodec(
            Registry.BLOCK, "replace",
            Codecs.LENIENT_BLOCKSTATE, "with"
        )).fieldOf("states").forGetter(c -> c.states),
        Codecs.POSITIVE_INT.fieldOf("min_radius").forGetter(c -> c.minRadius),
        Codecs.POSITIVE_INT.fieldOf("max_radius").forGetter(c -> c.maxRadius),
        Codec.intRange(0, 256).fieldOf("height").forGetter(c -> c.height)
    ).apply(instance, SoilDiscConfig::new));

    private final Map<Block, BlockState> states;
    private final int minRadius;
    private final int maxRadius;
    private final int height;

    public SoilDiscConfig(Map<Block, BlockState> states, int minRadius, int maxRadius, int height)
    {
        this.states = states;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.height = height;

        if (maxRadius < minRadius)
        {
            throw new IllegalArgumentException("Maximum radius (provided = " + maxRadius + ") must be >= min radius (provided = " + minRadius + ")");
        }
    }

    public int getRadius(Random random)
    {
        if (maxRadius > minRadius)
        {
            return minRadius + random.nextInt(maxRadius - minRadius);
        }
        return minRadius;
    }

    public int getHeight()
    {
        return height;
    }

    @Nullable
    public BlockState getState(BlockState stateIn)
    {
        return states.get(stateIn.getBlock());
    }
}
