/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.fluids.FluidHelpers;

public class BlockWithFluidFeature extends Feature<SimpleBlockConfiguration>
{
    public BlockWithFluidFeature(Codec<SimpleBlockConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SimpleBlockConfiguration> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();

        final BlockState state = FluidHelpers.fillWithFluid(context.config().toPlace().getState(context.random(), pos), level.getFluidState(pos).getType());
        if (state != null && state.canSurvive(level, pos))
        {
            level.setBlock(pos, state, 2);
            return true;
        }
        return false;
    }
}
