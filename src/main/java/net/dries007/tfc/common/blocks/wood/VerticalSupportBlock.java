package net.dries007.tfc.common.blocks.wood;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SixWayBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockVoxelShape;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;

public class VerticalSupportBlock extends Block implements IForgeBlockProperties
{
    private static final BooleanProperty NORTH = SixWayBlock.NORTH;
    private static final BooleanProperty EAST = SixWayBlock.EAST;
    private static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
    private static final BooleanProperty WEST = SixWayBlock.WEST;
    protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = SixWayBlock.PROPERTY_BY_DIRECTION.entrySet().stream()
        .filter(facing -> facing.getKey().getAxis().isHorizontal()).collect(Util.toMap());
    private final Map<BlockState, VoxelShape> SHAPE_BY_STATE;

    private final ForgeBlockProperties properties;

    public VerticalSupportBlock(ForgeBlockProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
        SHAPE_BY_STATE = makeShapes(box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D), getStateDefinition().getPossibleStates());
        registerDefaultState(getStateDefinition().any().setValue(NORTH, false).setValue(EAST, false).setValue(WEST, false).setValue(SOUTH, false));
    }

    protected Map<BlockState, VoxelShape> makeShapes(VoxelShape middleShape, ImmutableList<BlockState> possibleStates)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        for (BlockState state : possibleStates)
        {
            VoxelShape shape = middleShape;
            for (Direction d : Direction.Plane.HORIZONTAL)
            {
                if (state.getValue(PROPERTY_BY_DIRECTION.get(d)))
                {
                    VoxelShape joinShape = VoxelShapes.empty();
                    switch (d)
                    {
                        case NORTH:
                            joinShape = box(5.0D, 10.0D, 0.0D, 11.0D, 16.0D, 10.0D);
                            break;
                        case SOUTH:
                            joinShape = box(5.0D, 10.0D, 11.0D, 11.0D, 16.0D, 16.0D);
                            break;
                        case EAST:
                            joinShape = box(11.0D, 10.0D, 5.0D, 16.0D, 16.0D, 11.0D);
                            break;
                        case WEST:
                            joinShape = box(0.0D, 10.0D, 5.0D, 5.0D, 16.0D, 11.0D);
                            break;
                    }
                    shape = VoxelShapes.or(shape, joinShape);
                }
            }
            builder.put(state, shape);
        }
        return builder.build();
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if (worldIn.isClientSide() || placer == null) return;
        if (stack.getCount() > 2 && !placer.isShiftKeyDown()) // need two because the item block hasn't shrunk the stack yet
        {
            BlockPos above = pos.above();
            BlockPos above2 = above.above();
            if (worldIn.isEmptyBlock(above) && worldIn.isEmptyBlock(above2))
            {
                if (worldIn.getEntities(null, new AxisAlignedBB(above)).isEmpty())
                {
                    worldIn.setBlock(above, defaultBlockState(), 2);
                    if (worldIn.getEntities(null, new AxisAlignedBB(above2)).isEmpty())
                    {
                        worldIn.setBlock(above2, defaultBlockState(), 2);
                        stack.shrink(2);
                    }
                    else
                    {
                        stack.shrink(1);
                    }
                }
            }
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = defaultBlockState();
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            mutablePos.setWithOffset(context.getClickedPos(), d);
            state = state.setValue(PROPERTY_BY_DIRECTION.get(d), context.getLevel().getBlockState(mutablePos).is(TFCTags.Blocks.SUPPORT_BEAM));
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing.getAxis().isHorizontal())
        {
            stateIn = stateIn.setValue(PROPERTY_BY_DIRECTION.get(facing), facingState.is(TFCTags.Blocks.SUPPORT_BEAM));
        }
        else if (facing == Direction.DOWN)
        {
            if (facingState.is(TFCTags.Blocks.SUPPORT_BEAM) || facingState.isFaceSturdy(world, facingPos, Direction.UP, BlockVoxelShape.CENTER))
            {
                return stateIn;
            }
            return Blocks.AIR.defaultBlockState();
        }
        return stateIn;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = worldIn.getBlockState(belowPos);
        return belowState.is(TFCTags.Blocks.SUPPORT_BEAM) || belowState.isFaceSturdy(worldIn, belowPos, Direction.UP, BlockVoxelShape.CENTER);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape shape = SHAPE_BY_STATE.get(state);
        if (shape != null) return shape;
        throw new IllegalArgumentException("Asked for Support VoxelShape that was not cached");
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }
}
