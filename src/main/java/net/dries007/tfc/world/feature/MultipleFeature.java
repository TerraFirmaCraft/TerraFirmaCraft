/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.Feature;

import com.mojang.serialization.Codec;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class MultipleFeature extends Feature<SimpleRandomFeatureConfiguration>
{
    public MultipleFeature(Codec<SimpleRandomFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SimpleRandomFeatureConfiguration> context)
    {
        boolean result = false;
        for (Holder<PlacedFeature> feature : context.config().features)
        {
            result |= feature.value().placeWithBiomeCheck(context.level(), context.chunkGenerator(), context.random(), context.origin());
        }
        return result;
    }
}
