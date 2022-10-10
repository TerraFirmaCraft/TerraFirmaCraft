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

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

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

    BiMap<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableBiMap.<Direction, BooleanProperty>builder()
        .put(Direction.UP, BlockStateProperties.UP)
        .put(Direction.DOWN, BlockStateProperties.DOWN)
        .put(Direction.NORTH, BlockStateProperties.NORTH)
        .put(Direction.SOUTH, BlockStateProperties.SOUTH)
        .put(Direction.EAST, BlockStateProperties.EAST)
        .put(Direction.WEST, BlockStateProperties.WEST)
        .build();

    BooleanProperty[] PROPERTIES = new BooleanProperty[] {NORTH, SOUTH, EAST, WEST, UP, DOWN};

    static BooleanProperty getProperty(Direction direction)
    {
        return PROPERTY_BY_DIRECTION.get(direction);
    }

    static Direction getDirection(BooleanProperty property)
    {
        return PROPERTY_BY_DIRECTION.inverse().get(property);
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
}
