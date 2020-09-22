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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class RandomTreeConfig implements IFeatureConfig
{
    public static final Codec<RandomTreeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.listOf().fieldOf("structures").forGetter(c -> c.structureNames),
        Codec.INT.fieldOf("trunk_height_min").forGetter(c -> c.trunkHeightMin),
        Codec.INT.fieldOf("trunk_height_range").forGetter(c -> c.trunkHeightRange),
        Codec.INT.fieldOf("radius").forGetter(c -> c.radius)
    ).apply(instance, RandomTreeConfig::new));

    public final List<ResourceLocation> structureNames;
    public final int trunkHeightMin, trunkHeightRange;
    public final int radius;

    public RandomTreeConfig(List<ResourceLocation> structureNames, int trunkHeightMin, int trunkHeightRange, int radius)
    {
        this.structureNames = structureNames;
        this.trunkHeightMin = trunkHeightMin;
        this.trunkHeightRange = trunkHeightRange;
        this.radius = radius;
    }
}