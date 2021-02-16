/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;

public class GroundcoverBlock extends Block implements IFluidLoggable
{
    public static final FluidProperty FLUID = TFCBlockStateProperties.WATER;
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    public static final VoxelShape FLAT = makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);
    public static final VoxelShape SMALL = makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D);
    public static final VoxelShape MEDIUM = makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 4.0D, 11.0D);
    public static final VoxelShape PIXEL_HIGH = makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    public static final VoxelShape TWIG = makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);

    public static GroundcoverBlock twig(Properties properties)
    {
        return new GroundcoverBlock(properties, TWIG, null);
    }

    public static GroundcoverBlock looseOre(Properties properties)
    {
        return new GroundcoverBlock(properties, SMALL, null);
    }

    private final VoxelShape shape;
    @Nullable private final Supplier<? extends Item> pickBlock;

    public GroundcoverBlock(GroundcoverBlockType cover)
    {
        this(Properties.create(Material.PLANTS).hardnessAndResistance(0.05F, 0.0F).sound(SoundType.NETHER_WART).notSolid(), cover.getShape(), cover.getVanillaItem());
    }

    public GroundcoverBlock(Properties properties, VoxelShape shape, @Nullable Supplier<? extends Item> pickBlock)
    {
        super(properties);

        this.shape = shape;
        this.pickBlock = pickBlock;

        setDefaultState(getDefaultState().any().with(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)).with(FACING, Direction.EAST));
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        final FluidState fluidState = context.getWorld().getFluidState(context.getPos());

        BlockState state = getDefaultState().with(FACING, context.getHorizontalDirection().getOpposite());
        if (getFluidProperty().canContain(fluidState.getFluid()))
        {
            return state.with(getFluidProperty(), getFluidProperty().keyFor(fluidState.getFluid()));
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
        if (!stateIn.canBeReplacedByLeaves(worldIn, currentPos))
        {
            return Blocks.AIR.getDefaultState();
        }
        else
        {
            final Fluid containedFluid = stateIn.get(getFluidProperty()).getFluid();
            if (containedFluid != Fluids.EMPTY)
            {
                worldIn.getPendingFluidTicks().scheduleTick(currentPos, containedFluid, containedFluid.getTickRate(worldIn));
            }
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        worldIn.destroyBlock(pos, false);
        if (!player.isCreative() && worldIn instanceof ServerWorld)
        {
            TileEntity tileEntity = state.hasTileEntity() ? worldIn.getTileEntity(pos) : null;
            getDrops(state, (ServerWorld) worldIn, pos, tileEntity, null, ItemStack.EMPTY).forEach(stackToSpawn -> ItemHandlerHelper.giveItemToPlayer(player, stackToSpawn));
        }
        return ActionResultType.SUCCESS;
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
        return worldIn.getBlockState(pos.down()).isSolidSide(worldIn, pos, Direction.UP);
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

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return pickBlock != null ? new ItemStack(pickBlock.get()) : super.getPickBlock(state, target, world, pos, player);
    }
}
