/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.dries007.tfc.api.Rock;

public class BoulderConfig implements IFeatureConfig
{
    public static <T> BoulderConfig deserialize(Dynamic<T> ops)
    {
        Rock.BlockType baseType = Rock.BlockType.valueOf(ops.get("base_type").asInt(0));
        Rock.BlockType decorationType = Rock.BlockType.valueOf(ops.get("decoration_type").asInt(0));
        return new BoulderConfig(baseType, decorationType);
    }

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

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops)
    {
        return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(
            ops.createString("base_type"), ops.createInt(baseType.ordinal()),
            ops.createString("decoration_type"), ops.createInt(decorationType.ordinal())
        )));
    }
}
