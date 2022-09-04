/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record OverlayTreeConfig(ResourceLocation base, ResourceLocation overlay, Optional<TrunkConfig> trunk, float overlayIntegrity, TreePlacementConfig placement) implements FeatureConfiguration
{
    public static final Codec<OverlayTreeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("base").forGetter(c -> c.base),
        ResourceLocation.CODEC.fieldOf("overlay").forGetter(c -> c.overlay),
        TrunkConfig.CODEC.optionalFieldOf("trunk").forGetter(c -> c.trunk),
        Codec.floatRange(0, 1).optionalFieldOf("overlay_integrity", 0.5f).forGetter(c -> c.overlayIntegrity),
        TreePlacementConfig.CODEC.fieldOf("placement").forGetter(c -> c.placement)
    ).apply(instance, OverlayTreeConfig::new));
}