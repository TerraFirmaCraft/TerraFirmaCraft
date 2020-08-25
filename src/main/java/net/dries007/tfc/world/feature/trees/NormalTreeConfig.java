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

public class NormalTreeConfig implements IFeatureConfig
{
    @Nullable
    public static <T> NormalTreeConfig deserialize(Dynamic<T> config)
    {
        return null;
    }

    private final ResourceLocation base;
    private final ResourceLocation overlay;
    private final int heightMin;
    private final int heightRange;
    private final BlockState trunkState;

    public NormalTreeConfig(ResourceLocation base, ResourceLocation overlay, int heightMin, int heightRange, BlockState trunkState)
    {
        this.base = base;
        this.overlay = overlay;
        this.heightMin = heightMin;
        this.heightRange = heightRange;
        this.trunkState = trunkState;
    }

    public ResourceLocation getBase()
    {
        return base;
    }

    public ResourceLocation getOverlay()
    {
        return overlay;
    }

    public int getHeightMin()
    {
        return heightMin;
    }

    public int getHeightRange()
    {
        return heightRange;
    }

    public BlockState getTrunkState()
    {
        return trunkState;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops)
    {
        return new Dynamic<>(ops, ops.emptyMap());
    }
}
