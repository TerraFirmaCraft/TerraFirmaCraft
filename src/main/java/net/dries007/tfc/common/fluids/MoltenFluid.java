/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class MoltenFluid extends ForgeFlowingFluid
{
    private final LavaFluid lava;

    protected MoltenFluid(Properties properties)
    {
        super(properties);
        this.lava = (LavaFluid) Fluids.LAVA;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void animateTick(World worldIn, BlockPos pos, FluidState state, Random random)
    {
        lava.animateTick(worldIn, pos, state, random);
    }

    @Override
    protected void randomTick(World worldIn, BlockPos pos, FluidState state, Random random)
    {
        lava.randomTick(worldIn, pos, state, random);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    @Override
    protected IParticleData getDripParticle()
    {
        return lava.getDripParticle();
    }

    @Override
    protected boolean isRandomlyTicking()
    {
        return true;
    }

    @Override
    protected void beforeDestroyingBlock(IWorld worldIn, BlockPos pos, BlockState state)
    {
        worldIn.levelEvent(1501, pos, 0);
    }

    @Override
    protected int getSlopeFindDistance(IWorldReader worldIn)
    {
        return lava.getSlopeFindDistance(worldIn);
    }

    @Override
    protected int getDropOff(IWorldReader worldIn)
    {
        return lava.getDropOff(worldIn);
    }

    @Override
    public int getTickDelay(IWorldReader world)
    {
        return lava.getTickDelay(world);
    }

    @Override
    protected int getSpreadDelay(World worldIn, BlockPos pos, FluidState fluidState_, FluidState fluidState1_)
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
            return state.get(LEVEL);
        }

        protected void createFluidStateDefinition(StateContainer.Builder<Fluid, FluidState> builder)
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
