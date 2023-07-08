/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import net.dries007.tfc.common.blocks.plant.GrowingBranchingCactusBlock;
import net.dries007.tfc.world.feature.BlockConfig;

public class BranchingCactusFeature extends Feature<BlockConfig<GrowingBranchingCactusBlock>>
{
    public static final Codec<BlockConfig<GrowingBranchingCactusBlock>> CODEC = BlockConfig.codec(b -> b instanceof GrowingBranchingCactusBlock t ? t : null, "Must be a " + GrowingBranchingCactusBlock.class.getSimpleName());

    public BranchingCactusFeature(Codec<BlockConfig<GrowingBranchingCactusBlock>> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockConfig<GrowingBranchingCactusBlock>> context)
    {
        return context.config().block().growRecursively(context.level(), context.origin(), context.config().block().defaultBlockState(), 8);
    }
}
