/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.mechanical;

import java.util.Comparator;
import java.util.stream.Collectors;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;


/**
 * A collection of all {@link RotationNetwork}s present in a single world.
 */
public final class RotationNetworkManager
{
    private final RotationAccess level;
    private final Long2ObjectMap<RotationNetwork> networks;
    private long nextNetworkId;

    public RotationNetworkManager(RotationAccess level)
    {
        this.level = level;
        this.networks = new Long2ObjectOpenHashMap<>();
        this.nextNetworkId = 0;
    }

    /**
     * Attempts to add a given source node to the world. Returns {@code true} if successful, and {@code false} if it should be aborted and the node broken.
     */
    public boolean addSource(Node sourceToAdd)
    {
        // When adding a source, first search and make sure that we don't connect to any existing networks
        // Any disconnected connections would become part of this network, so those are fine to ignore for now

        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (Direction direction : sourceToAdd.connections())
        {
            cursor.setWithOffset(sourceToAdd.pos(), direction);

            final @Nullable Node adjacent = level.getNode(cursor);
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

        sourceToAdd.update(nextNetworkId, null, null);
        network.updateAfterAdd(sourceToAdd, level);

        networks.put(nextNetworkId, network);
        nextNetworkId++;

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
            if (network.updateOnAdd(toAdd))
            {
                if (addedNetwork != null)
                {
                    // More than one network connects to this node, so it must be removed
                    // First, remove it from the network we added it to, but don't call update(), since we haven't done that yet
                    addedNetwork.removeNode(toAdd);
                    return false;
                }
                // Otherwise, this is the first node connecting to this network, so record it
                addedNetwork = network;
            }
        }

        if (addedNetwork != null)
        {
            // This node was added to a network, so we need to update to connect to any a priori disconnected nodes
            addedNetwork.updateAfterAdd(toAdd, level);
        }
        else
        {
            // This node has been added to the world, but is disconnected, so set the disconnected state.
            toAdd.remove();
        }

        // And return true, as the block is always kept
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
                if (network.updateOnAdd(toUpdate))
                {
                    // Remove from the original network, and then update any connected nodes

                    originNetwork.removeNode(toUpdate);
                    originNetwork.updateNetwork();

                    // Return false, indicating the node was broken and needs to be removed
                    return false;
                }
            }

            // The node did not connect to any existing networks outside the current one, so we are free to update the current network with new connectivity
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
        final long networkId = toRemove.network();
        if (networkId != Node.NO_NETWORK)
        {
            final RotationNetwork network = getNetwork(networkId);

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
