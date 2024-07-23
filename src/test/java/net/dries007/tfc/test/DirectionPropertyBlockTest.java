/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.blocks.DirectionPropertyBlock;

import static org.junit.jupiter.api.Assertions.*;

public class DirectionPropertyBlockTest
{
    @Test
    public void testGetPropertyThenGetDirectionIsInjective()
    {
        for (Direction direction : Direction.values())
        {
            assertEquals(direction, DirectionPropertyBlock.getDirection(DirectionPropertyBlock.getProperty(direction)));
        }
    }

    @Test
    public void testGetDirectionThenGetPropertyIsInjective()
    {
        for (BooleanProperty property : DirectionPropertyBlock.PROPERTIES)
        {
            assertEquals(property, DirectionPropertyBlock.getProperty(DirectionPropertyBlock.getDirection(property)));
        }
    }
}
