package net.dries007.tfc.common.blocks.wood;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;

public class HorizontalSupportBlock extends Block implements IForgeBlockProperties
{
    private static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    private static final VoxelShape SHAPE = box(5.0D, 10.0D, 0.0D, 11.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_90 = box(0.0D, 10.0D, 5.0D, 16.0D, 16.0D, 11.0D);

    private final ForgeBlockProperties properties;

    public HorizontalSupportBlock(ForgeBlockProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return state.getValue(AXIS) == Direction.Axis.Z ? SHAPE : SHAPE_90;
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        Direction.Axis axis = state.getValue(AXIS);
        Direction d = Direction.get(Direction.AxisDirection.NEGATIVE, axis);
        if (worldIn.getBlockState(pos.relative(d)).is(TFCTags.Blocks.SUPPORT_BEAM))
        {
            d = d.getOpposite();
        }
        int distance = getHorizontalDistance(d, worldIn, pos);
        if (distance == 0 || stack.getCount() < distance)
        {
            worldIn.destroyBlock(pos, true);
        }
        else if (distance > 0)
        {
            stack.shrink(distance - 1); // first one will be used by IB
            BlockPos.Mutable mutablePos = new BlockPos.Mutable();
            for (int i = 1; i < distance; i++)
            {
                mutablePos.set(pos).move(d, i);
                if (worldIn.getBlockState(mutablePos).getMaterial().isReplaceable())
                {
                    worldIn.setBlock(mutablePos, defaultBlockState().setValue(AXIS, axis), 2);
                    mutablePos.move(Direction.DOWN);
                    worldIn.getBlockTicks().scheduleTick(mutablePos, worldIn.getBlockState(mutablePos).getBlock(), 3);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing.getAxis().isHorizontal())
        {
            // if support incomplete, try the other way (E/W vs N/S)
            if (!facingState.is(TFCTags.Blocks.SUPPORT_BEAM) || !world.getBlockState(currentPos.relative(facing.getOpposite())).is(TFCTags.Blocks.SUPPORT_BEAM))
            {
                // if support incomplete here, we definitely can break
                if (!world.getBlockState(currentPos.relative(facing.getClockWise())).is(TFCTags.Blocks.SUPPORT_BEAM)
                    || !world.getBlockState(currentPos.relative(facing.getCounterClockWise())).is(TFCTags.Blocks.SUPPORT_BEAM))
                {
                    return Blocks.AIR.defaultBlockState();
                }
            }
        }
        return stateIn;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(AXIS));
    }

    private int getHorizontalDistance(Direction d, IWorld world, BlockPos pos)
    {
        int distance = -1;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int i = 0; i < 5; i++)
        {
            mutablePos.set(pos).move(d, i);
            if (!world.getBlockState(mutablePos).is(TFCTags.Blocks.SUPPORT_BEAM) && !world.isEmptyBlock(mutablePos))
            {
                return 0;
            }
            mutablePos.move(d, 1);
            BlockState state = world.getBlockState(mutablePos);
            if (state.is(TFCTags.Blocks.SUPPORT_BEAM)) // vertical only?
            {
                distance = i;
                break;
            }
        }
        return distance == -1 ? 0 : distance + 1;
    }
}
