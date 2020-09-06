/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class RandomlyChosenTreeConfig implements IFeatureConfig
{
    @Nullable
    public static <T> RandomlyChosenTreeConfig deserialize(Dynamic<T> config)
    {
        return null;
    }

    private final List<ResourceLocation> structureNames;

    public RandomlyChosenTreeConfig(List<ResourceLocation> structureNames)
    {
        this.structureNames = structureNames;
    }

    public List<ResourceLocation> getStructureNames()
    {
        return structureNames;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops)
    {
        return new Dynamic<>(ops, ops.emptyMap());
    }
}
