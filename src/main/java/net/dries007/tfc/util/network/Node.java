/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.network;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class Node
{
    public static final int NO_NETWORK = -1;

    /**
     * @return The set of connections in a given axis
     */
    public static EnumSet<Direction> ofAxis(Direction.Axis axis)
    {
        return EnumSet.of(
            Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE),
            Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE)
        );
    }

    /**
     * @return The set of connections (possibly empty) of {@code directions}.
     */
    public static EnumSet<Direction> of(Direction... directions)
    {
        return directions.length == 0 ? EnumSet.noneOf(Direction.class) : EnumSet.copyOf(Arrays.asList(directions));
    }

    private final BlockPos pos;
    private final long posKey;
    private final EnumSet<Direction> connections;
    private long networkId;

    public Node(BlockPos pos, EnumSet<Direction> connections)
    {
        this.pos = pos.immutable();
        this.posKey = pos.asLong();
        this.connections = connections;
        this.networkId = NO_NETWORK;
    }

    @Override
    public String toString()
    {
        return "Node[%s]".formatted(toStringInternal());
    }

    protected final String toStringInternal()
    {
        return "connections=%s, pos=[%d, %d, %d], network=%d".formatted(
            connections,
            pos().getX(), pos().getY(), pos().getZ(),
            networkId);
    }

    final void connectToNetwork(long networkId)
    {
        this.networkId = networkId;
    }

    final boolean isConnectedToNetwork()
    {
        return networkId != NO_NETWORK;
    }

    final long networkId()
    {
        return networkId;
    }

    final BlockPos pos()
    {
        return pos;
    }

    final long key()
    {
        return posKey;
    }

    /**
     * @return A set of all possible directions this node connects in. These are labeled in <strong>outgoing</strong> convention. Any
     * update to the set of exposed connections must be followed by performing the network action {@link Action#UPDATE UPDATE}
     */
    public final Set<Direction> connections()
    {
        return connections;
    }
}
