/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;


public class OverlayTreeConfig implements IFeatureConfig
{
    @Nullable
    public static <T> OverlayTreeConfig deserialize(Dynamic<T> config)
    {
        return null;
    }

    public final ResourceLocation base;
    public final ResourceLocation overlay;
    public final int heightMin;
    public final int heightRange;
    public final BlockState trunkState;
    public final int radius;

    public OverlayTreeConfig(ResourceLocation base, ResourceLocation overlay, int heightMin, int heightRange, BlockState trunkState)
    {
        this.base = base;
        this.overlay = overlay;
        this.heightMin = heightMin;
        this.heightRange = heightRange;
        this.trunkState = trunkState;
        this.radius = 1;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops)
    {
        return new Dynamic<>(ops, ops.emptyMap());
    }
}
