/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature;

import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.common.types.Rock;

public class BoulderConfig implements IFeatureConfig
{
    public static final Codec<BoulderConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Rock.BlockType.CODEC.fieldOf("base_type").forGetter(c -> c.baseType),
        Rock.BlockType.CODEC.fieldOf("decoration_type").forGetter(c -> c.decorationType)
    ).apply(instance, BoulderConfig::new));

    private final Rock.BlockType baseType;
    private final Rock.BlockType decorationType;

    public BoulderConfig(Rock.BlockType baseType, Rock.BlockType decorationType)
    {
        this.baseType = baseType;
        this.decorationType = decorationType;
    }

    public Rock.BlockType getBaseType()
    {
        return baseType;
    }

    public Rock.BlockType getDecorationType()
    {
        return decorationType;
    }
}