/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.Rotation;
import net.dries007.tfc.util.rotation.RotationNetworkManager;

import static net.minecraft.core.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

public class RotationNetworkTests
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

        assertTrue(mock.add(0, 0, 0, true, NORTH));
        assertEquals("""
            [network=0]
            Node[connections=[north], pos=[0, 0, 0], network=0, rotation=null]
            """, mock.toString());
    }

    @Test
    public void testSingleSourceNoConnections()
    {
        final RotationMock mock = mock();

        assertTrue(mock.add(0, 0, 0, true));
        assertEquals("""
            [network=0]
            Node[connections=[], pos=[0, 0, 0], network=0, rotation=null]
            """, mock.toString());
    }

    @Test
    public void testSingleNodeNoSource()
    {
        final RotationMock mock = mock();

        assertTrue(mock.add(0, 0, 0, false, EAST, WEST));
        assertEquals("", mock.toString());
    }

    @Test
    public void testSourceAndDisconnectedNodes()
    {
        final RotationMock mock = mock();

        assertTrue(mock.add(0, 0, 0, true, NORTH));
        assertTrue(mock.add(0, 0, 0, false, EAST, WEST));
        assertTrue(mock.add(0, 0, 1, false, NORTH, SOUTH));
        assertTrue(mock.add(1, 0, 0, false, NORTH, SOUTH));
        assertTrue(mock.add(1, 0, 1, false, NORTH, SOUTH));
        assertEquals("""
            [network=0]
            Node[connections=[north], pos=[0, 0, 0], network=0, rotation=null]
            """, mock.toString());
    }

    @Test
    public void testSourceAndConnectedNodes()
    {
        final RotationMock mock = mock();

        assertTrue(mock.add(0, 0, 0, true, NORTH));
        assertTrue(mock.add(0, 0, -1, false, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, -2, false, NORTH, SOUTH));
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

        assertTrue(mock.add(0, 0, 0, true, NORTH));
        assertTrue(mock.add(0, 0, -2, false, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, -1, false, NORTH, SOUTH));
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

        assertTrue(mock.add(0, 0, -1, false, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, -2, false, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, 0, true, NORTH));
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

        assertTrue(mock.add(0, 0, 0, true, NORTH));
        assertTrue(mock.add(0, 0, -1, false, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, -2, false, NORTH, SOUTH));
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

        assertTrue(mock.add(0, 0, 0, true, NORTH));
        assertTrue(mock.add(0, 0, -1, false, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, -2, false, NORTH, SOUTH));
        assertTrue(mock.add(0, 0, -3, false, NORTH, SOUTH));
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

        assertTrue(mock.add(0, 0, 0, true, EAST));
        assertTrue(mock.add(2, 0, 0, true, WEST));
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

        assertTrue(mock.add(0, 0, 0, true, EAST));
        mock.remove(0, 0, 0);
        assertTrue(mock.add(2, 0, 0, true, WEST));
        assertTrue(mock.add(0, 0, 0, true, EAST));
        mock.remove(2, 0, 0);
        assertTrue(mock.add(2, 0, 0, true, WEST));
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

        assertTrue(mock.add(0, 0, 0, true, EAST));
        assertTrue(mock.add(2, 0, 0, true, WEST));
        assertFalse(mock.add(1, 0, 0, false, EAST, WEST));
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

        assertTrue(mock.add(0, 0, 0, true, EAST));
        assertTrue(mock.add(1, 0, 0, false, EAST, WEST));
        assertTrue(mock.add(2, 0, 0, false));
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

        assertTrue(mock.add(0, 0, 0, true, EAST));
        assertTrue(mock.add(1, 0, 0, false, EAST, WEST));
        assertTrue(mock.add(2, 0, 0, false, EAST));
        assertTrue(mock.add(3, 0, 0, false, EAST, WEST));
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

        assertTrue(mock.add(0, 0, 0, true, EAST));
        assertTrue(mock.add(1, 0, 0, false, EAST, WEST));
        assertTrue(mock.add(2, 0, 0, false, WEST));
        assertTrue(mock.add(3, 0, 0, false, EAST, WEST));
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

        assertTrue(mock.add(0, 0, 0, true, EAST));
        assertTrue(mock.add(1, 0, 0, false, EAST, WEST));
        assertTrue(mock.add(2, 0, 0, false, EAST, WEST));
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

        assertTrue(mock.add(0, 0, 0, true, EAST));
        assertTrue(mock.add(1, 0, 0, false, EAST, WEST));
        assertTrue(mock.add(2, 0, 0, false, EAST, WEST));
        assertTrue(mock.add(3, 0, 0, false, EAST, WEST));
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

        assertTrue(mock.add(0, 0, 0, true, EAST));
        assertTrue(mock.add(2, 0, 0, true, WEST));
        assertTrue(mock.add(1, 0, 0, false, WEST));
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

        assertTrue(mock.add(0, 0, 0, true, EAST));
        assertTrue(mock.add(2, 0, 0, true, WEST));
        assertTrue(mock.add(1, 0, 0, false, WEST, UP));
        assertTrue(mock.add(1, 1, 0, false, DOWN));
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

        assertTrue(mock.add(0, 0, 0, true, EAST));
        assertTrue(mock.add(1, 0, 0, false, WEST, UP));
        assertTrue(mock.add(1, 1, 0, false, DOWN));
        mock.remove(0, 0, 0);
        assertEquals("", mock.toString());
    }

    @Test
    public void testSourceConnectedToCycle()
    {
        final RotationMock mock = mock();

        assertTrue(mock.add(0, 0, 0, true, UP));
        assertTrue(mock.add(0, 1, 0, false, DOWN, NORTH, EAST, SOUTH, WEST));
        assertTrue(mock.add(1, 1, 0, false, NORTH, EAST, SOUTH, WEST));
        assertTrue(mock.add(0, 1, 1, false, NORTH, EAST, SOUTH, WEST));
        assertTrue(mock.add(1, 1, 1, false, NORTH, EAST, SOUTH, WEST));
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

        assertTrue(mock.add(0, 0, 0, true, UP));
        assertTrue(mock.add(0, 1, 0, false, DOWN, NORTH, EAST, SOUTH, WEST));
        assertTrue(mock.add(1, 1, 0, false, NORTH, EAST, SOUTH, WEST));
        assertTrue(mock.add(0, 1, 1, false, NORTH, EAST, SOUTH, WEST));
        assertTrue(mock.add(1, 1, 1, false, NORTH, EAST, SOUTH, WEST));
        mock.remove(1, 1, 0);
        assertEquals("""
            [network=0]
            Node[connections=[up], pos=[0, 0, 0], network=0, rotation=null]
            Node[connections=[down, north, south, west, east], pos=[0, 1, 0], network=0, rotation=[down, Rotation[direction=up, speed=1.0]]]
            Node[connections=[north, south, west, east], pos=[0, 1, 1], network=0, rotation=[north, Rotation[direction=south, speed=1.0]]]
            Node[connections=[north, south, west, east], pos=[1, 1, 1], network=0, rotation=[west, Rotation[direction=east, speed=1.0]]]
            """, mock.toString());
    }
    
    private RotationMock mock()
    {
        return new RotationMock(new RotationNetworkManager(), new HashMap<>());
    }


    record RotationMock(RotationNetworkManager manager, Map<BlockPos, Node> sourceNodes)
    {
        boolean add(int x, int y, int z, boolean source) { return add(x, y, z, source, EnumSet.noneOf(Direction.class)); }
        boolean add(int x, int y, int z, boolean source, Direction first, Direction... rest) { return add(x, y, z, source, EnumSet.of(first, rest)); }

        boolean add(int x, int y, int z, boolean source, EnumSet<Direction> connections)
        {
            final BlockPos pos = new BlockPos(x, y, z);
            final Node node = new MockNode(pos, connections);
            if (source)
            {
                sourceNodes.put(pos, node);
                return manager.addSource(node);
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
        public Rotation rotation(Direction exitDirection)
        {
            return Rotation.of(exitDirection, 1.0f);
        }

        @Override
        public String toString()
        {
            return "Node[connections=%s, pos=[%d, %d, %d], network=%d, rotation=%s]".formatted(connections(), pos().getX(), pos().getY(), pos().getZ(), network(), sourceRotation == null ? "null" : "[%s, Rotation[direction=%s, speed=%s]]".formatted(sourceDirection, sourceRotation.direction(), sourceRotation.speed()));
        }
    }
}
