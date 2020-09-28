package net.dries007.tfc.world.feature.trees;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class StackedTreeConfig implements IFeatureConfig
{
    public static final Codec<StackedTreeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Layer.CODEC.listOf().fieldOf("layers").forGetter(c -> c.layers),
        TrunkConfig.CODEC.fieldOf("trunk").forGetter(c -> c.trunk),
        Codec.INT.fieldOf("radius").forGetter(c -> c.radius)
    ).apply(instance, StackedTreeConfig::new));

    public final List<Layer> layers;
    public final TrunkConfig trunk;
    public final int radius;

    public StackedTreeConfig(List<Layer> layers, TrunkConfig trunk, int radius)
    {
        this.layers = layers;
        this.trunk = trunk;
        this.radius = radius;
    }

    public static class Layer
    {
        public static final Codec<Layer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.listOf().fieldOf("templates").forGetter(c -> c.templates),
            Codec.INT.fieldOf("min_count").forGetter(c -> c.minCount),
            Codec.INT.fieldOf("max_count").forGetter(c -> c.maxCount)
        ).apply(instance, Layer::new));

        public final List<ResourceLocation> templates;
        private final int minCount;
        private final int maxCount;

        public Layer(List<ResourceLocation> templates, int minCount, int maxCount)
        {
            this.templates = templates;
            this.minCount = minCount;
            this.maxCount = maxCount;
            if (maxCount < minCount)
            {
                throw new IllegalStateException("max count must be greater than min count");
            }
        }

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
