/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.mechanical;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.capabilities.power.OldRotationCapability;
import net.dries007.tfc.common.fluids.FluidHelpers;

public class WaterWheelBlock extends ExtendedBlock implements EntityBlockExtension
{
    public static boolean waterWheelValid(Level level, BlockPos pos, BlockState state)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final Direction.Axis axis = state.getValue(AXIS);
        boolean passed = false;
        for (Direction.AxisDirection axisDir : Direction.AxisDirection.values())
        {
            final Direction dir = Direction.get(axisDir, axis);
            cursor.setWithOffset(pos, dir);
            final BlockEntity blockEntity = level.getBlockEntity(cursor);
            if (blockEntity != null)
            {
                if (blockEntity.getCapability(OldRotationCapability.ROTATION).map(rot -> rot.hasShaft(level, cursor, dir.getOpposite())).orElse(false))
                {
                    passed = true;
                    break;
                }
            }
        }
        if (!passed)
        {
            return false;
        }

        final Direction[] sides = DIRECTION_SIDES.get(axis);
        for (int i = -1; i <= 1; i++)
        {
            for (int j = -1; j <= 1; j++)
            {
                if (j == 0 && i == 0)
                {
                    continue;
                }
                cursor.set(pos).move(sides[0], i).move(sides[1], j);
                if (!FluidHelpers.isAirOrEmptyFluid(level.getBlockState(cursor)))
                {
                    return false;
                }
            }
        }
        return true;
    }

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public static final Map<Direction.Axis, Direction[]> DIRECTION_SIDES = Map.of(
        Direction.Axis.X, new Direction[]{Direction.DOWN, Direction.NORTH},
        Direction.Axis.Z, new Direction[]{Direction.WEST, Direction.DOWN}
    );

    public WaterWheelBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(AXIS, Direction.Axis.X));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final BlockState state = defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
        return waterWheelValid(context.getLevel(), context.getClickedPos(), state) ? state : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(AXIS));
    }

    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState pState)
    {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
