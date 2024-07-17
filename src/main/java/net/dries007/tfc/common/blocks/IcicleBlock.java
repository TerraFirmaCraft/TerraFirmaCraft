/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.EnvironmentHelpers;
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
    protected void attack(BlockState state, Level level, BlockPos pos, Player player)
    {
        if (Helpers.isItem(player.getMainHandItem(), TFCTags.Items.TOOLS_HAMMER) || Helpers.isItem(player.getMainHandItem(), ItemTags.SWORDS))
        {
            level.destroyBlock(pos, true);
            for (BlockPos testPos : BlockPos.betweenClosed(pos.offset(-2, -2, -2), pos.offset(2, 2, 2)))
            {
                if (level.getBlockState(testPos).getBlock() instanceof IcicleBlock)
                {
                    level.destroyBlock(testPos, true);
                }
            }
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        final float temperature = Climate.getTemperature(level, pos);
        if (state.getValue(TIP) && state.getValue(FLUID).getFluid() == Fluids.EMPTY && temperature > OverworldClimateModel.ICICLE_MELT_TEMPERATURE && (random.nextInt(EnvironmentHelpers.ICICLE_MELT_RANDOM_TICK_CHANCE) == 0 || level.getBrightness(LightLayer.BLOCK, pos) > 11))
        {
            // Melt, shrink the icicle, and possibly fill a fluid handler beneath
            level.removeBlock(pos, false);

            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos().setWithOffset(pos, 0, 1, 0);

            final BlockState stateAbove = level.getBlockState(mutable);
            if (Helpers.isBlock(stateAbove, this))
            {
                level.setBlock(mutable, stateAbove.setValue(TIP, true), Block.UPDATE_ALL);
            }

            for (int i = 0; i < 5; i++)
            {
                mutable.move(0, -1, 0);
                BlockState stateAt = level.getBlockState(mutable);
                if (!stateAt.isAir()) // if we hit a non-air block, we won't be returning
                {
                    final IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, Direction.UP);
                    if (fluidHandler != null)
                    {
                        fluidHandler.fill(new FluidStack(Fluids.WATER, 100), IFluidHandler.FluidAction.EXECUTE);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
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
