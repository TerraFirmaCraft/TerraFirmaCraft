/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.coral;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCDamageSources;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.entities.aquatic.AquaticMob;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.util.Helpers;

/**
 * Base class for all coral blocks added/duplicated by TFC
 * This includes:
 * <ul>
 *     <li>'coral' blocks, which are the standalone, tall coral models</li>
 *     <li>'coral fan' blocks, which are the fan item, placed flat</li>
 *     <li>'coral wall fan' blocks, which are the fan item, placed on the side of a block</li>
 * </ul>
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
        final FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState().setValue(getFluidProperty(), getFluidProperty().keyForOrEmpty(fluid.getType()));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(getFluidProperty()));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        return facing == Direction.DOWN && !this.canSurvive(state, level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos posBelow = pos.below();
        return level.getBlockState(posBelow).isFaceSturdy(level, posBelow, Direction.UP);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return shape;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if (!(entity instanceof AquaticMob))
        {
            TFCDamageSources.coral(entity, 0.5f);
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
        if (Helpers.isFluid(state.getValue(getFluidProperty()).getFluid(), TFCTags.Fluids.ANY_INFINITE_WATER))
        {
            return true;
        }
        else
        {
            for (Direction direction : Helpers.DIRECTIONS)
            {
                if (Helpers.isFluid(level.getFluidState(pos.relative(direction)), TFCTags.Fluids.ANY_INFINITE_WATER))
                {
                    return true;
                }
            }
            return false;
        }
    }
}
