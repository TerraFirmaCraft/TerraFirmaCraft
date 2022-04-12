/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.OverworldClimateModel;

public class IcicleBlock extends ThinSpikeBlock
{
    /**
     * Modified from {@link net.minecraft.world.level.block.PointedDripstoneBlock#spawnDripParticle(Level, BlockPos, BlockState, Fluid)}
     */
    public static void spawnDripParticle(Level level, BlockPos pos, BlockState state)
    {
        Vec3 offset = state.getOffset(level, pos);
        level.addParticle(ParticleTypes.DRIPPING_DRIPSTONE_WATER, pos.getX() + 0.5D + offset.x, ((pos.getY() + 1) - 0.6875F) - 0.0625D, pos.getZ() + 0.5D + offset.z, 0.0D, 0.0D, 0.0D);
    }

    public IcicleBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random)
    {
        final float temperature = Climate.getTemperature(level, pos);
        if (state.getValue(TIP) && state.getValue(FLUID).getFluid() == Fluids.EMPTY && temperature > OverworldClimateModel.ICICLE_MELT_TEMPERATURE && random.nextFloat() < 0.008f)
        {
            // Melt, shrink the icicle, and possibly fill a fluid handler beneath
            level.removeBlock(pos, false);

            final BlockPos posAbove = pos.above();
            final BlockState stateAbove = level.getBlockState(posAbove);
            if (Helpers.isBlock(stateAbove, this))
            {
                level.setBlock(posAbove, stateAbove.setValue(TIP, true), Block.UPDATE_ALL);
            }

            // todo: fill fluid containers a certain distance below with 100mB water
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random)
    {
        final float temperature = Climate.getTemperature(level, pos);
        if (state.getValue(TIP) && state.getValue(FLUID).getFluid() == Fluids.EMPTY && temperature > OverworldClimateModel.ICICLE_DRIP_TEMPERATURE && random.nextFloat() < 0.15f)
        {
            if (random.nextFloat() < 0.15f)
            {
                spawnDripParticle(level, pos, state);
            }
        }
    }
}
