/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NormalTreeConfig implements IFeatureConfig
{
    public static <T> NormalTreeConfig deserialize(Dynamic<T> config)
    {
        return new NormalTreeConfig();
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops)
    {
        return new Dynamic<>(ops, ops.emptyMap());
    }
}
