/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.network.RotationNetworkUpdatePacket;
import net.dries007.tfc.util.tracker.WorldTracker;


public class RotationNetworkManager extends NetworkManager<RotationNode, RotationNetwork>
{
    public static final float AXLE_TORQUE = 1;
    public static final float GEARBOX_TORQUE = 1.5f;
    public static final float WINDMILL_TORQUE = 2.5f;

    public static final float WINDMILL_PROVIDED_TORQUE = 40;

    public static RotationNetworkManager get(ServerLevel level)
    {
        return WorldTracker.get(level).getRotationManager2();
    }

    private final List<RotationNetwork> pendingRemovals = new ArrayList<>(20);
    private final List<RotationNetwork> pendingUpdates = new ArrayList<>(20);

    public void tick(ServerLevel level)
    {
        for (RotationNetwork network : networks.values())
        {
            network.tick();
        }

        if (!pendingUpdates.isEmpty() || !pendingRemovals.isEmpty())
        {
            final List<RotationNetworkPayload> payload = new ArrayList<>(pendingUpdates.size() + pendingRemovals.size());
            final LongSet updated = new LongOpenHashSet(pendingUpdates.size() + pendingRemovals.size());

            // Do removals first, so any networks that have both a pending update and removal queued get properly removed
            // in practice this is only done on network update, which shouldn't be that frequent anyway
            for (RotationNetwork network : pendingRemovals)
            {
                if (updated.add(network.networkId))
                {
                    payload.add(RotationNetworkPayload.remove(network.networkId));
                }
            }
            for (RotationNetwork network : pendingUpdates)
            {
                if (network.isActive() && updated.add(network.networkId))
                {
                    payload.add(new RotationNetworkPayload(network));
                }
            }
            if (!payload.isEmpty())
            {
                PacketDistributor.sendToPlayersInDimension(level, new RotationNetworkUpdatePacket(payload));
            }
            pendingRemovals.clear();
            pendingUpdates.clear();
        }
    }

    public void syncTo(ServerPlayer player)
    {
        final List<RotationNetworkPayload> payload = new ArrayList<>(networks.size());
        for (RotationNetwork network : networks.values())
        {
            if (network.isActive())
            {
                payload.add(new RotationNetworkPayload(network));
            }
        }
        PacketDistributor.sendToPlayer(player, new RotationNetworkUpdatePacket(payload));
    }

    @Override
    protected boolean updateNodeFrom(RotationNode node, RotationNode adjNode, Direction connection, boolean force)
    {
        return node.updateFrom(adjNode, connection, adjNode.rotation(connection.getOpposite()), force);
    }

    @Override
    protected RotationNetwork createNetwork(@Nullable RotationNetwork parentNetwork, long networkId)
    {
        final RotationNetwork network = new RotationNetwork(parentNetwork, networkId);
        pendingUpdates.add(network);
        return network;
    }

    @Override
    protected void removeNetwork(RotationNetwork network)
    {
        pendingRemovals.add(network);
    }

    @Override
    protected void addNodeToNetwork(RotationNetwork network, RotationNode node)
    {
        super.addNodeToNetwork(network, node);
        network.addNodeToNetwork(node);
        node.owner.onUpdate();
        pendingUpdates.add(network);
    }

    @Override
    protected void updateNetwork(RotationNetwork network)
    {
        network.updateTargetSpeed();
        pendingUpdates.add(network);
    }

    @Override
    protected void removeNodeFromNetwork(RotationNetwork network, RotationNode node)
    {
        super.removeNodeFromNetwork(network, node);
        network.removeNodeFromNetwork(node);
        node.owner.onUpdate();
        pendingUpdates.add(network);
    }

    @Override
    protected void moveNodeBetweenNetworks(RotationNetwork oldNetwork, RotationNetwork newNetwork, RotationNode node)
    {
        super.moveNodeBetweenNetworks(oldNetwork, newNetwork, node);
        oldNetwork.updateTargetSpeed();
        newNetwork.updateTargetSpeed();
        node.owner.onUpdate(); // Only triggers a single markForSync()
        pendingUpdates.add(oldNetwork);
    }
}
