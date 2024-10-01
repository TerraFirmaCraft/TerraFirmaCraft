/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.network.Action;
import net.dries007.tfc.util.network.Network;
import net.dries007.tfc.util.network.NetworkHelpers;
import net.dries007.tfc.util.network.NetworkManager;
import net.dries007.tfc.util.network.Node;

import static net.dries007.tfc.test.TestAssertions.*;
import static net.minecraft.core.Direction.*;

public class NetworkTest
{
    // ===== Connectivity Tests - add / update / remove ===== //

    @Test
    public void testEmpty()
    {
        assertEquals("", dsl().toString());
    }

    @Test
    public void testSingleNodeNoConnections()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 0));
        assertEquals("""
            [network=0]
            Node[connections=[], pos=[0, 0, 0], network=0]
            """, dsl.toString());
    }

    @Test
    public void testSingleNodeWithConnections()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 0, NORTH));
        assertEquals("""
            [network=0]
            Node[connections=[north], pos=[0, 0, 0], network=0]
            """, dsl.toString());
    }

    @Test
    public void testTwoNodesNoConnectionsAndTooFar()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 0));
        assertTrue(dsl.add(0, 0, 2));
        assertEquals("""
            [network=0]
            Node[connections=[], pos=[0, 0, 0], network=0]
            
            [network=1]
            Node[connections=[], pos=[0, 0, 2], network=1]
            """, dsl.toString());
    }

    @Test
    public void testTwoNodesNoConnectionsAdjacent()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 0));
        assertTrue(dsl.add(0, 0, 1));
        assertEquals("""
            [network=0]
            Node[connections=[], pos=[0, 0, 0], network=0]
            
            [network=1]
            Node[connections=[], pos=[0, 0, 1], network=1]
            """, dsl.toString());
    }

    @Test
    public void testTwoNodesWithConnectionsNotConnecting()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 0, EAST, WEST));
        assertTrue(dsl.add(0, 0, 1, EAST, WEST));
        assertEquals("""
            [network=0]
            Node[connections=[west, east], pos=[0, 0, 0], network=0]
            
            [network=1]
            Node[connections=[west, east], pos=[0, 0, 1], network=1]
            """, dsl.toString());
    }

    @Test
    public void testTwoNodesConnecting()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 0, NORTH, SOUTH));
        assertTrue(dsl.add(0, 0, 1, NORTH, SOUTH));
        assertEquals("""
            [network=0]
            Node[connections=[north, south], pos=[0, 0, 0], network=0]
            Node[connections=[north, south], pos=[0, 0, 1], network=0]
            """, dsl.toString());
    }

    @Test
    public void testThreeNodesConnecting()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 0, NORTH, SOUTH));
        assertTrue(dsl.add(0, 0, 1, NORTH, SOUTH));
        assertTrue(dsl.add(0, 0, 2, NORTH, SOUTH));
        assertEquals("""
            [network=0]
            Node[connections=[north, south], pos=[0, 0, 0], network=0]
            Node[connections=[north, south], pos=[0, 0, 1], network=0]
            Node[connections=[north, south], pos=[0, 0, 2], network=0]
            """, dsl.toString());
    }

    @Test
    public void testThreeNodesConnectingInMiddle()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 0, NORTH, SOUTH));
        assertTrue(dsl.add(0, 0, 2, NORTH, SOUTH));
        assertTrue(dsl.add(0, 0, 1, NORTH, SOUTH));
        assertEquals("""
            [network=0]
            Node[connections=[north, south], pos=[0, 0, 0], network=0]
            Node[connections=[north, south], pos=[0, 0, 1], network=0]
            Node[connections=[north, south], pos=[0, 0, 2], network=0]
            """, dsl.toString());
    }

    @Test
    public void testThreeNodesConnectingInMiddleReversed()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 2, NORTH, SOUTH));
        assertTrue(dsl.add(0, 0, 0, NORTH, SOUTH));
        assertTrue(dsl.add(0, 0, 1, NORTH, SOUTH));
        assertEquals("""
            [network=1]
            Node[connections=[north, south], pos=[0, 0, 0], network=1]
            Node[connections=[north, south], pos=[0, 0, 1], network=1]
            Node[connections=[north, south], pos=[0, 0, 2], network=1]
            """, dsl.toString());
    }

    @Test
    public void testUpdateConnectToNetwork()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 0, SOUTH));
        assertTrue(dsl.add(0, 0, 1));
        assertTrue(dsl.update(0, 0, 1, e -> e.add(NORTH)));
        assertEquals("""
            [network=0]
            Node[connections=[south], pos=[0, 0, 0], network=0]
            Node[connections=[north], pos=[0, 0, 1], network=0]
            """, dsl.toString());
    }

    @Test
    public void testUpdateDoesNotConnectToNetwork()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 0, SOUTH));
        assertTrue(dsl.add(0, 0, 1));
        assertTrue(dsl.update(0, 0, 1, e -> e.add(EAST)));
        assertEquals("""
            [network=0]
            Node[connections=[south], pos=[0, 0, 0], network=0]
            
            [network=1]
            Node[connections=[east], pos=[0, 0, 1], network=1]
            """, dsl.toString());
    }

    @Test
    public void testUpdateStillConnectsToNetwork()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 0, SOUTH));
        assertTrue(dsl.add(0, 0, 1, NORTH));
        assertTrue(dsl.update(0, 0, 1, e -> e.add(EAST)));
        assertEquals("""
            [network=0]
            Node[connections=[south], pos=[0, 0, 0], network=0]
            Node[connections=[north, east], pos=[0, 0, 1], network=0]
            """, dsl.toString());
    }

    @Test
    public void testUpdateDisconnectsFromNetwork()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 0, SOUTH));
        assertTrue(dsl.add(0, 0, 1, NORTH));
        assertTrue(dsl.update(0, 0, 1, e -> e.remove(NORTH)));
        assertEquals("""
            [network=0]
            Node[connections=[south], pos=[0, 0, 0], network=0]
            
            [network=1]
            Node[connections=[], pos=[0, 0, 1], network=1]
            """, dsl.toString());
    }

    @Test
    public void testUpdateConnectsMultipleNetworks()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 0, SOUTH));
        assertTrue(dsl.add(0, 0, 1));
        assertTrue(dsl.add(0, 0, 2, NORTH));
        assertTrue(dsl.update(0, 0, 1, e -> e.addAll(List.of(NORTH, SOUTH))));
        assertEquals("""
            [network=0]
            Node[connections=[south], pos=[0, 0, 0], network=0]
            Node[connections=[north, south], pos=[0, 0, 1], network=0]
            Node[connections=[north], pos=[0, 0, 2], network=0]
            """, dsl.toString());
    }

    @Test
    public void testUpdateConnectsOneOfMultipleNetworks()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 0, SOUTH));
        assertTrue(dsl.add(0, 0, 1));
        assertTrue(dsl.add(0, 0, 2, NORTH));
        assertTrue(dsl.update(0, 0, 1, e -> e.add(SOUTH)));
        assertEquals("""
            [network=0]
            Node[connections=[south], pos=[0, 0, 0], network=0]

            [network=2]
            Node[connections=[south], pos=[0, 0, 1], network=2]
            Node[connections=[north], pos=[0, 0, 2], network=2]
            """, dsl.toString());
    }

    @Test
    public void testUpdateDisconnectsMultipleNetworks()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 0, SOUTH));
        assertTrue(dsl.add(0, 0, 1, NORTH, SOUTH));
        assertTrue(dsl.add(0, 0, 2, NORTH));
        assertTrue(dsl.update(0, 0, 1, Collection::clear));
        assertEquals("""
            [network=0]
            Node[connections=[south], pos=[0, 0, 0], network=0]
            
            [network=1]
            Node[connections=[north], pos=[0, 0, 2], network=1]
            
            [network=2]
            Node[connections=[], pos=[0, 0, 1], network=2]
            """, dsl.toString());
    }

    @Test
    public void testUpdateSwitchesNetwork()
    {
        final var dsl = dsl();

        assertTrue(dsl.add(0, 0, 0, SOUTH));
        assertTrue(dsl.add(0, 0, 1, NORTH, SOUTH));
        assertTrue(dsl.add(0, 0, 2, SOUTH));
        assertTrue(dsl.add(0, 0, 3, NORTH, SOUTH));
        assertTrue(dsl.add(0, 0, 4, NORTH));
        assertTrue(dsl.update(0, 0, 2, e -> { e.remove(SOUTH); e.add(NORTH); }));
        assertEquals("""
            [network=0]
            Node[connections=[south], pos=[0, 0, 0], network=0]
            Node[connections=[north, south], pos=[0, 0, 1], network=0]
            Node[connections=[north], pos=[0, 0, 2], network=0]
            
            [network=1]
            Node[connections=[north, south], pos=[0, 0, 3], network=1]
            Node[connections=[north], pos=[0, 0, 4], network=1]
            """, dsl.toString());
    }

    @SuppressWarnings("unchecked")
    public static void validateNetworkManagerIntegrity(NetworkManager<?, ?> manager)
    {
        Helpers.uncheck(() -> {
            // Reflection because I'd rather not expose these as fields on `NetworkManager`
            final Field nodesField = NetworkManager.class.getDeclaredField("nodes");
            final Field networksField = NetworkManager.class.getDeclaredField("networks");

            nodesField.setAccessible(true);
            networksField.setAccessible(true);

            final Long2ObjectMap<Node> nodes = (Long2ObjectMap<Node>) nodesField.get(manager);
            final Long2ObjectMap<Network<?>> networks = (Long2ObjectMap<Network<?>>) networksField.get(manager);

            networks.forEach((networkId, networkNodes) -> networkNodes.getNodes().forEach(node -> assertEquals(networkId, node.networkId(), "Node with incorrect network:\n" + manager)));
            nodes.forEach((key, node) -> assertTrue(node.isConnectedToNetwork(), "Node without network:\n" + manager));
        });
    }

    private static DSL dsl()
    {
        return new DSL(new NetworkManager<>());
    }

    record DSL(NetworkManager<Node, Network<Node>> manager)
    {
        boolean add(int x, int y, int z, Direction... connections)
        {
            return manager.performAction(new Node(new BlockPos(x, y, z), NetworkHelpers.of(connections)), Action.ADD);
        }

        boolean update(int x, int y, int z, Consumer<Collection<Direction>> updater)
        {
            final Node node = manager.nodeAt(new BlockPos(x, y, z));
            assertNotNull(node);
            updater.accept(node.connections());
            return manager.performAction(node, Action.UPDATE);
        }

        @Override
        public String toString()
        {
            validateNetworkManagerIntegrity(manager);
            return manager.toString();
        }
    }
}
