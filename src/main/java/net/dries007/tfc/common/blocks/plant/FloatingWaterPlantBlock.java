/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

    public static FloatingWaterPlantBlock create(IPlant plant, Supplier<? extends Fluid> fluid, Properties properties)
    {
        return new FloatingWaterPlantBlock(properties)
        {
            @Override
            public IPlant getPlant()
            {
                return plant;
            }

            @Override
            public Fluid getFavoriteFluid()
            {
                return fluid.get();
            }
        };
    }

    protected FloatingWaterPlantBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockState belowState = worldIn.getBlockState(pos.below());
        return (belowState.getFluidState() != Fluids.EMPTY.defaultFluidState() && belowState.getFluidState().getType() == getFavoriteFluid());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        super.entityInside(state, worldIn, pos, entityIn); // lifted from LilyPadBlock
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

    public abstract Fluid getFavoriteFluid();
}
