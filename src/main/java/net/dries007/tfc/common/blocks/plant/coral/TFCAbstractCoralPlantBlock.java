package net.dries007.tfc.common.blocks.plant.coral;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.fluids.TFCFluids;

/**
 * {@link net.minecraft.block.AbstractCoralPlantBlock}
 */
public class TFCAbstractCoralPlantBlock extends Block implements IFluidLoggable
{
    public static final FluidProperty FLUID = TFCBlockStateProperties.SALT_WATER;
    private static final VoxelShape AABB = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

    public TFCAbstractCoralPlantBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any());
    }

    protected void tryScheduleDieTick(BlockState state, IWorld worldIn, BlockPos pos)
    {
        if (!scanForWater(state, worldIn, pos))
        {
            worldIn.getBlockTicks().scheduleTick(pos, this, 60 + worldIn.getRandom().nextInt(40));
        }

    }

    protected boolean scanForWater(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        if (state.getValue(getFluidProperty()).getFluid().is(FluidTags.WATER))
        {
            return true;
        }
        else
        {
            for (Direction direction : Direction.values())
            {
                if (worldIn.getFluidState(pos.relative(direction)).is(FluidTags.WATER))
                {
                    return true;
                }
            }
            return false;
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(getFluidProperty(), getFluidProperty().keyFor((fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8) ? TFCFluids.SALT_WATER.getSource() : Fluids.EMPTY));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return AABB;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (stateIn.getValue(getFluidProperty()).getFluid().is(FluidTags.WATER))
        {
            worldIn.getLiquidTicks().scheduleTick(currentPos, TFCFluids.SALT_WATER.getSource(), TFCFluids.SALT_WATER.getSource().getTickDelay(worldIn));
        }
        return facing == Direction.DOWN && !this.canSurvive(stateIn, worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockPos blockpos = pos.below();
        return worldIn.getBlockState(blockpos).isFaceSturdy(worldIn, blockpos, Direction.UP);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        entityIn.hurt(DamageSource.CACTUS, 1.0F);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(getFluidProperty());
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }
}
