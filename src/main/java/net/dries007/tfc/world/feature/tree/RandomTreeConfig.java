/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.List;
import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import net.dries007.tfc.world.Codecs;

public record RandomTreeConfig(List<ResourceLocation> structureNames, Optional<TrunkConfig> trunk, TreePlacementConfig placement, Optional<RootConfig> rootSystem) implements FeatureConfiguration
{
    public static final Codec<RandomTreeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.listOf().fieldOf("structures").forGetter(c -> c.structureNames),
        Codecs.optionalFieldOf(TrunkConfig.CODEC, "trunk").forGetter(c -> c.trunk),
        TreePlacementConfig.CODEC.fieldOf("placement").forGetter(c -> c.placement),
        Codecs.optionalFieldOf(RootConfig.CODEC, "root_system").forGetter(c -> c.rootSystem)
    ).apply(instance, RandomTreeConfig::new));
}