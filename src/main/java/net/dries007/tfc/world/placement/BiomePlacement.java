/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.biome.TFCBiomes;

public final class BiomePlacement extends PlacementFilter
{
    public static final Codec<BiomePlacement> CODEC = Codec.unit(new BiomePlacement());

    @Override
    public PlacementModifierType<?> type()
    {
        return TFCPlacements.BIOME.get();
    }

    @Override
    protected boolean shouldPlace(PlacementContext context, Random random, BlockPos pos)
    {
        final PlacedFeature feature = context.topFeature().orElseThrow(() -> new IllegalStateException("Tried to biome check an unregistered feature"));
        final Biome biome = context.getLevel().getBiome(pos).value();
        return TFCBiomes.getExtensionOrThrow(context.getLevel(), biome).getFlattenedFeatureSet(biome).contains(feature);
    }
}
