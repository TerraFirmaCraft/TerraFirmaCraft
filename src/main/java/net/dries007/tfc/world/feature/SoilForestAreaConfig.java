/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.Codecs;

public record SoilForestAreaConfig(Map<Block, BlockState> states, int minForest, int maxForest, int priority, int height, float spread, float noiseScale, boolean inverted) implements FeatureConfiguration
{
    public static final Codec<SoilForestAreaConfig> CODEC = RecordCodecBuilder.<SoilForestAreaConfig>create(instance -> instance.group(
        Codecs.mapListCodec(Codecs.recordPairCodec(
            Codecs.BLOCK, "replace",
            Codecs.BLOCK_STATE, "with"
        )).fieldOf("states").forGetter(c -> c.states),
        Codecs.POSITIVE_INT.fieldOf("min_forest").forGetter(c -> c.minForest),
        Codecs.POSITIVE_INT.fieldOf("max_forest").forGetter(c -> c.maxForest),
        Codecs.POSITIVE_INT.optionalFieldOf("priority", 0).forGetter(c -> c.priority),
        Codec.intRange(0, 256).fieldOf("height").forGetter(c -> c.height),
        Codec.FLOAT.optionalFieldOf("spread", 0.05f).forGetter(c -> c.spread),
        Codec.FLOAT.optionalFieldOf("noise_scale", 1f).forGetter(c -> c.noiseScale),
        Codec.BOOL.optionalFieldOf("inverted", false).forGetter(c -> c.inverted)
    ).apply(instance, SoilForestAreaConfig::new)).comapFlatMap(c -> {
        if (c.maxForest < c.minForest)
        {
            return DataResult.error(() -> "Maximum forest (provided = " + c.maxForest + ") must be >= min forest (provided = " + c.minForest + ")");
        }
        return DataResult.success(c);
    }, Function.identity());

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
