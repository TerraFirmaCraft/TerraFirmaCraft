/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import net.dries007.tfc.world.chunkdata.ChunkData;

public class NoisyMultipleFeature extends Feature<SimpleRandomFeatureConfiguration>
{
    public NoisyMultipleFeature(Codec<SimpleRandomFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SimpleRandomFeatureConfiguration> context)
    {
        final BlockPos pos = context.origin();
        final ChunkData data = ChunkData.get(context.level(), pos);
        final int rotation = (int) Math.ceil(data.getForestWeirdness() * 10 * context.config().features.size());

        List<Holder<PlacedFeature>> features = context.config().features.stream().collect(Collectors.toList());
        Collections.rotate(features, rotation);

        int placed = 0;
        for (Holder<PlacedFeature> feature : features)
        {
            if (feature.value().placeWithBiomeCheck(context.level(), context.chunkGenerator(), context.random(), pos))
            {
                placed++;
                if (placed > 1)
                {
                    return true;
                }
            }
        }
        return placed > 0;
    }
}
