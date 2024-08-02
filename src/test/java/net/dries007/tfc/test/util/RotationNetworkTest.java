/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.util.rotation.AxleNode;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.Rotation;
import net.dries007.tfc.util.rotation.RotationNetworkManager;
import net.dries007.tfc.util.rotation.SourceNode;

import static net.minecraft.core.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

public class RotationNetworkTest
{
    @Test
    public void testEmpty()
    {
        assertEquals(mock().manager.toString(), "");
    }

    @Test
    public void testSingleSource()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, NORTH));
        assertEquals("""
            [network=0]
            Node[connections=[north], pos=[0, 0, 0], network=0, rotation=null]
            """, mock.toString());
    }

    @Test
    public void testConnectedSourcesCausesOtherToBreak()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, NORTH, SOUTH));
        assertFalse(mock.addSource(0, 0, 1, NORTH, SOUTH));
        assertEquals("""
            [network=0]
            Node[connections=[north, south], pos=[0, 0, 0], network=0, rotation=null]
            """, mock.toString());
    }

    @Test
    public void testSingleNodeNoSource()
    {
        final RotationMock mock = mock();

        assertTrue(mock.add(0, 0, 0, EAST, WEST));
        assertEquals("", mock.toString());
    }

    @Test
    public void testSourceAndDisconnectedNodes()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, NORTH));
        assertTrue(mock.add(0, 0, 0, EAST, WEST));
        assertTrue(mock.add(0, 0, 1, NORTH, SOUTH));
        assertTrue(mock.add(1, 0, 0, NORTH, SOUTH));
        assertTrue(mock.add(1, 0, 1, NORTH, SOUTH));
        assertEquals("""
            [network=0]
            Node[connections=[north], pos=[0, 0, 0], network=0, rotation=null]
            """, mock.toString());
    }

    @Test
    public void testSourceAndConnectedNodes()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, NORTH));
        assertTrue(mock.add(0, 0, -1, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, -2, NORTH, SOUTH));
        assertEquals("""
            [network=0]
            Node[connections=[north], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[north, south], pos=[0, 0, -2], network=0, rotation=[south, Rotation[direction=north, speed=1.0]]]
            Node[connections=[north, south], pos=[0, 0, -1], network=0, rotation=[south, Rotation[direction=north, speed=1.0]]]
            """, mock.toString());
    }

    @Test
    public void testSourceAndConnectedNodesInWrongOrder()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, NORTH));
        assertTrue(mock.add(0, 0, -2, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, -1, NORTH, SOUTH));
        assertEquals("""
            [network=0]
            Node[connections=[north], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[north, south], pos=[0, 0, -2], network=0, rotation=[south, Rotation[direction=north, speed=1.0]]]
            Node[connections=[north, south], pos=[0, 0, -1], network=0, rotation=[south, Rotation[direction=north, speed=1.0]]]
            """, mock.toString());
    }

    @Test
    public void testSourceAndConnectedNodesWithSourceLast()
    {
        final RotationMock mock = mock();

        assertTrue(mock.add(0, 0, -1, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, -2, NORTH, SOUTH));
        assertTrue(mock.addSource(0, 0, 0, NORTH));
        assertEquals("""
            [network=0]
            Node[connections=[north], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[north, south], pos=[0, 0, -2], network=0, rotation=[south, Rotation[direction=north, speed=1.0]]]
            Node[connections=[north, south], pos=[0, 0, -1], network=0, rotation=[south, Rotation[direction=north, speed=1.0]]]
            """, mock.toString());
    }

    @Test
    public void testRemovingNodeAtLeaf()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, NORTH));
        assertTrue(mock.add(0, 0, -1, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, -2, NORTH, SOUTH));
        mock.remove(0, 0, -2);
        assertEquals("""
            [network=0]
            Node[connections=[north], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[north, south], pos=[0, 0, -1], network=0, rotation=[south, Rotation[direction=north, speed=1.0]]]
            """, mock.toString());
    }

    @Test
    public void testRemovingNodeAtBranch()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, NORTH));
        assertTrue(mock.add(0, 0, -1, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, -2, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, -3, NORTH, SOUTH));
        mock.remove(0, 0, -2);
        assertEquals("""
            [network=0]
            Node[connections=[north], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[north, south], pos=[0, 0, -1], network=0, rotation=[south, Rotation[direction=north, speed=1.0]]]
            """, mock.toString());
    }

    @Test
    public void testMultipleSources()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, EAST));
        assertTrue(mock.addSource(2, 0, 0, WEST));
        assertEquals("""
            [network=0]
            Node[connections=[east], pos=[0, 0, 0], network=0, rotation=null]
            
            [network=1]
            Node[connections=[west], pos=[2, 0, 0], network=1, rotation=null]
            """, mock.toString());
    }

    @Test
    public void testAddingAndRemovingSources()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, EAST));
        mock.remove(0, 0, 0);
        assertTrue(mock.addSource(2, 0, 0, WEST));
        assertTrue(mock.addSource(0, 0, 0, EAST));
        mock.remove(2, 0, 0);
        assertTrue(mock.addSource(2, 0, 0, WEST));
        assertEquals("""
            [network=2]
            Node[connections=[east], pos=[0, 0, 0], network=2, rotation=null]
            
            [network=3]
            Node[connections=[west], pos=[2, 0, 0], network=3, rotation=null]
            """, mock.toString());
    }

    @Test
    public void testConnectingTwoSourcesShouldFail()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, EAST));
        assertTrue(mock.addSource(2, 0, 0, WEST));
        assertFalse(mock.add(1, 0, 0, EAST, WEST));
        assertEquals("""
            [network=0]
            Node[connections=[east], pos=[0, 0, 0], network=0, rotation=null]
            
            [network=1]
            Node[connections=[west], pos=[2, 0, 0], network=1, rotation=null]
            """, mock.toString());
    }
    
    @Test
    public void testUpdatingConnectivityToConnectAtLeaf()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, EAST));
        assertTrue(mock.add(1, 0, 0, EAST, WEST));
        assertTrue(mock.add(2, 0, 0));
        assertTrue(mock.update(2, 0, 0, n -> n.connections().add(WEST)));
        assertEquals("""
            [network=0]
            Node[connections=[east], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[west, east], pos=[1, 0, 0], network=0, rotation=[west, Rotation[direction=east, speed=1.0]]]
            Node[connections=[west], pos=[2, 0, 0], network=0, rotation=[west, Rotation[direction=east, speed=1.0]]]
            """, mock.toString());
    }

    @Test
    public void testUpdatingConnectivityToConnectAtBranch()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, EAST));
        assertTrue(mock.add(1, 0, 0, EAST, WEST));
        assertTrue(mock.add(2, 0, 0, EAST));
        assertTrue(mock.add(3, 0, 0, EAST, WEST));
        assertTrue(mock.update(2, 0, 0, n -> n.connections().add(WEST)));
        assertEquals("""
            [network=0]
            Node[connections=[east], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[west, east], pos=[1, 0, 0], network=0, rotation=[west, Rotation[direction=east, speed=1.0]]]
            Node[connections=[west, east], pos=[2, 0, 0], network=0, rotation=[west, Rotation[direction=east, speed=1.0]]]
            Node[connections=[west, east], pos=[3, 0, 0], network=0, rotation=[west, Rotation[direction=east, speed=1.0]]]
            """, mock.toString());
    }

    @Test
    public void testUpdatingDownstreamConnectivityToConnectAtBranch()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, EAST));
        assertTrue(mock.add(1, 0, 0, EAST, WEST));
        assertTrue(mock.add(2, 0, 0, WEST));
        assertTrue(mock.add(3, 0, 0, EAST, WEST));
        assertTrue(mock.update(2, 0, 0, n -> n.connections().add(EAST)));
        assertEquals("""
            [network=0]
            Node[connections=[east], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[west, east], pos=[1, 0, 0], network=0, rotation=[west, Rotation[direction=east, speed=1.0]]]
            Node[connections=[west, east], pos=[2, 0, 0], network=0, rotation=[west, Rotation[direction=east, speed=1.0]]]
            Node[connections=[west, east], pos=[3, 0, 0], network=0, rotation=[west, Rotation[direction=east, speed=1.0]]]
            """, mock.toString());
    }

    @Test
    public void testUpdatingConnectivityToDisconnectAtLeaf()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, EAST));
        assertTrue(mock.add(1, 0, 0, EAST, WEST));
        assertTrue(mock.add(2, 0, 0, EAST, WEST));
        assertTrue(mock.update(2, 0, 0, n -> n.connections().clear()));
        assertEquals("""
            [network=0]
            Node[connections=[east], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[west, east], pos=[1, 0, 0], network=0, rotation=[west, Rotation[direction=east, speed=1.0]]]
            """, mock.toString());
    }

    @Test
    public void testUpdatingConnectivityToDisconnectAtBranch()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, EAST));
        assertTrue(mock.add(1, 0, 0, EAST, WEST));
        assertTrue(mock.add(2, 0, 0, EAST, WEST));
        assertTrue(mock.add(3, 0, 0, EAST, WEST));
        assertTrue(mock.update(2, 0, 0, n -> n.connections().clear()));
        assertEquals("""
            [network=0]
            Node[connections=[east], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[west, east], pos=[1, 0, 0], network=0, rotation=[west, Rotation[direction=east, speed=1.0]]]
            """, mock.toString());
    }

    @Test
    public void testConnectingTwoSourcesViaUpdate()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, EAST));
        assertTrue(mock.addSource(2, 0, 0, WEST));
        assertTrue(mock.add(1, 0, 0, WEST));
        assertFalse(mock.update(1, 0, 0, n -> n.connections().add(EAST)));
        assertEquals("""
            [network=0]
            Node[connections=[east], pos=[0, 0, 0], network=0, rotation=null]
            
            [network=1]
            Node[connections=[west], pos=[2, 0, 0], network=1, rotation=null]
            """, mock.toString());
    }

    @Test
    public void testConnectingTwoSourcesViaUpdateAndDisconnectingLeaf()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, EAST));
        assertTrue(mock.addSource(2, 0, 0, WEST));
        assertTrue(mock.add(1, 0, 0, WEST, UP));
        assertTrue(mock.add(1, 1, 0, DOWN));
        assertFalse(mock.update(1, 0, 0, n -> n.connections().add(EAST)));
        assertEquals("""
            [network=0]
            Node[connections=[east], pos=[0, 0, 0], network=0, rotation=null]
            
            [network=1]
            Node[connections=[west], pos=[2, 0, 0], network=1, rotation=null]
            """, mock.toString());
    }

    @Test
    public void testRemovingSource()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, EAST));
        assertTrue(mock.add(1, 0, 0, WEST, UP));
        assertTrue(mock.add(1, 1, 0, DOWN));
        mock.remove(0, 0, 0);
        assertEquals("", mock.toString());
    }

    @Test
    public void testSourceConnectedToCycle()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, UP));
        assertTrue(mock.add(0, 1, 0, DOWN, NORTH, EAST, SOUTH, WEST));
        assertTrue(mock.add(1, 1, 0, NORTH, EAST, SOUTH, WEST));
        assertTrue(mock.add(0, 1, 1, NORTH, EAST, SOUTH, WEST));
        assertTrue(mock.add(1, 1, 1, NORTH, EAST, SOUTH, WEST));
        assertEquals("""
            [network=0]
            Node[connections=[up], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[down, north, south, west, east], pos=[0, 1, 0], network=0, rotation=[down, Rotation[direction=up, speed=1.0]]]
            Node[connections=[north, south, west, east], pos=[1, 1, 0], network=0, rotation=[west, Rotation[direction=east, speed=1.0]]]
            Node[connections=[north, south, west, east], pos=[0, 1, 1], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            Node[connections=[north, south, west, east], pos=[1, 1, 1], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            """, mock.toString());
    }

    @Test
    public void testRemovingBranchInCycle()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, UP));
        assertTrue(mock.add(0, 1, 0, DOWN, NORTH, EAST, SOUTH, WEST));
        assertTrue(mock.add(1, 1, 0, NORTH, EAST, SOUTH, WEST));
        assertTrue(mock.add(0, 1, 1, NORTH, EAST, SOUTH, WEST));
        assertTrue(mock.add(1, 1, 1, NORTH, EAST, SOUTH, WEST));
        mock.remove(1, 1, 0);
        assertEquals("""
            [network=0]
            Node[connections=[up], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[down, north, south, west, east], pos=[0, 1, 0], network=0, rotation=[down, Rotation[direction=up, speed=1.0]]]
            Node[connections=[north, south, west, east], pos=[0, 1, 1], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            Node[connections=[north, south, west, east], pos=[1, 1, 1], network=0, rotation=[west, Rotation[direction=east, speed=1.0]]]
            """, mock.toString());
    }

    @Test
    public void testConnectTwoDifferentHandRotationsInCycle()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, UP));
        assertTrue(mock.add(0, 1, 0, EAST, DOWN, UP));
        assertTrue(mock.add(1, 1, 0, WEST, UP));
        assertTrue(mock.add(1, 2, 0, InvertNode::new, WEST, DOWN, UP));
        assertTrue(mock.add(1, 3, 0, DOWN));
        assertFalse(mock.add(0, 2, 0, EAST, DOWN));
        assertEquals("""
            [network=0]
            Node[connections=[up], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[down, up, east], pos=[0, 1, 0], network=0, rotation=[down, Rotation[direction=up, speed=1.0]]]
            Node[connections=[up, west], pos=[1, 1, 0], network=0, rotation=[west, Rotation[direction=east, speed=1.0]]]
            Node[connections=[down, up, west], pos=[1, 2, 0], network=0, rotation=[down, Rotation[direction=up, speed=1.0]]]
            Node[connections=[down], pos=[1, 3, 0], network=0, rotation=[down, Rotation[direction=down, speed=1.0]]]
            """, mock.toString());
    }

    @Test
    public void testConnectTwoDifferentHandRotationsInCycleFromUpdate()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, UP));
        assertTrue(mock.add(0, 1, 0, EAST, DOWN, UP));
        assertTrue(mock.add(1, 1, 0, WEST, UP));
        assertTrue(mock.add(1, 2, 0, InvertNode::new, WEST, DOWN, UP));
        assertTrue(mock.add(1, 3, 0, DOWN));
        assertTrue(mock.add(0, 2, 0, DOWN));
        assertFalse(mock.update(0, 2, 0, n -> n.connections().add(EAST)));
        assertEquals("""
            [network=0]
            Node[connections=[up], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[down, up, east], pos=[0, 1, 0], network=0, rotation=[down, Rotation[direction=up, speed=1.0]]]
            Node[connections=[up, west], pos=[1, 1, 0], network=0, rotation=[west, Rotation[direction=east, speed=1.0]]]
            Node[connections=[down, up, west], pos=[1, 2, 0], network=0, rotation=[down, Rotation[direction=up, speed=1.0]]]
            Node[connections=[down], pos=[1, 3, 0], network=0, rotation=[down, Rotation[direction=down, speed=1.0]]]
            """, mock.toString());
    }

    @Test
    public void testExactRightLengthAxleWorks()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, SOUTH));
        assertTrue(mock.add(0, 0, 1, AxleNode::new, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, 2, AxleNode::new, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, 3, AxleNode::new, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, 4, AxleNode::new, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, 5, AxleNode::new, NORTH, SOUTH));
        assertEquals("""
            [network=0]
            Node[connections=[south], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[north, south], pos=[0, 0, 1], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            Node[connections=[north, south], pos=[0, 0, 2], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            Node[connections=[north, south], pos=[0, 0, 3], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            Node[connections=[north, south], pos=[0, 0, 4], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            Node[connections=[north, south], pos=[0, 0, 5], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            """, mock.toString());
    }

    @Test
    public void testTooLongAxleFailsToAdd()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, SOUTH));
        assertTrue(mock.add(0, 0, 1, AxleNode::new, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, 2, AxleNode::new, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, 3, AxleNode::new, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, 4, AxleNode::new, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, 5, AxleNode::new, NORTH, SOUTH));
        assertFalse(mock.add(0, 0, 6, AxleNode::new, NORTH, SOUTH));
        assertEquals("""
            [network=0]
            Node[connections=[south], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[north, south], pos=[0, 0, 1], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            Node[connections=[north, south], pos=[0, 0, 2], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            Node[connections=[north, south], pos=[0, 0, 3], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            Node[connections=[north, south], pos=[0, 0, 4], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            Node[connections=[north, south], pos=[0, 0, 5], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            """, mock.toString());
    }

    @Test
    public void testTooLongAxleFailsWhenAddedInMiddle()
    {
        final RotationMock mock = mock();

        assertTrue(mock.addSource(0, 0, 0, SOUTH));
        assertTrue(mock.add(0, 0, 1, AxleNode::new, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, 2, AxleNode::new, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, 4, AxleNode::new, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, 5, AxleNode::new, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, 6, AxleNode::new, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, 3, AxleNode::new, NORTH, SOUTH));
        assertEquals("""
            [network=0]
            Node[connections=[south], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[north, south], pos=[0, 0, 1], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            Node[connections=[north, south], pos=[0, 0, 2], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            Node[connections=[north, south], pos=[0, 0, 3], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            Node[connections=[north, south], pos=[0, 0, 4], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            Node[connections=[north, south], pos=[0, 0, 5], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            """, mock.toString());
    }


    
    private RotationMock mock()
    {
        return new RotationMock(new RotationNetworkManager(), new HashMap<>());
    }


    record RotationMock(RotationNetworkManager manager, Map<BlockPos, Node> sourceNodes)
    {
        boolean add(int x, int y, int z) { return add(x, y, z, MockNode::new, EnumSet.noneOf(Direction.class)); }
        boolean add(int x, int y, int z, Direction first, Direction... rest) { return add(x, y, z, MockNode::new, EnumSet.of(first, rest)); }
        boolean add(int x, int y, int z, BiFunction<BlockPos, EnumSet<Direction>, Node> factory, Direction first, Direction... rest) { return add(x, y, z, factory, EnumSet.of(first, rest)); }
        boolean addSource(int x, int y, int z, Direction dir, Direction... rest) { return add(x, y, z, (p, c) -> new SourceNode(p, c, dir, 1.0f) {}, EnumSet.of(dir, rest)); }

        boolean add(int x, int y, int z, BiFunction<BlockPos, EnumSet<Direction>, Node> factory, EnumSet<Direction> connections)
        {
            final BlockPos pos = new BlockPos(x, y, z);
            final Node node = factory.apply(pos, connections);
            if (node instanceof SourceNode srcNode)
            {
                sourceNodes.put(pos, srcNode);
                return manager.addSource(srcNode);
            }
            else return manager.add(node);
        }

        boolean update(int x, int y, int z, Consumer<Node> apply)
        {
            final BlockPos pos = new BlockPos(x, y, z);
            final Node node = manager.getNode(pos);
            assertNotNull(node);
            apply.accept(node);
            return manager.update(node);
        }

        void remove(int x, int y, int z)
        {
            final BlockPos pos = new BlockPos(x, y, z);
            final Node removed = sourceNodes.containsKey(pos) ? sourceNodes.get(pos) : manager.getNode(pos);
            assertNotNull(removed);
            manager.remove(removed);
        }

        @Override
        public String toString()
        {
            return manager.toString();
        }
    }
    
    static class MockNode extends Node
    {
        MockNode(BlockPos pos, EnumSet<Direction> connections)
        {
            super(pos, connections);
        }

        @Override
        public Rotation rotation(Rotation sourceRotation, Direction sourceDirection, Direction exitDirection)
        {
            // This node acts as a passthrough
            // It conveys the input rotation through unmodified, and keeps the same axis direction (handedness) in other directions
            return Rotation.of(fromAxisAndDirection(exitDirection.getAxis(), sourceRotation.direction().getAxisDirection()), 1.0f);
        }
    }

    static class InvertNode extends MockNode
    {
        InvertNode(BlockPos pos, EnumSet<Direction> connections)
        {
            super(pos, connections);
        }

        @Override
        public Rotation rotation(Rotation sourceRotation, Direction sourceDirection, Direction exitDirection)
        {
            // This is the inverse of MockNode
            // It inverts the rotation in all connected directions
            return Rotation.of(fromAxisAndDirection(exitDirection.getAxis(), sourceRotation.direction().getAxisDirection()).getOpposite(), 1.0f);
        }
    }
}
