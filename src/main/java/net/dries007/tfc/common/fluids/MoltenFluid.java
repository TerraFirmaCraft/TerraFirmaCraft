/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.LavaFluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import org.jetbrains.annotations.Nullable;

public abstract class MoltenFluid extends BaseFlowingFluid
{
    private final LavaFluid lava;

    protected MoltenFluid(Properties properties)
    {
        super(properties);
        this.lava = (LavaFluid) Fluids.LAVA;
    }

    @Override
    protected void animateTick(Level worldIn, BlockPos pos, FluidState state, RandomSource random)
    {
        lava.animateTick(worldIn, pos, state, random);
    }

    @Override
    protected void randomTick(Level worldIn, BlockPos pos, FluidState state, RandomSource random)
    {
        lava.randomTick(worldIn, pos, state, random);
    }

    @Nullable
    @Override
    protected ParticleOptions getDripParticle()
    {
        return lava.getDripParticle();
    }

    @Override
    protected boolean isRandomlyTicking()
    {
        return true;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor worldIn, BlockPos pos, BlockState state)
    {
        worldIn.levelEvent(1501, pos, 0);
    }

    @Override
    protected int getSlopeFindDistance(LevelReader worldIn)
    {
        return lava.getSlopeFindDistance(worldIn);
    }

    @Override
    protected int getDropOff(LevelReader worldIn)
    {
        return lava.getDropOff(worldIn);
    }

    @Override
    public int getTickDelay(LevelReader world)
    {
        return lava.getTickDelay(world);
    }

    @Override
    protected int getSpreadDelay(Level worldIn, BlockPos pos, FluidState fluidState_, FluidState fluidState1_)
    {
        return lava.getSpreadDelay(worldIn, pos, fluidState_, fluidState1_);
    }

    public static class Flowing extends MoltenFluid
    {
        public Flowing(Properties properties)
        {
            super(properties);
        }

        public boolean isSource(FluidState state)
        {
            return false;
        }

        public int getAmount(FluidState state)
        {
            return state.getValue(LEVEL);
        }

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder)
        {
            super.createFluidStateDefinition(builder.add(LEVEL));
        }
    }

    public static class Source extends MoltenFluid
    {
        public Source(Properties properties)
        {
            super(properties);
        }

        public boolean isSource(FluidState state)
        {
            return true;
        }

        public int getAmount(FluidState state)
        {
            return 8;
        }
    }
}
