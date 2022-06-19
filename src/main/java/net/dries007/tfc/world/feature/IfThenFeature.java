/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import com.mojang.serialization.Codec;

public class IfThenFeature extends Feature<IfThenConfig>
{
    private static boolean place(FeaturePlaceContext<IfThenConfig> context, Holder<PlacedFeature> feature)
    {
        return feature.value().placeWithBiomeCheck(context.level(), context.chunkGenerator(), context.random(), context.origin());
    }

    public IfThenFeature(Codec<IfThenConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<IfThenConfig> context)
    {
        return place(context, context.config().ifFeature()) && place(context, context.config().thenFeature());
    }
}
