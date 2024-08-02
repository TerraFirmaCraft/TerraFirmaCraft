/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.block;

import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.blocks.wood.BranchDirection;

import static org.junit.jupiter.api.Assertions.*;

public class BranchDirectionTest
{
    @Test
    public void testRotations()
    {
        assertEquals(BranchDirection.SOUTH, BranchDirection.EAST.rotate(Rotation.CLOCKWISE_90));
        assertEquals(BranchDirection.SOUTH_WEST, BranchDirection.NORTH_WEST.rotate(Rotation.COUNTERCLOCKWISE_90));
        assertEquals(BranchDirection.TRUNK_NORTH_WEST, BranchDirection.TRUNK_SOUTH_EAST.rotate(Rotation.CLOCKWISE_180));
    }

    @Test
    public void testMirrors()
    {
        assertEquals(BranchDirection.NORTH_WEST, BranchDirection.NORTH_EAST.mirror(Mirror.FRONT_BACK));
        assertEquals(BranchDirection.TRUNK_SOUTH_WEST, BranchDirection.TRUNK_NORTH_WEST.mirror(Mirror.LEFT_RIGHT));
    }

    @Test
    public void testSymmetricIdentities()
    {
        for (BranchDirection dir : BranchDirection.values())
        {
            assertEquals(dir, dir.mirror(Mirror.NONE));
            assertEquals(dir, dir.mirror(Mirror.FRONT_BACK).mirror(Mirror.FRONT_BACK));
            assertEquals(dir, dir.mirror(Mirror.LEFT_RIGHT).mirror(Mirror.LEFT_RIGHT));

            assertEquals(dir, dir.rotate(Rotation.NONE));
            assertEquals(dir, dir.rotate(Rotation.CLOCKWISE_90).rotate(Rotation.COUNTERCLOCKWISE_90));
            assertEquals(dir, dir.rotate(Rotation.CLOCKWISE_180).rotate(Rotation.CLOCKWISE_180));

            assertEquals(dir, dir.mirror(Mirror.FRONT_BACK).mirror(Mirror.LEFT_RIGHT).rotate(Rotation.CLOCKWISE_180));
            assertEquals(dir.mirror(Mirror.LEFT_RIGHT), dir.rotate(Rotation.CLOCKWISE_180).mirror(Mirror.FRONT_BACK));
        }
    }
}
