/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.plant.KelpTreeFlowerBlock;
import net.dries007.tfc.world.feature.BlockConfig;

public class KelpTreeFeature extends Feature<BlockConfig<KelpTreeFlowerBlock>>
{
    public static final Codec<BlockConfig<KelpTreeFlowerBlock>> CODEC = BlockConfig.codec(b -> b instanceof KelpTreeFlowerBlock t ? t : null, "Must be a " + KelpTreeFlowerBlock.class.getSimpleName());

    public KelpTreeFeature(Codec<BlockConfig<KelpTreeFlowerBlock>> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockConfig<KelpTreeFlowerBlock>> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();

        final FluidState fluidAt = level.getFluidState(pos);

        final int seaLevel = level.getLevel().getChunkSource().getGenerator().getSeaLevel();
        return context.config().block().generatePlant(level, pos, context.random(), 8, fluidAt.getType(), seaLevel);
    }
}
