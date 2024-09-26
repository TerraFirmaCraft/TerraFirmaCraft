/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.network;

import java.util.Comparator;
import java.util.stream.Collectors;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;


public class NetworkManager<T extends Node, N extends Network<T>>
{
    protected final Long2ObjectMap<T> nodes = new Long2ObjectOpenHashMap<>();
    protected final Long2ObjectMap<N> networks = new Long2ObjectOpenHashMap<>();
    private long nextNetworkId = 0;

    /**
     * Performs a network action on a given node.
     * <ul>
     *     <li>{@link Action#ADD ADD} is called when a node is placed in world</li>
     *     <li>{@link Action#UPDATE UPDATE} is called when any change is made to a node's {@link Node#connections()}</li>
     *     <li>{@link Action#UPDATE_IN_NETWORK UPDATE_IN_NETWORK} is called whenever any change is made to internal node properties,
     *     which do not affect connectivity to the network</li>
     *     <li>{@link Action#REMOVE REMOVE} is called when a node is removed from the world</li>
     * </ul>
     * @param node The node to perform an action on
     * @param action The action to be performed.
     * @return {@code true} if successful, {@code false} if the node is invalid and needs to be removed.
     */
    public boolean performAction(T node, Action action)
    {
        return switch (action)
        {
            case ADD -> add(node);
            case UPDATE -> update(node);
            case UPDATE_IN_NETWORK -> {
                updateInNetwork(node);
                yield true;
            }
            case REMOVE -> {
                remove(node);
                yield true;
            }
        };
    }

    @Nullable
    public T nodeAt(BlockPos pos)
    {
        return nodes.get(pos.asLong());
    }

    @Override
    public String toString()
    {
        return networks.values()
            .stream()
            .sorted(Comparator.comparingLong(network -> network.networkId))
            .map(Object::toString)
            .collect(Collectors.joining("\n"));
    }

    private boolean add(T node)
    {
        assert !node.isConnectedToNetwork();

        final boolean isValid = updateConnectionsToAdjacentNetworks(node, null);
        if (isValid && !node.isConnectedToNetwork())
        {
            // The node is valid, but it was not connected to any existing networks. In this case, we have to add a new network
            // for this node itself. It has already been added to the global nodes map
            addNodeToNetwork(createAndAddNetwork(), node);
        }
        if (isValid)
        {
            nodes.put(node.key(), node);
        }
        return isValid;
    }

    private boolean update(T node)
    {
        // The node belongs to an existing network, although it's connectivity has now changed. This may cause it to disconnect
        // from the existing network, cause splits, or cause it to connect to more networks.
        //
        // In order to handle this, we first try re-connecting this node to all adjacent networks. If successful, we may have to merge
        // existing networks if we end up connecting multiple networks. If not, this node may break, removing it from the network.
        //
        // Then, we need to update the source network this node was connecting to, because we may have lost connectivity with this node.
        // This may involve splitting networks into connected components.
        final N originNetwork = getOwningNetwork(node);

        assert node.isConnectedToNetwork();
        assert originNetwork != null;

        final boolean isValid = updateConnectionsToAdjacentNetworks(node, originNetwork);
        if (!isValid)
        {
            // The node is not valid, and has been removed from any networks it was added to, but we remove it from the map of all nodes
            nodes.remove(node.key());
        }

        // After updating, we need to re-check connectivity of the origin network, if valid or not
        updateExistingNetworkAfterRemovingConnectivity(originNetwork);

        return isValid;
    }

    private void remove(T node)
    {
        final N originNetwork = getOwningNetwork(node);

        assert node.isConnectedToNetwork();
        assert originNetwork != null;

        if (originNetwork.nodes.size() == 1)
        {
            // Special case - if we are removing a network where this is the only node, we can skip everything else,
            // and just remove the network
            networks.remove(node.networkId());
            return;
        }

        // Otherwise, remove this node from the origin network, and then update connectivity after removing (possibly
        // involving splitting the network)
        originNetwork.nodes.remove(node.key());
        updateExistingNetworkAfterRemovingConnectivity(originNetwork);
    }

    /**
     * Iterates through all connections this node has to adjacent nodes, and tries to update the state from each connection. This
     * may perform different actions based on the connectivity to adjacent networks
     * <ul>
     *     <li>If the node is not connected to any network, it will be added to the first network it finds a valid connection to</li>
     *     <li>If the node is connected in an invalid state to any network, this will return {@code false} and the node will be
     *     removed from any networks it connect to</li>
     *     <li>If this node causes multiple networks to connect, those networks will be merged</li>
     * </ul>
     * @param node The node to update
     * @param network The current network the node is connected to, or {@code null} if the node is disconnected
     * @return {@code true} if the node is valid after updating, and {@code false} if it is invalid and must be broken
     */
    private boolean updateConnectionsToAdjacentNetworks(T node, @Nullable N network)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        @Nullable LongArraySet queuedNetworksForMerge = null;

        // Iterate through all directions this node connects in
        for (Direction connection : node.connections())
        {
            cursor.setWithOffset(node.pos(), connection);

            final long adj = cursor.asLong();
            final @Nullable T adjNode = nodes.get(adj);
            if (adjNode != null && // There is an adjacent node
                adjNode.connections().contains(connection.getOpposite())) // Which connects in the requested direction
            {
                // We can connect to the adjacent node. Update depending on how many connections we have already seen
                if (!node.isConnectedToNetwork())
                {
                    // The node has not seen any connections yet. We need to first update the node's *state*, from the adjacent node
                    // We should always be able to update this node with the pending connection, since it does not have any
                    updateNodeFrom(node, adjNode, connection, true);
                    network = getOwningNetwork(adjNode);
                    assert network != null;
                    addNodeToNetwork(network, node);
                }
                else if (node.networkId() == adjNode.networkId())
                {
                    // This node has already seen a connection, but it is from the same network. So, we just need to ensure the state
                    // is valid in both connections. If not, the node is invalid, and we queue for breaking.
                    if (!updateNodeFrom(node, adjNode, connection, false))
                    {
                        // Node is not valid, so remove from the existing network and break
                        assert network != null;

                        removeNodeFromNetwork(network, node);
                        return false;
                    }
                }
                else
                {
                    // This node has already seen a connection and been added to a network, but it connects to a different network as well.
                    if (queuedNetworksForMerge == null || !queuedNetworksForMerge.contains(adjNode.networkId()))
                    {
                        // If this is the first time we have seen this network, then enqueue it for merging
                        if (queuedNetworksForMerge == null)
                        {
                            queuedNetworksForMerge = new LongArraySet(4);
                        }
                        queuedNetworksForMerge.add(adjNode.networkId());

                        // Additionally, BFS through the new network, starting from the connecting node, updating each state
                        // from the previous node.
                        updateNodeFrom(adjNode, node, connection.getOpposite(), true);
                        updateNetworkFrom(adjNode);
                    }
                    else if (!updateNodeFrom(node, adjNode, connection, false))
                    {
                        // If the network was seen before, it was already queued for merging, and it was also updated to connect.
                        // So, if we find an invalid connection again, this network connects in multiple invalid ways.
                        assert network != null;
                        removeNodeFromNetwork(network, node);
                        return false;
                    }
                }
            }
        }

        if (queuedNetworksForMerge != null)
        {
            assert network != null;

            // There are networks queued for merging. At this point, we know that this node connects to all networks (and is valid),
            // and by priors, we know that each individual network is connected and legal. So all we need to do is merge, assume everything
            // is valid, and remove the merged networks
            for (LongIterator it = queuedNetworksForMerge.iterator(); it.hasNext(); )
            {
                final long networkId = it.nextLong();
                final Network<T> networkToMerge = networks.remove(networkId);

                assert networkToMerge != null;

                for (Long2ObjectMap.Entry<T> entry : Long2ObjectMaps.fastIterable(networkToMerge.nodes))
                {
                    addNodeToNetwork(network, entry.getValue());
                }
            }
        }

        return true;
    }

    /**
     * Iterates through nodes on the given {@code network} and forms connected components in the network. If multiple connected components
     * are found, they are split into their own unique networks. Note that this function does not do any state legality checks - it assumes
     * that the state of all networks is valid, and {@code network} is valid but possibly disconnected.
     * @param network The network which has nodes, or connectivity removed, and needs to be re-checked for validity.
     */
    private void updateExistingNetworkAfterRemovingConnectivity(N network)
    {
        // Once a node has been updated or removed from a network, the network might be disconnected.
        // We BFS through the network, searching for connected components. If we find more than one,
        // we split them into their own network.

        final LongLinkedOpenHashSet unvisited = new LongLinkedOpenHashSet(network.nodes.keySet());
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        // By default, we assume all nodes are re-added to the original network. Any disconnected components will be added
        // to a new network, and removed from the current network.
        @Nullable N resultNetwork = null;

        while (!unvisited.isEmpty())
        {
            // Find all connected components from an arbitrary unvisited node
            final LongArrayFIFOQueue queue = new LongArrayFIFOQueue(unvisited.size());
            final long origin = unvisited.firstLong();
            final T originNode = network.nodes.get(origin);

            // First time, we use the existing network. Any other times, we create a new network
            if (resultNetwork == null)
            {
                // The origin should still be in the original network, so we make no modifications here
                resultNetwork = network;
            }
            else
            {
                // If we create a new network, then immediately add the origin node to it as the first node
                resultNetwork = createAndAddNetwork();
                network.nodes.remove(origin);
                addNodeToNetwork(resultNetwork, originNode);
            }

            unvisited.remove(origin);
            queue.enqueue(origin);

            while (!queue.isEmpty())
            {
                // Dequeue the current node, which belongs to our already visited network, hence using `resultNetwork` here
                final long current = queue.dequeueLong();
                final T currentNode = resultNetwork.nodes.get(current);
                for (Direction direction : currentNode.connections())
                {
                    cursor.setWithOffset(currentNode.pos(), direction);

                    // If there is potentially a node at this position, query it. Note that the unvisited set will contain
                    // nodes that still belong to the root network - we have not touched them yet. Hence, it is safe to query
                    // `network.nodes` here
                    final long nextKey = cursor.asLong();
                    if (unvisited.contains(nextKey))
                    {
                        final T nextNode = network.nodes.get(nextKey);
                        if (nextNode.connections().contains(direction.getOpposite()))
                        {
                            // Both nodes connect, so we remove that node from the unvisited set, and enqueue for further
                            // exploration for this connected component
                            unvisited.remove(nextKey);
                            queue.enqueue(nextKey);

                            // If we have created a new network, then move the node to the new network
                            if (resultNetwork.networkId != network.networkId)
                            {
                                network.nodes.remove(nextKey);
                                addNodeToNetwork(resultNetwork, nextNode);
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateNetworkFrom(T origin)
    {
        final N network = getOwningNetwork(origin);

        assert origin.isConnectedToNetwork();
        assert network != null;

        final LongArrayFIFOQueue queue = new LongArrayFIFOQueue();
        final LongOpenHashSet visited = new LongOpenHashSet(network.nodes.size());
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        queue.enqueue(origin.key());
        visited.add(origin.key());

        while (!queue.isEmpty())
        {
            final long current = queue.dequeueLong();
            final T currentNode = network.nodes.get(current);

            assert currentNode != null; // Node should belong to the network

            for (Direction connection : currentNode.connections())
            {
                cursor.setWithOffset(currentNode.pos(), connection);

                final long nextKey = cursor.asLong();
                final T nextNode = network.nodes.get(nextKey);
                if (nextNode != null && // There is a node at this position in this network
                    nextNode.connections().contains(connection.getOpposite()) && // Which connects in the requested direction
                    !visited.contains(nextKey) // Which we have not visited yet
                )
                {
                    // Update the node from the previous connection
                    updateNodeFrom(nextNode, currentNode, connection.getOpposite(), true);
                    queue.enqueue(nextKey);
                    visited.add(nextKey);
                }
            }
        }
    }

    private N createAndAddNetwork()
    {
        final long networkId = nextNetworkId;
        final N network = createNetwork(networkId);

        networks.put(networkId, network);
        nextNetworkId++;

        return network;
    }

    @Nullable
    private N getOwningNetwork(T node)
    {
        return networks.get(node.networkId());
    }

    protected void addNodeToNetwork(N network, T node)
    {
        node.connectToNetwork(network.networkId);
        network.nodes.put(node.key(), node);
    }

    protected void updateInNetwork(T node) {}

    protected void removeNodeFromNetwork(N network, T node)
    {
        assert node.networkId() == network.networkId;

        node.connectToNetwork(Node.NO_NETWORK);
        network.nodes.remove(node.key());
    }

    /**
     * @param node The node that is being updated
     * @param adjNode An existing node which is used to propagate state to the updated node
     * @param connection The connection, in <strong>outgoing</strong> convention, from {@code node} that the connection is in
     * @param force If {@code true}, the state should be forcefully updated, and this node should always be able to connect
     * @return {@code true} if the connection is legal, and {@code false} if {@code node} and {@code adjNode} are not able to connect
     */
    @Contract("_, _, _, true -> true")
    protected boolean updateNodeFrom(T node, T adjNode, Direction connection, boolean force)
    {
        return true;
    }

    @SuppressWarnings("unchecked")
    protected N createNetwork(long networkId)
    {
        return (N) new Network<>(networkId);
    }
}
