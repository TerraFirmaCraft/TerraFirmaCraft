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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.plant.TallWaterPlantBlock;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.world.feature.BlockConfig;

public class EmergentPlantFeature extends Feature<BlockConfig<TallWaterPlantBlock>>
{
    public static final Codec<BlockConfig<TallWaterPlantBlock>> CODEC = BlockConfig.codec(b -> b instanceof TallWaterPlantBlock t ? t : null, "Must be a " + TallWaterPlantBlock.class.getSimpleName());

    public EmergentPlantFeature(Codec<BlockConfig<TallWaterPlantBlock>> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockConfig<TallWaterPlantBlock>> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();

        final Fluid fluidTop = level.getFluidState(pos.above()).getType();
        if (fluidTop.isSame(Fluids.EMPTY) && EnvironmentHelpers.isWorldgenReplaceable(level, pos))
        {
            context.config().block().placeTwoHalves(level, pos, 2, context.random());
        }
        return true;
    }
}
