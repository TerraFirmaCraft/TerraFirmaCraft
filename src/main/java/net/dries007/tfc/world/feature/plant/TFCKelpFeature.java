/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.fluids.FluidHelpers;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class TFCKelpFeature extends Feature<ColumnPlantConfig>
{
    public TFCKelpFeature(Codec<ColumnPlantConfig> codec)
    {
        super(codec);
    }

    public boolean place(FeaturePlaceContext<ColumnPlantConfig> context)
    {
        final WorldGenLevel world = context.level();
        final BlockPos pos = context.origin();
        final Random rand = context.random();
        final ColumnPlantConfig config = context.config();

        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final int radius = config.radius();

        boolean placedAny = false;

        for (int i = 0; i < config.tries(); i++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(radius) - rand.nextInt(radius), 0, rand.nextInt(radius) - rand.nextInt(radius));
            mutablePos.set(world.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR, mutablePos));

            final BlockState state = world.getBlockState(mutablePos);
            final Fluid fluid = state.getFluidState().getType();
            final BlockState bodyState = FluidHelpers.fillWithFluid(config.bodyState(), fluid);
            final BlockState headState = FluidHelpers.fillWithFluid(config.headState(), fluid);

            if (bodyState != null && headState != null && bodyState.canSurvive(world, mutablePos) && FluidHelpers.isAirOrEmptyFluid(state))
            {
                placeColumn(world, rand, mutablePos, rand.nextInt(config.maxHeight() - config.minHeight()) + config.minHeight(), 17, 25, bodyState, headState);
                placedAny = true;
            }
        }
        return placedAny;
    }

    private void placeColumn(LevelAccessor world, Random rand, BlockPos.MutableBlockPos mutablePos, int height, int minAge, int maxAge, BlockState body, BlockState head)
    {
        for (int i = 1; i <= height; ++i)
        {
            if (world.isWaterAt(mutablePos) && body.canSurvive(world, mutablePos))
            {
                if (i == height || !world.isWaterAt(mutablePos.above()))
                {
                    if (!world.getBlockState(mutablePos.below()).is(head.getBlock()))
                    {
                        world.setBlock(mutablePos, head.setValue(GrowingPlantHeadBlock.AGE, Mth.nextInt(rand, minAge, maxAge)), 16);
                    }
                    return;
                }
                world.setBlock(mutablePos, body, 16);
            }
            mutablePos.move(Direction.UP);
        }
    }
}
