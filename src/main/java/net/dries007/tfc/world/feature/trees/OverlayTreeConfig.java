/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class OverlayTreeConfig implements IFeatureConfig
{
    public static final Codec<OverlayTreeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("base").forGetter(c -> c.base),
        ResourceLocation.CODEC.fieldOf("overlay").forGetter(c -> c.overlay),
        Codec.INT.fieldOf("height_min").forGetter(c -> c.heightMin),
        Codec.INT.fieldOf("height_range").forGetter(c -> c.heightRange),
        BlockState.CODEC.fieldOf("trunk_state").forGetter(c -> c.trunkState),
        Codec.INT.fieldOf("radius").forGetter(c -> c.radius)
    ).apply(instance, OverlayTreeConfig::new));

    public final ResourceLocation base;
    public final ResourceLocation overlay;
    public final int heightMin;
    public final int heightRange;
    public final BlockState trunkState;
    public final int radius;

    public OverlayTreeConfig(ResourceLocation base, ResourceLocation overlay, int heightMin, int heightRange, BlockState trunkState, int radius)
    {
        this.base = base;
        this.overlay = overlay;
        this.heightMin = heightMin;
        this.heightRange = heightRange;
        this.trunkState = trunkState;
        this.radius = radius;
    }
}