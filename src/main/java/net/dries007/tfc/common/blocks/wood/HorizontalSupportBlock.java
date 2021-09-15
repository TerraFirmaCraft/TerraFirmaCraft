/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;

public class HorizontalSupportBlock extends VerticalSupportBlock implements IForgeBlockExtension
{
    private final Map<BlockState, VoxelShape> SHAPE_BY_STATE;

    public HorizontalSupportBlock(ExtendedProperties properties)
    {
        super(properties);
        SHAPE_BY_STATE = makeShapes(box(5.0D, 10.0D, 5.0D, 11.0D, 16.0D, 11.0D), getStateDefinition().getPossibleStates());
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        Direction d = null;
        for (Direction checkDir : Direction.Plane.HORIZONTAL)
        {
            mutablePos.set(pos).move(checkDir);
            if (worldIn.getBlockState(mutablePos).is(TFCTags.Blocks.SUPPORT_BEAM))
            {
                d = checkDir.getOpposite();
                break;
            }
        }
        if (d == null) return;

        int distance = getHorizontalDistance(d, worldIn, pos);
        if (distance == 0 || stack.getCount() < distance)
        {
            worldIn.destroyBlock(pos, true);
        }
        else if (distance > 0)
        {
            stack.shrink(distance - 1); // first one will be used by IB
            for (int i = 1; i < distance; i++)
            {
                mutablePos.set(pos).move(d, i);
                if (worldIn.getBlockState(mutablePos).getMaterial().isReplaceable())
                {
                    worldIn.setBlock(mutablePos, defaultBlockState().setValue(PROPERTY_BY_DIRECTION.get(d), true).setValue(PROPERTY_BY_DIRECTION.get(d.getOpposite()), true), 2);
                    mutablePos.move(Direction.DOWN);
                    worldIn.getLiquidTicks().scheduleTick(mutablePos, worldIn.getFluidState(mutablePos).getType(), 3);
                }
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing.getAxis().isHorizontal())
        {
            stateIn = stateIn.setValue(PROPERTY_BY_DIRECTION.get(facing), facingState.is(TFCTags.Blocks.SUPPORT_BEAM));
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

    /**
     * In 1.16 canPlaceBlockOnSide is no longer a thing, instead we use this to trick ItemBlock into not placing in invalid conditions.
     * This eliminates cases of placing and then immediately breaking.
     */
    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            if (getHorizontalDistance(d, worldIn, pos) > 0) // we found a pole it could connect to
            {
                if (worldIn.getBlockState(pos.relative(d.getOpposite())).is(TFCTags.Blocks.SUPPORT_BEAM))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        VoxelShape shape = SHAPE_BY_STATE.get(state);
        if (shape != null) return shape;
        throw new IllegalArgumentException("Asked for Support VoxelShape that was not cached");
    }

    private int getHorizontalDistance(Direction d, LevelReader world, BlockPos pos)
    {
        int distance = -1;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
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
