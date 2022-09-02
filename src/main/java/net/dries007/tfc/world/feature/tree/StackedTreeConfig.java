/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record StackedTreeConfig(List<Layer> layers, TrunkConfig trunk, TreePlacementConfig placement) implements FeatureConfiguration
{
    public static final Codec<StackedTreeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Layer.CODEC.listOf().fieldOf("layers").forGetter(c -> c.layers),
        TrunkConfig.CODEC.fieldOf("trunk").forGetter(c -> c.trunk),
        TreePlacementConfig.CODEC.fieldOf("placement").forGetter(c -> c.placement)
    ).apply(instance, StackedTreeConfig::new));

    public record Layer(List<ResourceLocation> templates, int minCount, int maxCount)
    {
        public static final Codec<Layer> CODEC = RecordCodecBuilder.<Layer>create(instance -> instance.group(
            ResourceLocation.CODEC.listOf().fieldOf("templates").forGetter(c -> c.templates),
            Codec.INT.fieldOf("min_count").forGetter(c -> c.minCount),
            Codec.INT.fieldOf("max_count").forGetter(c -> c.maxCount)
        ).apply(instance, Layer::new)).comapFlatMap(c -> {
            if (c.maxCount < c.minCount)
            {
                return DataResult.error("max count (provided = " + c.maxCount + ") must be greater than min count (provided = " + c.minCount + ")");
            }
            return DataResult.success(c);
        }, Function.identity());

        public int getCount(Random random)
        {
            if (maxCount == minCount)
            {
                return minCount;
            }
            return minCount + random.nextInt(1 + maxCount - minCount);
        }
    }
}
