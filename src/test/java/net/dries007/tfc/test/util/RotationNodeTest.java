/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.util;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.util.network.NetworkHelpers;
import net.dries007.tfc.util.network.RotationNode;
import net.dries007.tfc.util.network.RotationOwner;

import static net.minecraft.core.Direction.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RotationNodeTest
{
    @Test
    public void testAxle()
    {
        final var axle = new RotationNode.Axle(owner(), Direction.Axis.X, 0);

        assertEquals(EAST, axle.rotation()); // Default to positive
        assertEquals(EAST, axle.rotation(WEST)); // Reports same direction in both query directions
        assertEquals(EAST, axle.rotation(EAST));
    }

    @Test
    public void testGearBoxDefaultConventions()
    {
        // Default, X -> Y -> Z
        assertEquals(EAST, gearBox().convention()); // X
        assertEquals(EAST, gearBox(UP).convention()); // X; Y blocked
        assertEquals(EAST, gearBox(DOWN, SOUTH).convention()); // X; Y,Z blocked
        assertEquals(UP, gearBox(EAST).convention()); // Y; X blocked
        assertEquals(UP, gearBox(WEST, NORTH).convention()); // Y; X,Z blocked
        assertEquals(SOUTH, gearBox(EAST, DOWN).convention()); // Z; X,Y blocked
    }

    @Test
    public void testGearBoxRotationsWithXAxisPositive()
    {
        final var box = gearBox(NORTH, SOUTH, UP, DOWN); // Y, Z Axis

        assertEquals(EAST, box.convention()); // Positive X
        assertEquals(UP, box.rotation(UP));
        assertEquals(DOWN, box.rotation(DOWN));
        assertEquals(SOUTH, box.rotation(NORTH));
        assertEquals(NORTH, box.rotation(SOUTH));
    }

    @Test
    public void testGearBoxRotationsWithYAxisPositive()
    {
        final var box = gearBox(NORTH, SOUTH, EAST, WEST); // X, Z Axis

        assertEquals(UP, box.convention()); // Positive Y
        assertEquals(SOUTH, box.rotation(SOUTH));
        assertEquals(NORTH, box.rotation(NORTH));
        assertEquals(EAST, box.rotation(WEST));
        assertEquals(WEST, box.rotation(EAST));
    }

    @Test
    public void testGearBoxRotationsWithZAxisPositive()
    {
        final var box = gearBox(UP, DOWN, EAST, WEST); // X, Y Axis

        assertEquals(SOUTH, box.convention()); // Positive Z
        assertEquals(EAST, box.rotation(EAST));
        assertEquals(WEST, box.rotation(WEST));
        assertEquals(DOWN, box.rotation(UP));
        assertEquals(UP, box.rotation(DOWN));
    }

    @Test
    public void testConventionSwitchXtoY()
    {
        final var box = gearBox(NORTH, SOUTH); // Z Blocked, Default X

        box.connections().addAll(List.of(EAST, WEST)); // Add X -> Y
        box.updateConvention();
        assertEquals(DOWN, box.convention());
        assertEquals(SOUTH, box.rotation(NORTH)); // Existing connections must be compatible with default X positive
        assertEquals(NORTH, box.rotation(SOUTH));
    }

    @Test
    public void testConventionSwitchXtoZ()
    {
        final var box = gearBox(UP, DOWN); // Y Blocked, Default X

        box.connections().addAll(List.of(EAST, WEST)); // Add X -> Z
        box.updateConvention();
        assertEquals(NORTH, box.convention());
        assertEquals(EAST, box.rotation(WEST)); // Existing connections must be compatible with default X positive
        assertEquals(WEST, box.rotation(EAST));
    }

    @Test
    public void testConventionSwitchYtoZ()
    {
        final var box = gearBox(EAST, WEST); // X Blocked, Default Y

        box.connections().addAll(List.of(UP, DOWN)); // Add Y -> Z
        box.updateConvention();
        assertEquals(NORTH, box.convention());
        assertEquals(UP, box.rotation(UP)); // Existing connections must be compatible with default Y positive
        assertEquals(DOWN, box.rotation(DOWN));
    }

    @Test
    public void testConventionSwitchXtoYtoX()
    {
        final var box = gearBox(NORTH, SOUTH); // Z Blocked, Default X

        assertEquals(EAST, box.convention());
        box.connections().addAll(List.of(EAST, WEST)); // Add X -> Y
        box.updateConvention();
        box.connections().removeIf(e -> e.getAxis() == Axis.X);
        box.updateConvention();
        assertEquals(EAST, box.convention()); // Default X
    }

    @Test
    public void testConventionSwitchXtoZtoX()
    {
        final var box = gearBox(UP, DOWN); // Y Blocked, Default X

        assertEquals(EAST, box.convention());
        box.connections().addAll(List.of(EAST, WEST)); // Add X -> Z
        box.updateConvention();
        box.connections().removeIf(e -> e.getAxis() == Axis.X);
        box.updateConvention();
        assertEquals(EAST, box.convention());
    }

    private RotationNode.GearBox gearBox(Direction... directions)
    {
        return new RotationNode.GearBox(owner(), NetworkHelpers.of(directions), 0);
    }

    private RotationOwner owner()
    {
        return new RotationOwner() {
            @Override public RotationNode getRotationNode() { throw new AssertionError(); }
            @Override public BlockPos getBlockPos() { return BlockPos.ZERO; }
        };
    }
}
