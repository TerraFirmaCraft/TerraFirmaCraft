/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * Implementing this interface in a {@link Block} class is a sufficient condition for said block to have all the below properties.
 * The static helper methods can be used without implementing this class, if the block does not use all sides.
 */
public interface DirectionPropertyBlock
{
    BooleanProperty UP = BlockStateProperties.UP;
    BooleanProperty DOWN = BlockStateProperties.DOWN;
    BooleanProperty NORTH = BlockStateProperties.NORTH;
    BooleanProperty EAST = BlockStateProperties.EAST;
    BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    BooleanProperty WEST = BlockStateProperties.WEST;

    ImmutableMap<BooleanProperty, Direction> TO_DIRECTION = ImmutableMap.<BooleanProperty, Direction>builder()
        .put(BlockStateProperties.DOWN, Direction.DOWN)
        .put(BlockStateProperties.UP, Direction.UP)
        .put(BlockStateProperties.NORTH, Direction.NORTH)
        .put(BlockStateProperties.SOUTH, Direction.SOUTH)
        .put(BlockStateProperties.WEST, Direction.WEST)
        .put(BlockStateProperties.EAST, Direction.EAST)
        .build();

    /**
     * Matches order declared in {@link Direction} so we can use {@link Enum#ordinal()} to access.
     */
    BooleanProperty[] PROPERTIES = new BooleanProperty[] {DOWN, UP, NORTH, SOUTH, WEST, EAST};

    @NotNull
    static BooleanProperty getProperty(Direction direction)
    {
        return PROPERTIES[direction.ordinal()];
    }

    @NotNull
    @SuppressWarnings("ConstantConditions")
    static Direction getDirection(BooleanProperty property)
    {
        return TO_DIRECTION.get(property);
    }

    static BlockState setAllDirections(BlockState state, boolean value)
    {
        for (BooleanProperty property : PROPERTIES)
        {
            state = state.setValue(property, value);
        }
        return state;
    }

    static Map<BlockState, VoxelShape> makeShapeCache(StateDefinition<Block, BlockState> stateDefinition, Function<BooleanProperty, VoxelShape> toShape)
    {
        return stateDefinition.getPossibleStates()
            .stream()
            .collect(Collectors.toUnmodifiableMap(
                    Function.identity(),
                    state -> Arrays.stream(PROPERTIES)
                        .filter(state::getValue)
                        .map(toShape)
                        .reduce(Shapes::or)
                        .orElseGet(Shapes::empty)
                )
            );
    }

    static BlockState rotate(BlockState state, Rotation rot)
    {
        return switch (rot)
            {
                case CLOCKWISE_90 -> state.setValue(NORTH, state.getValue(WEST))
                    .setValue(EAST, state.getValue(NORTH))
                    .setValue(SOUTH, state.getValue(EAST))
                    .setValue(WEST, state.getValue(SOUTH));
                case CLOCKWISE_180 -> state.setValue(NORTH, state.getValue(SOUTH))
                    .setValue(EAST, state.getValue(WEST))
                    .setValue(SOUTH, state.getValue(NORTH))
                    .setValue(WEST, state.getValue(EAST));
                case COUNTERCLOCKWISE_90 -> state.setValue(NORTH, state.getValue(EAST))
                    .setValue(EAST, state.getValue(SOUTH))
                    .setValue(SOUTH, state.getValue(WEST))
                    .setValue(WEST, state.getValue(NORTH));
                default -> state;
            };
    }

    static BlockState mirror(BlockState state, Mirror mirror)
    {
        return switch (mirror)
            {
                case LEFT_RIGHT -> state.setValue(NORTH, state.getValue(SOUTH))
                    .setValue(SOUTH, state.getValue(NORTH));
                case FRONT_BACK -> state.setValue(EAST, state.getValue(WEST))
                    .setValue(WEST, state.getValue(EAST));
                default -> state;
            };
    }
}
