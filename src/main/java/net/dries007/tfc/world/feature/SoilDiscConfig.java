/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public record SoilDiscConfig(Map<Block, BlockState> states, int minRadius, int maxRadius, int height) implements FeatureConfiguration
{
    public static final Codec<SoilDiscConfig> CODEC = RecordCodecBuilder.<SoilDiscConfig>create(instance -> instance.group(
        Codecs.mapListCodec(Codecs.recordPairCodec(
            Codecs.BLOCK, "replace",
            Codecs.BLOCK_STATE, "with"
        )).fieldOf("states").forGetter(c -> c.states),
        Codecs.POSITIVE_INT.fieldOf("min_radius").forGetter(c -> c.minRadius),
        Codecs.POSITIVE_INT.fieldOf("max_radius").forGetter(c -> c.maxRadius),
        Codec.intRange(0, 256).fieldOf("height").forGetter(c -> c.height)
    ).apply(instance, SoilDiscConfig::new)).comapFlatMap(c -> {
        if (c.maxRadius < c.minRadius)
        {
            return DataResult.error("Maximum radius (provided = " + c.maxRadius + ") must be >= min radius (provided = " + c.minRadius + ")");
        }
        return DataResult.success(c);
    }, Function.identity());

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
