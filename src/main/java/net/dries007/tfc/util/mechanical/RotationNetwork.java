/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.mechanical;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;


/**
 * A single network, consisting of a rotation source, plus any connected components. Forms a <em>directed tree</em>.
 */
final class RotationNetwork
{
    private final long id;
    private final Node source;
    private final Map<BlockPos, Node> nodes;

    RotationNetwork(long id, Node source)
    {
        this.id = id;
        this.source = source;
        this.nodes = new HashMap<>();
    }

    /**
     * Updates the network on the addition of a node. Returns {@code true} if the node was successfully added to this network.
     * <p>
     * When adding a new block to the world, this will be called on every existing mechanical network. If two networks attempt to add the same node, the second will cause the block to break (by returning {@code true} when {@code toAdd.network() != NO_NETWORK}.
     * <p>
     * Note that in addition to adding the target node, we also need to explore for any other nodes which connect from that newly added node, but were not connected to a source (and thus not already within a network).
     */
    boolean updateOnAdd(Node toAdd)
    {
        // Assume network was in a valid state before any modifications
        // Try and connect from any adjacent positions within the network, and propagate to this node only

        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (Direction direction : toAdd.connections())
        {
            cursor.setWithOffset(toAdd.pos(), direction);

            final @Nullable Node adjacentNode = getNode(cursor);
            final Direction inverseDirection = direction.getOpposite();

            if (adjacentNode != null && // There is a node at this position
                adjacentNode.connections().contains(inverseDirection) && // Which connects in the target direction
                adjacentNode.source() != inverseDirection // And we are not trying to connect to the source
            )
            {
                // We can connect from the given direction, so we add this node to the network and update it.
                // First, if we have already added this node to a network, immediately exit and return true. The node will be broken.
                if (toAdd.network() != Node.NO_NETWORK)
                {
                    return true;
                }

                // Otherwise, add this node to the current network, and update it
                nodes.put(toAdd.pos(), toAdd);
                toAdd.update(id, direction, adjacentNode.rotation(inverseDirection));

                // Then immediately return true, indicating we added this node to the network, but importantly have not searched outwards to add any other disconnected nodes.
                // We only do that once we're sure this node is valid (not connecting to multiple networks)
                return true;
            }
        }
        return false;
    }

    void updateAfterAdd(Node added, RotationAccess level)
    {
        // `toAdd` has been successfully added to the current network, so we can search outwards from this point
        // We should only encounter nodes that have been disconnected from any network, which we can directly add
        assert added.network() == id;

        final Queue<Node> queue = new ArrayDeque<>();
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        queue.add(added);

        while (!queue.isEmpty())
        {
            final Node current = queue.poll();

            for (Direction direction : current.connections())
            {
                cursor.setWithOffset(current.pos(), direction);

                final @Nullable Node next = level.getNode(cursor);
                final Direction inverseDirection = direction.getOpposite();

                if (next != null && // There is a node at this position
                    next.network() == Node.NO_NETWORK && // And it is not connected to a network
                    next.connections().contains(inverseDirection) && // And it connects in the target direction
                    current.source() != direction // And we are not trying to connect from our source
                )
                {
                    // Then we can add this node, and enqueue it to explore further
                    queue.add(next);
                    nodes.put(next.pos(), next);
                    next.update(id, inverseDirection, current.rotation(direction));
                }
            }
        }
    }

    void removeNode(Node toRemove)
    {
        assert toRemove.network() == id;

        nodes.remove(toRemove.pos());
    }

    /**
     * Update a network, once a node within it has been updated or removed.
     */
    void updateNetwork()
    {
        // BFS from the source, marking each node as either visited, or unvisited.
        // This method assumes all current nodes in the graph represent the real state in world.
        // Thus, it should be called whenever nodes are removed, to detect disconnected components

        final Queue<Node> queue = new ArrayDeque<>();
        final Set<Node> visited = new ReferenceOpenHashSet<>();
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        queue.add(source);

        // Initially populate the visited set with all nodes, and remove them as we find them
        // This leaves the final set as all disconnected nodes, which makes removing them trivial
        visited.addAll(nodes.values());

        while (!queue.isEmpty())
        {
            final Node current = queue.poll();

            for (Direction direction : current.connections())
            {
                cursor.setWithOffset(current.pos(), direction);

                final @Nullable Node next = getNode(cursor);
                final Direction inverseDirection = direction.getOpposite();

                if (next != null && // There is a node at this position
                    next.connections().contains(inverseDirection) && // That connects in the matching direction
                    visited.contains(next) // We haven't already visited this node, and updated its rotation (and source). Prevents cycles
                )
                {
                    // This node connects in the given direction, so we need to (1) mark it as seen, (2) push it to the queue, and (3) update the rotation parameters
                    next.update(id, inverseDirection, current.rotation(direction));

                    queue.add(next);
                    visited.remove(next);
                }
            }
        }

        // Any nodes that were not visited, are disconnected and are removed from the network
        for (Node node : visited)
        {
            node.remove();
            nodes.remove(node.pos());
        }
    }

    /**
     * Removes the entire network, when the source node is removed.
     */
    void removeNetwork()
    {
        for (Node node : nodes.values())
        {
            node.remove();
        }
        nodes.clear();
    }

    boolean isSource(Node node)
    {
        return this.source == node;
    }

    long networkId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return "[network=%d]\n%s\n%s".formatted(id, source, nodes.values()
            .stream()
            .sorted(Comparator.comparing(Node::pos))
            .map(e -> e + "\n")
            .collect(Collectors.joining()));
    }

    @Nullable
    private Node getNode(BlockPos pos)
    {
        return pos.equals(source.pos()) ? source : nodes.get(pos);
    }
}
