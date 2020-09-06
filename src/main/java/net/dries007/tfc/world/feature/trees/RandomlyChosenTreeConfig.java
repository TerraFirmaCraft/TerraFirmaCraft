/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class RandomlyChosenTreeConfig implements IFeatureConfig
{
    public static RandomlyChosenTreeConfig forVariants(String baseName, int variants)
    {
        return new RandomlyChosenTreeConfig(IntStream.range(1, 1 + variants).mapToObj(i -> new ResourceLocation(MOD_ID, baseName + "/" + i)).collect(Collectors.toList()));
    }

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
