package net.dries007.tfc.common.blocks.berrybush;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.tileentity.BerryBushTileEntity;
import net.dries007.tfc.util.Helpers;

public class WaterloggedBerryBushBlock extends StationaryBerryBushBlock implements IFluidLoggable
{
    public static final FluidProperty FLUID = TFCBlockStateProperties.FRESH_WATER;
    public static final BooleanProperty WILD = TFCBlockStateProperties.WILD;

    public WaterloggedBerryBushBlock(ForgeBlockProperties properties, BerryBush bush)
    {
        super(properties, bush);
        registerDefaultState(getStateDefinition().any().setValue(WILD, false).setValue(FLUID, FLUID.keyFor(Fluids.EMPTY)).setValue(LIFECYCLE, Lifecycle.HEALTHY));
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (state.getValue(LIFECYCLE) == Lifecycle.FRUITING)
        {
            return ActionResultType.FAIL; // pick berries by flooding
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void cycle(BerryBushTileEntity te, World world, BlockPos pos, BlockState state, int stage, Lifecycle lifecycle, Random random)
    {
        if (state.getValue(WILD)) return; // prevent wild blocks from spreading
        if (lifecycle == Lifecycle.HEALTHY && state.getFluidState().getType().is(FluidTags.WATER))
        {
            super.cycle(te, world, pos, state, stage, Lifecycle.FLOWERING, random); // cannot grow if its waterlogged so we pretend it flowers so we cant grow (without actually disabling growth forever)
            return;
        }
        super.cycle(te, world, pos, state, stage, lifecycle, random);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        super.randomTick(state, world, pos, random);
        BerryBushTileEntity te = Helpers.getTileEntity(world, pos, BerryBushTileEntity.class);
        if (te == null) return;

        Lifecycle lifecycle = state.getValue(LIFECYCLE);
        Fluid fluid = state.getFluidState().getType();
        if (lifecycle == Lifecycle.DORMANT && !fluid.is(FluidTags.WATER))
        {
            te.setGrowing(false); // need to be waterlogged over the winter
        }
        else if (lifecycle == Lifecycle.FLOWERING && fluid.is(FluidTags.WATER))
        {
            te.setGrowing(false); // if we're flowering and STILL waterlogged, just kill it!
        }
        else if (lifecycle == Lifecycle.FRUITING && fluid.is(FluidTags.WATER))
        {
            Helpers.spawnItem(world, pos, new ItemStack(bush.getBerry()));
            te.setHarvested(true);
            world.setBlockAndUpdate(pos, state.setValue(LIFECYCLE, Lifecycle.DORMANT));
        }
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = worldIn.getBlockState(belowPos);
        return belowState.is(TFCTags.Blocks.BUSH_PLANTABLE_ON) || belowState.is(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON) || this.mayPlaceOn(worldIn.getBlockState(belowPos), worldIn, belowPos);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(LIFECYCLE, STAGE, getFluidProperty(), WILD);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!canSurvive(stateIn, worldIn, currentPos))
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

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER.getSource();
        return defaultBlockState().setValue(getFluidProperty(), flag ? getFluidProperty().keyFor(Fluids.WATER.getSource()) : getFluidProperty().keyFor(Fluids.EMPTY));
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
