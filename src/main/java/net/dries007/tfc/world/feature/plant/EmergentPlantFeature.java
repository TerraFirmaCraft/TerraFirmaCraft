/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.plant.TallWaterPlantBlock;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.world.Codecs;

public class EmergentPlantFeature extends Feature<BlockStateConfiguration>
{
    public static final Codec<BlockStateConfiguration> CODEC = Codecs.blockStateConfigCodec(b -> b instanceof TallWaterPlantBlock, "Must be a TallWaterPlantBlock");

    public EmergentPlantFeature(Codec<BlockStateConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();

        final Fluid fluidTop = level.getFluidState(pos.above()).getType();
        if (fluidTop.isSame(Fluids.EMPTY) && EnvironmentHelpers.isWorldgenReplaceable(level, pos))
        {
            ((TallWaterPlantBlock) context.config().state.getBlock()).placeTwoHalves(level, pos, 2, context.random());
        }
        return true;
    }
}
