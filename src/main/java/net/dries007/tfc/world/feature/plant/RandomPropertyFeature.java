/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import com.mojang.serialization.Codec;

public class RandomPropertyFeature extends Feature<RandomPropertyConfig>
{
    public RandomPropertyFeature(Codec<RandomPropertyConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<RandomPropertyConfig> context)
    {
        context.level().setBlock(context.origin(), context.config().state(context.random()), 2);
        return true;
    }
}
