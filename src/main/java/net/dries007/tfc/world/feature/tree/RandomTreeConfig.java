/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.List;
import java.util.Optional;

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