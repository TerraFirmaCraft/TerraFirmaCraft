/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.util.network.Action;
import net.dries007.tfc.util.network.NetworkHelpers;
import net.dries007.tfc.util.network.RotationNetworkManager;
import net.dries007.tfc.util.network.RotationNode;
import net.dries007.tfc.util.network.RotationOwner;

import static net.minecraft.core.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

public class RotationNetworkTest
{
    // ===== Connectivity Tests - add ===== //

    @Test
    public void testEmpty()
    {
        assertEquals("", dsl().toString());
    }

    @Test
    public void testSingleAxle()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Z));
        assertEquals("""
            [network=0]
            Axle[connections=[north, south], pos=[0, 0, 0], network=0, axis=z, rotation=south]
            """, dsl.toString());
    }

    @Test
    public void testSingleAxleInYAxis()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Y));
        assertEquals("""
            [network=0]
            Axle[connections=[down, up], pos=[0, 0, 0], network=0, axis=y, rotation=up]
            """, dsl.toString());
    }

    @Test
    public void testTwoAxlesNotConnected()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Z));
        assertTrue(dsl.axle(0, 0, 2, Axis.Z));
        assertEquals("""
            [network=0]
            Axle[connections=[north, south], pos=[0, 0, 0], network=0, axis=z, rotation=south]
            
            [network=1]
            Axle[connections=[north, south], pos=[0, 0, 2], network=1, axis=z, rotation=south]
            """, dsl.toString());
    }

    @Test
    public void testTwoAxlesAdjacentNotConnected()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.X));
        assertTrue(dsl.axle(0, 0, 1, Axis.X));
        assertEquals("""
            [network=0]
            Axle[connections=[west, east], pos=[0, 0, 0], network=0, axis=x, rotation=east]
            
            [network=1]
            Axle[connections=[west, east], pos=[0, 0, 1], network=1, axis=x, rotation=east]
            """, dsl.toString());
    }

    @Test
    public void testTwoAxlesConnected()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Z));
        assertTrue(dsl.axle(0, 0, 1, Axis.Z));
        assertEquals("""
            [network=0]
            Axle[connections=[north, south], pos=[0, 0, 0], network=0, axis=z, rotation=south]
            Axle[connections=[north, south], pos=[0, 0, 1], network=0, axis=z, rotation=south]
            """, dsl.toString());
    }

    @Test
    public void testTwoAxlesConnectedInYAxis()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Y));
        assertTrue(dsl.axle(0, 1, 0, Axis.Y));
        assertEquals("""
            [network=0]
            Axle[connections=[down, up], pos=[0, 0, 0], network=0, axis=y, rotation=up]
            Axle[connections=[down, up], pos=[0, 1, 0], network=0, axis=y, rotation=up]
            """, dsl.toString());
    }

    @Test
    public void testThreeAxlesConnected()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Z));
        assertTrue(dsl.axle(0, 0, 1, Axis.Z));
        assertTrue(dsl.axle(0, 0, 2, Axis.Z));
        assertEquals("""
            [network=0]
            Axle[connections=[north, south], pos=[0, 0, 0], network=0, axis=z, rotation=south]
            Axle[connections=[north, south], pos=[0, 0, 1], network=0, axis=z, rotation=south]
            Axle[connections=[north, south], pos=[0, 0, 2], network=0, axis=z, rotation=south]
            """, dsl.toString());
    }

    @Test
    public void testThreeAxlesConnectingInMiddle()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Z));
        assertTrue(dsl.axle(0, 0, 2, Axis.Z));
        assertTrue(dsl.axle(0, 0, 1, Axis.Z));
        assertEquals("""
            [network=0]
            Axle[connections=[north, south], pos=[0, 0, 0], network=0, axis=z, rotation=south]
            Axle[connections=[north, south], pos=[0, 0, 1], network=0, axis=z, rotation=south]
            Axle[connections=[north, south], pos=[0, 0, 2], network=0, axis=z, rotation=south]
            """, dsl.toString());
    }

    @Test
    public void testThreeAxlesConnectingInMiddleReversed()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 2, Axis.Z));
        assertTrue(dsl.axle(0, 0, 0, Axis.Z));
        assertTrue(dsl.axle(0, 0, 1, Axis.Z));
        assertEquals("""
            [network=1]
            Axle[connections=[north, south], pos=[0, 0, 0], network=1, axis=z, rotation=south]
            Axle[connections=[north, south], pos=[0, 0, 1], network=1, axis=z, rotation=south]
            Axle[connections=[north, south], pos=[0, 0, 2], network=1, axis=z, rotation=south]
            """, dsl.toString());
    }

    // ===== Connectivity - update ===== //

    @Test
    public void testUpdateConnectsToLeaf()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Y));
        assertTrue(dsl.gearBox(0, 1, 0));
        assertTrue(dsl.update(0, 1, 0, c -> c.add(DOWN)));
        assertEquals("""
            [network=0]
            Axle[connections=[down, up], pos=[0, 0, 0], network=0, axis=y, rotation=up]
            GearBox[connections=[down], pos=[0, 1, 0], network=0, convention=west, rotation=[up]]
            """, dsl.toString());
    }

    @Test
    public void testUpdateDisconnectsToLeaf()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Y));
        assertTrue(dsl.gearBox(0, 1, 0, DOWN));
        assertTrue(dsl.update(0, 1, 0, c -> c.remove(DOWN)));
        assertEquals("""
            [network=0]
            Axle[connections=[down, up], pos=[0, 0, 0], network=0, axis=y, rotation=up]
            
            [network=1]
            GearBox[connections=[], pos=[0, 1, 0], network=1, convention=west, rotation=[]]
            """, dsl.toString());
    }

    @Test
    public void testUpdateConnectsTwoNetworks()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Y));
        assertTrue(dsl.axle(0, 2, 0, Axis.Y));
        assertTrue(dsl.gearBox(0, 1, 0));
        assertTrue(dsl.update(0, 1, 0, c -> c.addAll(List.of(DOWN, UP))));
        assertEquals("""
            [network=0]
            Axle[connections=[down, up], pos=[0, 0, 0], network=0, axis=y, rotation=up]
            GearBox[connections=[down, up], pos=[0, 1, 0], network=0, convention=west, rotation=[up, down]]
            Axle[connections=[down, up], pos=[0, 2, 0], network=0, axis=y, rotation=down]
            """, dsl.toString());
    }

    @Test
    public void testUpdateDisconnectsTwoNetworks()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Y));
        assertTrue(dsl.axle(0, 2, 0, Axis.Y));
        assertTrue(dsl.gearBox(0, 1, 0, DOWN, UP));
        assertTrue(dsl.update(0, 1, 0, Collection::clear));
        assertEquals("""
            [network=0]
            Axle[connections=[down, up], pos=[0, 0, 0], network=0, axis=y, rotation=up]
            
            [network=2]
            Axle[connections=[down, up], pos=[0, 2, 0], network=2, axis=y, rotation=down]
            
            [network=3]
            GearBox[connections=[], pos=[0, 1, 0], network=3, convention=west, rotation=[]]
            """, dsl.toString());
    }

    @Test
    public void testUpdateChangesNetworkConnected()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Y));
        assertTrue(dsl.axle(0, 2, 0, Axis.Y));
        assertTrue(dsl.gearBox(0, 1, 0, DOWN));
        assertTrue(dsl.update(0, 1, 0, c -> { c.remove(DOWN); c.add(UP); }));
        assertEquals("""
            [network=0]
            Axle[connections=[down, up], pos=[0, 0, 0], network=0, axis=y, rotation=up]
            
            [network=1]
            GearBox[connections=[up], pos=[0, 1, 0], network=1, convention=east, rotation=[up]]
            Axle[connections=[down, up], pos=[0, 2, 0], network=1, axis=y, rotation=up]
            """, dsl.toString());
    }

    @Test
    public void testGearBoxConnectInThreeAxiesSequentially()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(1, 0, 0, Axis.X));
        assertTrue(dsl.axle(0, 1, 0, Axis.Y));
        assertTrue(dsl.axle(0, 0, 1, Axis.Z));
        assertTrue(dsl.gearBox(0, 0, 0, UP));
        assertTrue(dsl.update(0, 0, 0, c -> c.add(SOUTH)));
        assertTrue(dsl.update(0, 0, 0, c -> c.remove(UP)));
        assertTrue(dsl.update(0, 0, 0, c -> c.add(EAST)));
        assertEquals("""
            [network=1]
            GearBox[connections=[south, east], pos=[0, 0, 0], network=1, convention=down, rotation=[north, east]]
            Axle[connections=[west, east], pos=[1, 0, 0], network=1, axis=x, rotation=east]
            Axle[connections=[north, south], pos=[0, 0, 1], network=1, axis=z, rotation=north]
            
            [network=3]
            Axle[connections=[down, up], pos=[0, 1, 0], network=3, axis=y, rotation=up]
            """, dsl.toString());
    }

    // ===== Connectivity - remove ===== //

    @Test
    public void testRemoveOnlyNode()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Y));
        assertTrue(dsl.remove(0, 0, 0));
        assertEquals("", dsl.toString());
    }

    @Test
    public void testRemoveNodeNotConnected()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.X));
        assertTrue(dsl.axle(0, 1, 0, Axis.Z));
        assertTrue(dsl.remove(0, 0, 0));
        assertTrue(dsl.remove(0, 1, 0));
        assertEquals("", dsl.toString());
    }

    @Test
    public void testRemoveConnectedNodes()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Y));
        assertTrue(dsl.axle(0, 1, 0, Axis.Y));
        assertTrue(dsl.remove(0, 1, 0));
        assertTrue(dsl.remove(0, 0, 0));
        assertEquals("", dsl.toString());
    }

    @Test
    public void testRemoveLeafNodeOfNetwork()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Y));
        assertTrue(dsl.axle(0, 1, 0, Axis.Y));
        assertTrue(dsl.axle(0, 2, 0, Axis.Y));
        assertTrue(dsl.remove(0, 2, 0));
        assertEquals("""
            [network=0]
            Axle[connections=[down, up], pos=[0, 0, 0], network=0, axis=y, rotation=up]
            Axle[connections=[down, up], pos=[0, 1, 0], network=0, axis=y, rotation=up]
            """, dsl.toString());
    }

    @Test
    public void testRemoveNodeSplitsNetwork()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Y));
        assertTrue(dsl.axle(0, 1, 0, Axis.Y));
        assertTrue(dsl.axle(0, 2, 0, Axis.Y));
        assertTrue(dsl.remove(0, 1, 0));
        assertEquals("""
            [network=0]
            Axle[connections=[down, up], pos=[0, 0, 0], network=0, axis=y, rotation=up]
            
            [network=1]
            Axle[connections=[down, up], pos=[0, 2, 0], network=1, axis=y, rotation=up]
            """, dsl.toString());
    }

    @Test
    public void testRemoveReplaceAndRemove()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Y));
        assertTrue(dsl.axle(0, 1, 0, Axis.Y));
        assertTrue(dsl.axle(0, 2, 0, Axis.Y));
        assertTrue(dsl.remove(0, 1, 0));
        assertTrue(dsl.remove(0, 0, 0));
        assertTrue(dsl.axle(0, 0, 0, Axis.Y));
        assertTrue(dsl.axle(0, 1, 0, Axis.Y));
        assertTrue(dsl.remove(0, 0, 0));
        assertTrue(dsl.axle(0, 0, 0, Axis.Y));
        assertEquals("""
            [network=2]
            Axle[connections=[down, up], pos=[0, 0, 0], network=2, axis=y, rotation=up]
            Axle[connections=[down, up], pos=[0, 1, 0], network=2, axis=y, rotation=up]
            Axle[connections=[down, up], pos=[0, 2, 0], network=2, axis=y, rotation=up]
            """, dsl.toString());
    }

    // ===== Rotation ===== //

    @Test
    public void testGearBoxConnectsToAxlesInYAxis()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Y));
        assertTrue(dsl.axle(0, 1, 0, Axis.Y));
        assertTrue(dsl.gearBox(0, 2, 0, DOWN, UP));
        assertEquals("""
            [network=0]
            Axle[connections=[down, up], pos=[0, 0, 0], network=0, axis=y, rotation=up]
            Axle[connections=[down, up], pos=[0, 1, 0], network=0, axis=y, rotation=up]
            GearBox[connections=[down, up], pos=[0, 2, 0], network=0, convention=west, rotation=[up, down]]
            """, dsl.toString());
    }

    @Test
    public void testGearBoxReversesRotationInLine()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Z));
        assertTrue(dsl.gearBox(0, 0, 1, NORTH, SOUTH));
        assertTrue(dsl.axle(0, 0, 2, Axis.Z));
        assertEquals("""
            [network=0]
            Axle[connections=[north, south], pos=[0, 0, 0], network=0, axis=z, rotation=south]
            GearBox[connections=[north, south], pos=[0, 0, 1], network=0, convention=east, rotation=[south, north]]
            Axle[connections=[north, south], pos=[0, 0, 2], network=0, axis=z, rotation=north]
            """, dsl.toString());
    }

    @Test
    public void testGearBoxReversesRotationInLineOpposite()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 2, Axis.Z));
        assertTrue(dsl.gearBox(0, 0, 1, NORTH, SOUTH));
        assertTrue(dsl.axle(0, 0, 0, Axis.Z));
        assertEquals("""
            [network=0]
            Axle[connections=[north, south], pos=[0, 0, 0], network=0, axis=z, rotation=north]
            GearBox[connections=[north, south], pos=[0, 0, 1], network=0, convention=west, rotation=[north, south]]
            Axle[connections=[north, south], pos=[0, 0, 2], network=0, axis=z, rotation=south]
            """, dsl.toString());
    }

    @Test
    public void testGearBoxReversesRotationInLineInMiddle()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Z));
        assertTrue(dsl.axle(0, 0, 2, Axis.Z));
        assertTrue(dsl.gearBox(0, 0, 1, NORTH, SOUTH));
        assertEquals("""
            [network=0]
            Axle[connections=[north, south], pos=[0, 0, 0], network=0, axis=z, rotation=south]
            GearBox[connections=[north, south], pos=[0, 0, 1], network=0, convention=east, rotation=[south, north]]
            Axle[connections=[north, south], pos=[0, 0, 2], network=0, axis=z, rotation=north]
            """, dsl.toString());
    }

    @Test
    public void testGearBoxReversesRotationInLine5xInMiddle()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Z));
        assertTrue(dsl.axle(0, 0, 1, Axis.Z));
        assertTrue(dsl.axle(0, 0, 3, Axis.Z));
        assertTrue(dsl.axle(0, 0, 4, Axis.Z));
        assertTrue(dsl.gearBox(0, 0, 2, NORTH, SOUTH));
        assertEquals("""
            [network=0]
            Axle[connections=[north, south], pos=[0, 0, 0], network=0, axis=z, rotation=south]
            Axle[connections=[north, south], pos=[0, 0, 1], network=0, axis=z, rotation=south]
            GearBox[connections=[north, south], pos=[0, 0, 2], network=0, convention=east, rotation=[south, north]]
            Axle[connections=[north, south], pos=[0, 0, 3], network=0, axis=z, rotation=north]
            Axle[connections=[north, south], pos=[0, 0, 4], network=0, axis=z, rotation=north]
            """, dsl.toString());
    }

    @Test
    public void testGearBoxReversesConventionInPerpendicularDirection()
    {
        final var dsl = dsl();

        assertTrue(dsl.axle(0, 0, 0, Axis.Z));
        assertTrue(dsl.gearBox(0, 0, 1, NORTH, SOUTH, EAST, WEST));
        assertTrue(dsl.axle(0, 0, 2, Axis.Z));
        assertTrue(dsl.axle(1, 0, 1, Axis.X));
        assertTrue(dsl.axle(-1, 0, 1, Axis.X));
        assertEquals("""
            [network=0]
            Axle[connections=[north, south], pos=[0, 0, 0], network=0, axis=z, rotation=south]
            Axle[connections=[west, east], pos=[-1, 0, 1], network=0, axis=x, rotation=west]
            GearBox[connections=[north, south, west, east], pos=[0, 0, 1], network=0, convention=down, rotation=[south, north, west, east]]
            Axle[connections=[west, east], pos=[1, 0, 1], network=0, axis=x, rotation=east]
            Axle[connections=[north, south], pos=[0, 0, 2], network=0, axis=z, rotation=north]
            """, dsl.toString());
    }

    @Test
    public void testGearBoxPropagatesReversedDirectionThroughGearBox()
    {
        final var dsl = dsl();

        // ...5
        // 17234
        // ...6.
        // where 3, 7 are boxes
        assertTrue(dsl.axle(0, 0, 0, Axis.Z)); // network=0
        assertTrue(dsl.axle(0, 0, 2, Axis.Z)); // network=1
        assertTrue(dsl.gearBox(0, 0, 3, NORTH, SOUTH, EAST, WEST));
        assertTrue(dsl.axle(0, 0, 4, Axis.Z));
        assertTrue(dsl.axle(1, 0, 3, Axis.X));
        assertTrue(dsl.axle(-1, 0, 3, Axis.X));
        assertTrue(dsl.gearBox(0, 0, 1, NORTH, SOUTH)); // merge 0 <- 1
        assertEquals("""
            [network=0]
            Axle[connections=[north, south], pos=[0, 0, 0], network=0, axis=z, rotation=south]
            GearBox[connections=[north, south], pos=[0, 0, 1], network=0, convention=east, rotation=[south, north]]
            Axle[connections=[north, south], pos=[0, 0, 2], network=0, axis=z, rotation=north]
            Axle[connections=[west, east], pos=[-1, 0, 3], network=0, axis=x, rotation=east]
            GearBox[connections=[north, south, west, east], pos=[0, 0, 3], network=0, convention=up, rotation=[north, south, east, west]]
            Axle[connections=[west, east], pos=[1, 0, 3], network=0, axis=x, rotation=west]
            Axle[connections=[north, south], pos=[0, 0, 4], network=0, axis=z, rotation=south]
            """, dsl.toString());
    }

    @Test
    public void testTwoConnectionsToSameNetworkCompatible()
    {
        final var dsl = dsl();

        assertTrue(dsl.gearBox(0, 0, 0, NORTH, SOUTH, EAST, WEST));
        assertTrue(dsl.gearBox(1, 0, 0, NORTH, SOUTH, EAST, WEST));
        assertTrue(dsl.axle(1, 0, 1, Axis.Z));
        assertTrue(dsl.gearBox(1, 0, 2, NORTH, SOUTH, EAST, WEST));
        assertTrue(dsl.gearBox(0, 0, 2, NORTH, SOUTH, EAST, WEST));
        assertTrue(dsl.axle(0, 0, 1, Axis.Z));
        assertEquals("""
            [network=0]
            GearBox[connections=[north, south, west, east], pos=[0, 0, 0], network=0, convention=up, rotation=[north, south, east, west]]
            GearBox[connections=[north, south, west, east], pos=[1, 0, 0], network=0, convention=down, rotation=[south, north, west, east]]
            Axle[connections=[north, south], pos=[0, 0, 1], network=0, axis=z, rotation=south]
            Axle[connections=[north, south], pos=[1, 0, 1], network=0, axis=z, rotation=north]
            GearBox[connections=[north, south, west, east], pos=[0, 0, 2], network=0, convention=down, rotation=[south, north, west, east]]
            GearBox[connections=[north, south, west, east], pos=[1, 0, 2], network=0, convention=up, rotation=[north, south, east, west]]
            """, dsl.toString());
    }

    @Test
    public void testTwoConnectionsToSameNetworkIncompatible()
    {
        final var dsl = dsl();

        assertTrue(dsl.gearBox(0, 0, 0, NORTH, SOUTH, EAST, WEST));
        assertTrue(dsl.gearBox(1, 0, 0, NORTH, SOUTH, EAST, WEST));
        assertTrue(dsl.axle(1, 0, 1, Axis.Z));
        assertTrue(dsl.gearBox(1, 0, 2, NORTH, SOUTH, EAST, WEST));
        assertTrue(dsl.gearBox(0, 0, 2, NORTH, SOUTH, EAST, WEST));
        assertFalse(dsl.gearBox(0, 0, 1, NORTH, SOUTH));
        assertEquals("""
            [network=0]
            GearBox[connections=[north, south, west, east], pos=[0, 0, 0], network=0, convention=up, rotation=[north, south, east, west]]
            GearBox[connections=[north, south, west, east], pos=[1, 0, 0], network=0, convention=down, rotation=[south, north, west, east]]
            Axle[connections=[north, south], pos=[1, 0, 1], network=0, axis=z, rotation=north]
            GearBox[connections=[north, south, west, east], pos=[0, 0, 2], network=0, convention=down, rotation=[south, north, west, east]]
            GearBox[connections=[north, south, west, east], pos=[1, 0, 2], network=0, convention=up, rotation=[north, south, east, west]]
            """, dsl.toString());
    }

    private static BlockPos pos(int x, int y, int z)
    {
        return new BlockPos(x, y, z);
    }

    private static DSL dsl()
    {
        return new DSL(new RotationNetworkManager(), new ArrayList<>());
    }

    record DSL(RotationNetworkManager manager, List<BlockPos> updates)
    {
        boolean axle(int x, int y, int z, Direction.Axis axis)
        {
            return manager.performAction(new RotationNode.Axle(owner(x, y, z), axis, 0f), Action.ADD);
        }

        boolean gearBox(int x, int y, int z, Direction... connections)
        {
            return manager.performAction(new RotationNode.GearBox(owner(x, y, z), NetworkHelpers.of(connections), 0f), Action.ADD);
        }

        boolean update(int x, int y, int z, Consumer<Collection<Direction>> updater)
        {
            final var node = (RotationNode.GearBox) manager.nodeAt(pos(x, y, z));
            assertNotNull(node);
            updater.accept(node.connections());
            node.updateConvention();
            return manager.performAction(node, Action.UPDATE);
        }

        boolean remove(int x, int y, int z)
        {
            final RotationNode node = manager.nodeAt(pos(x, y, z));
            assertNotNull(node);
            return manager.performAction(node, Action.REMOVE);
        }

        RotationOwner owner(int x, int y, int z)
        {
            return new RotationOwner() {
                @Override
                public BlockPos getBlockPos()
                {
                    return pos(x, y, z);
                }

                @Override public RotationNode getRotationNode() { throw new AssertionError(); }
                @Override public void onUpdate() { DSL.this.updates.add(getBlockPos()); }
            };
        }

        @Override
        public String toString()
        {
            NetworkTest.validateNetworkManagerIntegrity(manager);
            return manager.toString();
        }
    }
}
