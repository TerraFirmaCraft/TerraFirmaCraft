/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class FloatingWaterPlantBlock extends PlantBlock
{
    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

    public static FloatingWaterPlantBlock create(IPlant plant, Supplier<? extends Fluid> fluid, Properties properties)
    {
        return new FloatingWaterPlantBlock(properties, fluid)
        {
            @Override
            public IPlant getPlant()
            {
                return plant;
            }
        };
    }

    private final Supplier<? extends Fluid> fluid;

    protected FloatingWaterPlantBlock(Properties properties, Supplier<? extends Fluid> fluid)
    {
        super(properties);
        this.fluid = fluid;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        BlockState belowState = worldIn.getBlockState(pos.below());
        return (belowState.getFluidState() != Fluids.EMPTY.defaultFluidState() && isValidFluid(belowState.getFluidState().getType()));
    }

    /**
     * {@link net.minecraft.world.level.block.WaterlilyBlock#entityInside}
     */
    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn)
    {
        super.entityInside(state, worldIn, pos, entityIn);
        if (worldIn instanceof ServerLevel && entityIn instanceof Boat)
        {
            worldIn.destroyBlock(new BlockPos(pos), true, entityIn);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    private boolean isValidFluid(Fluid fluidIn)
    {
        return fluidIn.isSame(fluid.get());
    }
}
