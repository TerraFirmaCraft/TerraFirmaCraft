/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class FloatingWaterPlantBlock extends PlantBlock
{
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);
    private final Supplier<? extends Fluid> fluid;

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

    protected FloatingWaterPlantBlock(Properties properties, Supplier<? extends Fluid> fluid)
    {
        super(properties);
        this.fluid = fluid;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockState belowState = worldIn.getBlockState(pos.down());
        return (belowState.getFluidState() != Fluids.EMPTY.getDefaultState() && isValidFluid(belowState.getFluidState().getFluid()));
    }

    /**
     * {  LilyPadBlock#onEntityCollision}
     */
    @Override
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        super.onEntityCollision(state, worldIn, pos, entityIn);
        if (worldIn instanceof ServerWorld && entityIn instanceof BoatEntity)
        {
            worldIn.destroyBlock(new BlockPos(pos), true, entityIn);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    private boolean isValidFluid(Fluid fluidIn)
    {
        return fluidIn.isEquivalentTo(fluid.get());
    }
}
