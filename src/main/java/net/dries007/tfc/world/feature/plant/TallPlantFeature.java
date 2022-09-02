/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.plant.TFCTallGrassBlock;
import net.dries007.tfc.world.feature.BlockConfig;

public class TallPlantFeature extends Feature<BlockConfig<TFCTallGrassBlock>>
{
    public static final Codec<BlockConfig<TFCTallGrassBlock>> CODEC = BlockConfig.codec(b -> b instanceof TFCTallGrassBlock t ? t : null, "Must be a " + TFCTallGrassBlock.class.getSimpleName());

    public TallPlantFeature(Codec<BlockConfig<TFCTallGrassBlock>> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockConfig<TFCTallGrassBlock>> context)
    {
        context.config().block().placeTwoHalves(context.level(), context.origin(), 2, context.random());
        return true;
    }
}
