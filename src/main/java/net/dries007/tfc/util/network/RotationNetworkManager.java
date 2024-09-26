/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
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

    public void tick(ServerLevel level)
    {
        for (RotationNetwork network : networks.values())
        {
            network.tick();
        }

        // todo: migrate to a "sync all initially, sync only on changes" model
        if (level.getGameTime() % 20 == 0)
        {
            final List<RotationNetworkUpdatePacket.Network> payload = new ArrayList<>(networks.size());
            for (RotationNetwork network : networks.values())
            {
                payload.add(new RotationNetworkUpdatePacket.Network(
                    network.networkId,
                    network.requiredTorque,
                    network.currentSpeed,
                    network.targetSpeed
                ));
            }
            PacketDistributor.sendToPlayersInDimension(level, new RotationNetworkUpdatePacket(payload));
        }
    }

    @Override
    protected boolean updateNodeFrom(RotationNode node, RotationNode adjNode, Direction connection, boolean force)
    {
        return node.updateFrom(adjNode, connection, Objects.requireNonNull(adjNode.rotation(connection.getOpposite())), force);
    }

    @Override
    protected RotationNetwork createNetwork(@Nullable RotationNetwork parentNetwork, long networkId)
    {
        return new RotationNetwork(parentNetwork, networkId);
    }

    @Override
    protected void addNodeToNetwork(RotationNetwork network, RotationNode node)
    {
        super.addNodeToNetwork(network, node);
        network.addNodeToNetwork(node);
        node.owner.markForSync();
    }

    @Override
    protected void updateInNetwork(RotationNode node)
    {
        final RotationNetwork network = networks.get(node.networkId());
        if (network != null)
        {
            network.updateTargetSpeed();
        }
    }

    @Override
    protected void removeNodeFromNetwork(RotationNetwork network, RotationNode node)
    {
        super.removeNodeFromNetwork(network, node);
        network.removeNodeFromNetwork(node);
        node.owner.markForSync();
    }
}
