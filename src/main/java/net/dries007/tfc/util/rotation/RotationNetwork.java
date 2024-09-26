/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.rotation;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Nullable;


/**
 * A single network, consisting of a rotation source, plus any connected components. Forms a <em>directed tree</em>.
 */
final class RotationNetwork
{
    private final long id;
    private final Node source;
    private final Long2ObjectMap<Node> nodes = new Long2ObjectOpenHashMap<>();

    RotationNetwork(long id, Node source)
    {
        this.id = id;
        this.source = source;
    }

    /**
     * Updates the network on the attempted addition of a node.
     * <ul>
     *     <li>Returns {@link NetworkAddAction#SUCCESS} if the node was able to be successfully added to the network.</li>
     *     <li>Returns {@link NetworkAddAction#FAIL_CONNECTED_TO_OTHER_NETWORK} if the node was capable of being added, but was not due to already being connected to an existing other network.</li>
     *     <li>Returns {@link NetworkAddAction#FAIL_INVALID_CONNECTION} if the node was capable of being added, but was not due to connecting to the current network in multiple incompatible connections.</li>
     *     <li>Returns {@link NetworkAddAction#FAIL_NO_CONNECTION} if the node was <strong>not</strong> capable of being connected to the current network.</li>
     * </ul>
     */
    @CheckReturnValue
    NetworkAddAction updateOnAdd(Node toAdd)
    {
        // A pending connection
        // - node is an adjacent node
        // - direction is the direction of that node, outgoing from the current position
        // - rotation is the rotation provided by that node, as the pending source rotation of the current node
        record PendingConnection(Node node, Direction direction, Rotation rotation) {}

        // Assume network was in a valid state before any modifications
        // Try and connect from any adjacent positions within the network, and propagate to this node only

        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        @Nullable PendingConnection pendingConnection = null;

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
                // The adjacent node belongs to the current network
                assert adjacentNode.network() == id;

                // If this node belongs to another, external network, the node will be broken as we're connected to two possible networks.
                // We have an exception, if the node currently belongs to _this_ network, in which case we are likely re-checking it for update
                if (toAdd.network() != Node.NO_NETWORK && toAdd.network() != id)
                {
                    return NetworkAddAction.FAIL_CONNECTED_TO_OTHER_NETWORK;
                }

                if (pendingConnection != null)
                {
                    // If there is already a pending connection, then we already have a pending rotation, which means we must ensure the pending rotation is compatible with this one, in this direction.
                    // Rotations are deemed compatible if they have the same handedness, indicating the rotation is in the same direction.
                    // We don't check speed, since speed should be constant within a network to avoid breakage.
                    final Rotation adjacentRotation = adjacentNode.rotation(inverseDirection);

                    // The rotation should not be null, since the node is connected to a network
                    assert adjacentRotation != null;


                    if (toAdd.rotation(pendingConnection.rotation, pendingConnection.direction, direction).direction() != adjacentRotation.direction())
                    {
                        // The node cannot be added to this network.
                        return NetworkAddAction.FAIL_INVALID_CONNECTION;
                    }
                    continue;
                }

                // Otherwise, we consider that this node _may_ connect to this network, but we need to check all other connections.
                final Rotation pendingRotation = adjacentNode.rotation(inverseDirection);

                // The rotation should not be null, since the node is connected to a network
                assert pendingRotation != null;

                pendingConnection = new PendingConnection(adjacentNode, direction, pendingRotation);
            }
        }

        if (pendingConnection != null)
        {
            if (toAdd.network() == id)
            {
                // Special case: if we are already connected to the current network, we abort here, but successfully.
                // This is if we were updating the current node
                return NetworkAddAction.SUCCESS;
            }

            if (!toAdd.update(id, pendingConnection.direction, pendingConnection.rotation))
            {
                // We could not update this node on addition to the network, so the connection was invalid
                return NetworkAddAction.FAIL_INVALID_CONNECTION;
            }

            nodes.put(toAdd.posKey(), toAdd);

            // Then return true, indicating we added this node to the network, but importantly have not searched outwards to add any other disconnected nodes.
            // We only do that once we're sure this node is valid (not connecting to multiple networks)
            return NetworkAddAction.SUCCESS;
        }

        return NetworkAddAction.FAIL_NO_CONNECTION;
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
                    current.source() != direction && // And we are not trying to connect from our source
                    !nodes.containsKey(next.posKey()) // And we don't already connect to a node at this location
                )
                {
                    final Rotation exitRotation = current.rotation(direction);

                    // The node currently belongs to a network, so it's rotation should never be null
                    assert exitRotation != null;

                    if (!next.update(id, inverseDirection, exitRotation))
                    {
                        // This node could not be updated to be part of the network, so we have to abort here
                        // We don't enqueue the node or add it to the network
                        // Removal of the node is already handled (as per the contract of `update()`)
                        continue;
                    }

                    // Then we can add this node, and enqueue it to explore further
                    queue.add(next);
                    nodes.put(next.posKey(), next);
                }
            }
        }
    }

    void removeNode(Node toRemove)
    {
        assert toRemove.network() == id;

        nodes.remove(toRemove.posKey());
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
                    final Rotation exitRotation = current.rotation(direction);

                    // The node currently belongs to a network, so it's rotation should never be null
                    assert exitRotation != null;

                    if (!next.update(id, inverseDirection, exitRotation))
                    {
                        // This node could not be updated to be part of the network, so we have to abort here
                        // We don't enqueue the node or add it to the network
                        // Removal of the node is already handled (as per the contract of `update()`)
                        continue;
                    }

                    queue.add(next);
                    visited.remove(next);
                }
            }
        }

        // Any nodes that were not visited, are disconnected and are removed from the network
        for (Node node : visited)
        {
            node.remove();
            nodes.remove(node.posKey());
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
        return pos.equals(source.pos()) ? source : nodes.get(pos.asLong());
    }
}
