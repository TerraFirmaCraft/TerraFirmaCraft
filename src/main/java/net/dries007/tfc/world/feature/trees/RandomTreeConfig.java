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

public class RandomTreeConfig implements IFeatureConfig
{
    public static RandomTreeConfig forVariants(String baseName, int variants)
    {
        return new RandomTreeConfig(IntStream.range(1, 1 + variants).mapToObj(i -> new ResourceLocation(MOD_ID, baseName + "/" + i)).collect(Collectors.toList()));
    }

    public static RandomTreeConfig forVariants(String baseName, int variants, int trunkHeightMin, int trunkHeightRange, int radius)
    {
        return new RandomTreeConfig(IntStream.range(1, 1 + variants).mapToObj(i -> new ResourceLocation(MOD_ID, baseName + "/" + i)).collect(Collectors.toList()), trunkHeightMin, trunkHeightRange, radius);
    }

    @Nullable
    public static <T> RandomTreeConfig deserialize(Dynamic<T> config)
    {
        return null;
    }

    public final List<ResourceLocation> structureNames;
    public final int trunkHeightMin, trunkHeightRange;
    public final int radius;

    public RandomTreeConfig(List<ResourceLocation> structureNames)
    {
        this(structureNames, 0, 0, 1);
    }

    public RandomTreeConfig(List<ResourceLocation> structureNames, int trunkHeightMin, int trunkHeightRange, int radius)
    {
        this.structureNames = structureNames;
        this.trunkHeightMin = trunkHeightMin;
        this.trunkHeightRange = trunkHeightRange;
        this.radius = radius;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops)
    {
        return new Dynamic<>(ops, ops.emptyMap());
    }
}