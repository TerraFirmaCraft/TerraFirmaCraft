/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.rotation;

import java.util.Comparator;
import java.util.stream.Collectors;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.tracker.WorldTracker;


/**
 * A collection of all {@link RotationNetwork}s present in a single world. In general interactions should be able to go through the static methods, rather than on an individual instance.
 * <p>
 * A single {@link RotationNetworkManager} is maintained per-world, on both sides.
 */
@Deprecated
public final class RotationNetworkManager implements RotationAccess
{
    public static RotationNetworkManager get(Level level)
    {
        return WorldTracker.get(level).getRotationManager();
    }

    private final Long2ObjectMap<RotationNetwork> networks;

    // This is a cache of all nodes in the world. It's probably not the most efficient data structure, but comparable to fetching block entities.
    // We maintain this mainly due to the fact that when nodes initially load on client, they don't exist in the world yet, so we can't do BFS to structure networks.
    private final Long2ObjectMap<Node> nodes;
    private long nextNetworkId;

    public RotationNetworkManager()
    {
        this.networks = new Long2ObjectOpenHashMap<>();
        this.nodes = new Long2ObjectOpenHashMap<>();
        this.nextNetworkId = 0;
    }

    /**
     * Performs the given action on the network. Note that if {@code action} is {@link NetworkAction#ADD_SOURCE}, then {@code node} <strong>must</strong> be a {@link SourceNode}
     *
     */
    public boolean performAction(Node node, NetworkAction action)
    {
        return switch (action)
            {
                case ADD -> add(node);
                case ADD_SOURCE -> addSource((SourceNode) node);
                case UPDATE -> update(node);
                case REMOVE -> {
                    remove(node);
                    yield true;
                }
            };
    }

    /**
     * Attempts to add a given source node to the world. Returns {@code true} if successful, and {@code false} if it should be aborted and the node broken.
     */
    public boolean addSource(SourceNode sourceToAdd)
    {
        // When adding a source, first search and make sure that we don't connect to any existing networks
        // Any disconnected connections would become part of this network, so those are fine to ignore for now
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (Direction direction : sourceToAdd.connections())
        {
            cursor.setWithOffset(sourceToAdd.pos(), direction);

            final @Nullable Node adjacent = getNode(cursor);
            final Direction inverseDirection = direction.getOpposite();

            if (adjacent != null && // There is a node at this location
                adjacent.network() != Node.NO_NETWORK && // And it already connects to a pre-existing network
                adjacent.connections().contains(inverseDirection) // And it can connect in this direction
            )
            {
                return false; // Then, the source cannot be placed here due to conflict with another network
            }
        }

        // Otherwise, we know the network can be created here, so create a new network ID for it and create the network
        // Then BFS outwards from the source node as the recently added node, to connect to any disconnected nodes
        final RotationNetwork network = new RotationNetwork(nextNetworkId, sourceToAdd);

        sourceToAdd.updateSource(nextNetworkId);
        network.updateAfterAdd(sourceToAdd, this);

        networks.put(nextNetworkId, network);
        nextNetworkId++;

        nodes.put(sourceToAdd.posKey(), sourceToAdd);

        // The source node was added successfully, so return true
        return true;
    }

    /**
     * Attempts to add a given node to the world. Returns {@code true} if successful, and {@code false} if it should be aborted and the node broken.
     */
    public boolean add(Node toAdd)
    {
        @Nullable RotationNetwork addedNetwork = null;
        for (RotationNetwork network : networks.values())
        {
            switch (network.updateOnAdd(toAdd))
            {
                case SUCCESS -> {
                    // Connected to a network
                    assert addedNetwork == null;
                    addedNetwork = network;
                }
                case FAIL_NO_CONNECTION -> {} // Does not connect to this network
                case FAIL_CONNECTED_TO_OTHER_NETWORK -> {
                    // More than one network connects to this node, so it must be removed
                    // First, remove it from the network we added it to, but don't call update(), since we haven't done that yet
                    assert addedNetwork != null;
                    addedNetwork.removeNode(toAdd);
                    return false;
                }
                case FAIL_INVALID_CONNECTION -> {
                    // This connects to the current network in two incompatible ways
                    // It will not be added in this case, so immediately return false
                    return false;
                }
            }
        }

        if (addedNetwork != null)
        {
            // This node was added to a network, so we need to update to connect to any a priori disconnected nodes
            addedNetwork.updateAfterAdd(toAdd, this);
        }
        else
        {
            // This node has been added to the world, but is disconnected, so set the disconnected state.
            // Store the node in the disconnected nodes map
            toAdd.remove();
        }

        // And return true, as the block is always kept
        nodes.put(toAdd.posKey(), toAdd);
        return true;
    }

    /**
     * Called when a <strong>non-source</strong> node in the world is updated in some way which would affect the existing network. This is any change to {@link Node#connections()} or {@link Node#rotation(Direction)}.
     * @param toUpdate The node being updated.
     * @return {@code true} if the update was successful, otherwise the node was removed and needs to be removed from the world.
     */
    public boolean update(Node toUpdate)
    {
        final long networkId = toUpdate.network();
        if (networkId != Node.NO_NETWORK)
        {
            // The node currently belongs to a network
            // We have to try and re-add this node to other networks, to see if the change in connectivity caused two networks to connect
            // If so, this update needs to be reverted and the current block broken, which means removing it from its original network
            final RotationNetwork originNetwork = getNetwork(networkId);

            for (RotationNetwork network : networks.values())
            {
                // Note that the node already belongs to a network, so if this returns true, it is already broken and will not add
                switch (network.updateOnAdd(toUpdate))
                {
                    case SUCCESS -> {
                        assert network.networkId() == networkId;
                        // Still successfully connects to the target network
                    }
                    case FAIL_NO_CONNECTION -> {} // Not connected to this network
                    case FAIL_CONNECTED_TO_OTHER_NETWORK, FAIL_INVALID_CONNECTION -> {
                        // We will receive an already connected to other network if this update manages to connect to a different network
                        // Cannot be added here if this update connects in incompatible ways to the current network

                        // Remove from the original network, and then update any connected nodes
                        originNetwork.removeNode(toUpdate);
                        originNetwork.updateNetwork();

                        // Return false, indicating the node was broken and needs to be removed
                        return false;
                    }
                }
            }

            // We first have to reconsider this node as 'added', because it may have increased connectivity to nodes outside the network, that were disconnected.
            originNetwork.updateAfterAdd(toUpdate, this);

            // Then, we need to update the network, because likewise we may have _lost_ connectivity with this node
            originNetwork.updateNetwork();
            return true;
        }
        else
        {
            // The node does not exist on a network, so an update is akin to adding it
            // We then try and re-add the node here
            return add(toUpdate);
        }
    }

    /**
     * Called when a node in the world is being removed.
     * @param toRemove The node being removed.
     */
    public void remove(Node toRemove)
    {
        nodes.remove(toRemove.posKey());

        final long networkId = toRemove.network();
        if (networkId != Node.NO_NETWORK)
        {
            final @Nullable RotationNetwork network = networks.get(networkId);
            if (network != null) // If the network we are trying to remove from doesn't exist, don't need to do anything
            {
                if (network.isSource(toRemove))
                {
                    // When we remove the source of a network, we remove the entire network
                    network.removeNetwork();
                    networks.remove(networkId);
                }
                else
                {
                    // Otherwise, we need to update the network regularly, after removing the specific node
                    network.removeNode(toRemove);
                    network.updateNetwork();
                }
            }
        }
    }

    public void clear()
    {
        this.nodes.clear();
        this.networks.clear();
        this.nextNetworkId = 0;
    }

    @Nullable
    @Override
    public Node getNode(BlockPos pos)
    {
        return nodes.get(pos.asLong());
    }

    @Override
    public String toString()
    {
        return networks.values()
            .stream()
            .sorted(Comparator.comparingLong(RotationNetwork::networkId))
            .map(Object::toString)
            .collect(Collectors.joining("\n"));
    }

    private RotationNetwork getNetwork(long networkId)
    {
        final RotationNetwork network = networks.get(networkId);
        if (network == null)
        {
            throw new IllegalStateException("Missing network for networkId = " + networkId);
        }
        return network;
    }
}
