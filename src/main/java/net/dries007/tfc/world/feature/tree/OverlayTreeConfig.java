/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Optional;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class OverlayTreeConfig implements IFeatureConfig
{
    public static final Codec<OverlayTreeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("base").forGetter(c -> c.base),
        ResourceLocation.CODEC.fieldOf("overlay").forGetter(c -> c.overlay),
        Codec.intRange(0, Integer.MAX_VALUE).fieldOf("radius").forGetter(c -> c.radius),
        TrunkConfig.CODEC.optionalFieldOf("trunk").forGetter(c -> c.trunk),
        Codec.floatRange(0, 1).optionalFieldOf("overlay_integrity", 0.5f).forGetter(c -> c.overlayIntegrity)
    ).apply(instance, OverlayTreeConfig::new));

    public final ResourceLocation base;
    public final ResourceLocation overlay;
    public final int radius;
    public final Optional<TrunkConfig> trunk;
    public final float overlayIntegrity;

    public OverlayTreeConfig(ResourceLocation base, ResourceLocation overlay, int radius, Optional<TrunkConfig> trunk, float overlayIntegrity)
    {
        this.base = base;
        this.overlay = overlay;
        this.radius = radius;
        this.trunk = trunk;
        this.overlayIntegrity = overlayIntegrity;
    }
}