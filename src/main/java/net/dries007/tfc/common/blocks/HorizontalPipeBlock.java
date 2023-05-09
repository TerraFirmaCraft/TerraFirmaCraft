/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public interface HorizontalPipeBlock
{
    Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream()
        .filter(facing -> facing.getKey().getAxis().isHorizontal()).collect(Util.toMap());

    BooleanProperty NORTH = PipeBlock.NORTH;
    BooleanProperty EAST = PipeBlock.EAST;
    BooleanProperty SOUTH = PipeBlock.SOUTH;
    BooleanProperty WEST = PipeBlock.WEST;

}
