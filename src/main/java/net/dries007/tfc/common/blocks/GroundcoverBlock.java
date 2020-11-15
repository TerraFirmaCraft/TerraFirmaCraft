package net.dries007.tfc.common.blocks;

import javax.annotation.Nonnull;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;

public class GroundcoverBlock extends Block implements IFluidLoggable
{
    public static final FluidProperty FLUID = TFCBlockStateProperties.WATER;
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    public static final VoxelShape FLAT = box(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);
    public static final VoxelShape SMALL = box(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D);
    public static final VoxelShape MEDIUM = box(5.0D, 0.0D, 5.0D, 11.0D, 4.0D, 11.0D);
    public static final VoxelShape PIXEL_HIGH = box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    public static final VoxelShape TWIG = box(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);

    public static GroundcoverBlock twig(Properties properties)
    {
        return new GroundcoverBlock(properties, TWIG);
    }

    public static GroundcoverBlock looseOre(Properties properties)
    {
        return new GroundcoverBlock(properties, SMALL);
    }

    private final VoxelShape shape;

    public GroundcoverBlock(GroundcoverBlockType cover)
    {
        this(Properties.of(Material.GRASS).strength(0.05F, 0.0F).sound(SoundType.NETHER_WART).noOcclusion(), cover.getShape());
    }

    public GroundcoverBlock(Properties properties, VoxelShape shape)
    {
        super(properties);

        this.shape = shape;

        registerDefaultState(getStateDefinition().any().setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)).setValue(FACING, Direction.EAST));
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        final FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());

        BlockState state = defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        if (getFluidProperty().canContain(fluidState.getType()))
        {
            return state.setValue(getFluidProperty(), getFluidProperty().keyFor(fluidState.getType()));
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, getFluidProperty());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!stateIn.canSurvive(worldIn, currentPos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        else
        {
            final Fluid containedFluid = stateIn.getValue(getFluidProperty()).getFluid();
            if (containedFluid != Fluids.EMPTY)
            {
                worldIn.getLiquidTicks().scheduleTick(currentPos, containedFluid, containedFluid.getTickDelay(worldIn));
            }
            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (player.getMainHandItem().isEmpty())
        {
            worldIn.destroyBlock(pos, (!player.isCreative()));
        }
        return ActionResultType.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.below()).isFaceSturdy(worldIn, pos, Direction.UP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return shape;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }
}
