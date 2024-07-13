/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Map;
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
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

public class HorizontalSupportBlock extends VerticalSupportBlock implements IForgeBlockExtension
{
    private final Map<BlockState, VoxelShape> cachedShapes;

    public HorizontalSupportBlock(ExtendedProperties properties)
    {
        super(properties);
        cachedShapes = makeShapes(box(5.0D, 10.0D, 5.0D, 11.0D, 16.0D, 11.0D), getStateDefinition().getPossibleStates());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        Direction direction = null;
        for (Direction checkDir : Direction.Plane.HORIZONTAL)
        {
            mutablePos.set(pos).move(checkDir);
            if (Helpers.isBlock(level.getBlockState(mutablePos), TFCTags.Blocks.SUPPORT_BEAM))
            {
                direction = checkDir.getOpposite();
                break;
            }
        }
        if (direction == null)
        {
            return;
        }

        final int distance = getHorizontalDistance(direction, level, pos);
        if (distance == 0 || stack.getCount() < distance)
        {
            level.destroyBlock(pos, true);
        }
        else if (distance > 0)
        {
            stack.shrink(distance - 1); // first one will be used by BlockItem
            for (int i = 1; i < distance; i++)
            {
                mutablePos.set(pos).move(direction, i);
                final BlockState stateAt = level.getBlockState(mutablePos);
                if (isEmptyOrValidFluid(stateAt))
                {
                    level.setBlock(mutablePos, defaultBlockState().setValue(PROPERTY_BY_DIRECTION.get(direction), true).setValue(PROPERTY_BY_DIRECTION.get(direction.getOpposite()), true).setValue(getFluidProperty(), getFluidProperty().keyForOrEmpty(stateAt.getFluidState().getType())), 2);
                    mutablePos.move(Direction.DOWN);
                    level.scheduleTick(mutablePos, level.getFluidState(mutablePos).getType(), 3);
                }
            }
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        if (facing.getAxis().isHorizontal())
        {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(facing), Helpers.isBlock(facingState, TFCTags.Blocks.SUPPORT_BEAM));
            // if support incomplete, try the other way (E/W vs N/S)
            if (!Helpers.isBlock(facingState, TFCTags.Blocks.SUPPORT_BEAM) || !Helpers.isBlock(level.getBlockState(currentPos.relative(facing.getOpposite())), TFCTags.Blocks.SUPPORT_BEAM))
            {
                // if support incomplete here, we definitely can break
                if (!Helpers.isBlock(level.getBlockState(currentPos.relative(facing.getClockWise())), TFCTags.Blocks.SUPPORT_BEAM)
                    || !Helpers.isBlock(level.getBlockState(currentPos.relative(facing.getCounterClockWise())), TFCTags.Blocks.SUPPORT_BEAM))
                {
                    return Blocks.AIR.defaultBlockState();
                }
            }
        }
        return state;
    }

    /**
     * In 1.16 canPlaceBlockOnSide is no longer a thing, instead we use this to trick ItemBlock into not placing in invalid conditions.
     * This eliminates cases of placing and then immediately breaking.
     */
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            if (getHorizontalDistance(d, level, pos) > 0) // we found a pole it could connect to
            {
                if (Helpers.isBlock(level.getBlockState(pos.relative(d.getOpposite())), TFCTags.Blocks.SUPPORT_BEAM))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        VoxelShape shape = cachedShapes.get(state);
        if (shape != null) return shape;
        throw new IllegalArgumentException("Asked for Support VoxelShape that was not cached");
    }

    private int getHorizontalDistance(Direction direction, LevelReader level, BlockPos pos)
    {
        int distance = -1;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 5; i++)
        {
            cursor.set(pos).move(direction, i);
            final BlockState stateAt = level.getBlockState(cursor);
            if (!Helpers.isBlock(stateAt, TFCTags.Blocks.SUPPORT_BEAM) && !isEmptyOrValidFluid(stateAt))
            {
                return 0;
            }
            cursor.move(direction, 1);
            BlockState state = level.getBlockState(cursor);
            if (Helpers.isBlock(state, TFCTags.Blocks.SUPPORT_BEAM)) // vertical only?
            {
                distance = i;
                break;
            }
        }
        return distance == -1 ? 0 : distance + 1;
    }
}
