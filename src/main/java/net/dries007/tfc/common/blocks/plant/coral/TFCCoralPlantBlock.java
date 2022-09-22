/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.coral;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCDamageSources;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.entities.aquatic.AquaticMob;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for all coral blocks added/duplicated by TFC
 * This includes:
 * - 'coral' blocks, which are the standalone, tall coral models
 * - 'coral fan' blocks, which are the fan item, placed flat
 * - 'coral wall fan' blocks, which are the fan item, placed on the side of a block
 *
 * {@link net.minecraft.world.level.block.CoralBlock}
 */
public class TFCCoralPlantBlock extends Block implements IFluidLoggable
{
    public static final FluidProperty FLUID = TFCBlockStateProperties.SALT_WATER;

    public static final VoxelShape SMALL_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
    public static final VoxelShape BIG_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    private final VoxelShape shape;

    public TFCCoralPlantBlock(VoxelShape shape, BlockBehaviour.Properties properties)
    {
        super(properties);

        this.shape = shape;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(getFluidProperty(), getFluidProperty().keyFor((Helpers.isFluid(fluidstate, FluidTags.WATER) && fluidstate.getAmount() == 8) ? TFCFluids.SALT_WATER.getSource() : Fluids.EMPTY));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(getFluidProperty()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        return facing == Direction.DOWN && !this.canSurvive(state, level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos posBelow = pos.below();
        return level.getBlockState(posBelow).isFaceSturdy(level, posBelow, Direction.UP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return shape;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entityIn)
    {
        if (!(entityIn instanceof AquaticMob))
        {
            entityIn.hurt(TFCDamageSources.CORAL, 0.5F);
        }
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    /**
     * {@link net.minecraft.world.level.block.BaseCoralPlantTypeBlock#tryScheduleDieTick(BlockState, LevelAccessor, BlockPos)}
     */
    protected void tryScheduleDieTick(BlockState state, LevelAccessor level, BlockPos pos)
    {
        if (!scanForWater(state, level, pos))
        {
            level.scheduleTick(pos, this, 60 + level.getRandom().nextInt(40));
        }
    }

    /**
     * {@link net.minecraft.world.level.block.BaseCoralPlantTypeBlock#scanForWater(BlockState, BlockGetter, BlockPos)}
     */
    protected boolean scanForWater(BlockState state, BlockGetter level, BlockPos pos)
    {
        if (Helpers.isFluid(state.getValue(getFluidProperty()).getFluid(), FluidTags.WATER))
        {
            return true;
        }
        else
        {
            for (Direction direction : Helpers.DIRECTIONS)
            {
                if (Helpers.isFluid(level.getFluidState(pos.relative(direction)), FluidTags.WATER))
                {
                    return true;
                }
            }
            return false;
        }
    }
}
