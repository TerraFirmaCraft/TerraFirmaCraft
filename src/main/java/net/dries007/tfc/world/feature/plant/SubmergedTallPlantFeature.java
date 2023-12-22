/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.blocks.plant.TallWaterPlantBlock;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.world.feature.BlockConfig;

public class SubmergedTallPlantFeature extends Feature<BlockConfig<TallWaterPlantBlock>>
{
    public static final Codec<BlockConfig<TallWaterPlantBlock>> CODEC = BlockConfig.codec(b -> b instanceof TallWaterPlantBlock t ? t : null, "Must be a " + TallWaterPlantBlock.class.getSimpleName());

    public SubmergedTallPlantFeature(Codec<BlockConfig<TallWaterPlantBlock>> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockConfig<TallWaterPlantBlock>> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();

        final Fluid fluidBottom = level.getFluidState(pos).getType();
        final Fluid fluidTop = level.getFluidState(pos.above()).getType();
        if (fluidTop.isSame(fluidBottom) && EnvironmentHelpers.isWorldgenReplaceable(level, pos) && EnvironmentHelpers.isWorldgenReplaceable(level, pos.above()))
        {
            context.config().block().placeTwoHalves(level, pos, 2, context.random());
        }
        return true;
    }
}
