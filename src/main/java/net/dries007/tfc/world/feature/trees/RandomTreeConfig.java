/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class RandomTreeConfig implements IFeatureConfig
{
    public static final Codec<RandomTreeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.listOf().fieldOf("structures").forGetter(c -> c.structureNames),
        TrunkConfig.CODEC.optionalFieldOf("trunk").forGetter(c -> c.trunk),
        Codec.INT.fieldOf("radius").forGetter(c -> c.radius)
    ).apply(instance, RandomTreeConfig::new));

    public final List<ResourceLocation> structureNames;
    public final Optional<TrunkConfig> trunk;
    public final int radius;

    public RandomTreeConfig(List<ResourceLocation> structureNames, Optional<TrunkConfig> trunk, int radius)
    {
        this.structureNames = structureNames;
        this.trunk = trunk;
        this.radius = radius;
    }
}