/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.function.Supplier;

import net.minecraft.world.level.levelgen.feature.Feature;

import com.mojang.serialization.Codec;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class MultipleFeature extends Feature<MultipleConfig>
{
    public MultipleFeature(Codec<MultipleConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<MultipleConfig> context)
    {
        boolean result = false;
        for (Supplier<PlacedFeature> feature : context.config().features())
        {
            result |= feature.get().placeWithBiomeCheck(context.level(), context.chunkGenerator(), context.random(), context.origin());
        }
        return result;
    }
}
