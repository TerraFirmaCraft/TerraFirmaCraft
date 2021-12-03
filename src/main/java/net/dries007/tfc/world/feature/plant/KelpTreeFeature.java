/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.plant.KelpTreeFlowerBlock;
import net.dries007.tfc.world.Codecs;

public class KelpTreeFeature extends Feature<BlockStateConfiguration>
{
    public static final Codec<BlockStateConfiguration> CODEC = Codecs.blockStateConfigCodec(b -> b instanceof KelpTreeFlowerBlock, "Must be a KelpTreeFlowerBlock");

    public KelpTreeFeature(Codec<BlockStateConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();

        final FluidState fluidAt = level.getFluidState(pos);
        final KelpTreeFlowerBlock flower = (KelpTreeFlowerBlock) context.config().state.getBlock();

        flower.generatePlant(level, pos, context.random(), 8, fluidAt.getType());
        return true;
    }
}
