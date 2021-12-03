/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.plant.TFCTallGrassBlock;
import net.dries007.tfc.world.Codecs;

public class TallPlantFeature extends Feature<BlockStateConfiguration>
{
    public static final Codec<BlockStateConfiguration> CODEC = Codecs.blockStateConfigCodec(b -> b instanceof TFCTallGrassBlock, "Must be a TFCTallGrassBlock");

    public TallPlantFeature(Codec<BlockStateConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context)
    {
        ((TFCTallGrassBlock) context.config().state.getBlock()).placeTwoHalves(context.level(), context.origin(), 2, context.random());
        return true;
    }
}
