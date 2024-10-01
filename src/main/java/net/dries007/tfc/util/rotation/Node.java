/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.rotation;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.calendar.Calendars;

/**
 * The core element of a rotation network. This is supplied as a capability from a {@link net.minecraft.world.level.block.entity.BlockEntity} to expose that entity as connected to the rotation network.
 */
@Deprecated
public abstract class Node
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

    private final BlockPos pos;
    private final long posKey;
    private final EnumSet<Direction> connections;

    private @Nullable Direction sourceDirection;
    private @Nullable Rotation sourceRotation;
    protected long networkId;

    protected Node(BlockPos pos, EnumSet<Direction> connections)
    {
        this.pos = pos.immutable();
        this.posKey = pos.asLong();
        this.connections = connections;
        this.sourceDirection = null;
        this.sourceRotation = null;
        this.networkId = Node.NO_NETWORK;
    }

    /**
     * Returns the set of all possible (including source) directions in which this node connects.
     * Directions are specified in <strong>outgoing</strong> convention, and include possible source directions.
     */
    public final Set<Direction> connections()
    {
        return connections;
    }

    /**
     * @return The position of this node. <strong>Immutable.</strong>
     */
    public final BlockPos pos()
    {
        return pos;
    }

    /**
     * @return The source direction, in <strong>outgoing</strong> convention, from this node. {@code null} indicates this node is a source, or has no power (i.e. is not part of a network)
     */
    @Nullable
    @Contract(pure = true)
    public Direction source()
    {
        return sourceDirection;
    }

    /**
     * If this node is connected to a source, this rotation <strong>should not be null</strong>! It may be stationary, but connected.
     * @return The current source rotation of this node, or {@code null} if it is disconnected. This is the rotation provided incoming to this node by its source.
     */
    @Nullable
    @Contract(pure = true)
    public Rotation rotation()
    {
        return sourceRotation;
    }

    /**
     * This may be {@code null} <strong>if any only if</strong> {@link #network()} is equal to {@link #NO_NETWORK}
     *
     * @param exitDirection A direction, in <strong>outgoing</strong> convention that is contained within {@link #connections()}
     * @return The rotation currently emitted in the target direction.
     */
    @Nullable
    @Contract(pure = true)
    public Rotation rotation(Direction exitDirection)
    {
        if (sourceRotation != null)
        {
            assert sourceDirection != null;
            return rotation(sourceRotation, sourceDirection, exitDirection);
        }
        return null;
    }

    /**
     * Calculates the rotation that would leave this node in a given {@code exitDirection}, when provided a rotation from a given {@code sourceDirection}.
     * <p>
     * Both the {@code sourceDirection} and {@code exitDirection} must only be contained within {@link #connections()}
     *
     * @param sourceRotation The proposed source rotation incoming to this node.
     * @param sourceDirection A direction, in <strong>outgoing</strong> convention that the rotation is incoming on.
     * @param exitDirection A direction, in <strong>outgoing</strong> convention that the rotation is exiting on.
     * @return The rotation that would've been emitted in the target direction, with the provided source rotation
     */
    public abstract Rotation rotation(Rotation sourceRotation, Direction sourceDirection, Direction exitDirection);

    /**
     * @return The ID of the current network this node is owned by. Returns {@link Node#NO_NETWORK} if not within any network.
     */
    public final long network()
    {
        return networkId;
    }

    /**
     * @return {@code true} if this node is connected to a rotational network.
     */
    public final boolean isConnectedToNetwork()
    {
        return networkId != Node.NO_NETWORK;
    }

    /**
     * Updates the current node with rotation parameters, and potentially indicates if this node has become invalid.
     * <p>
     * Note that any node which becomes invalid via this mechanism must handle breaking the block holding this node manually. The node will be removed from the network and not searched from, but no update will be sent to the source block.
     *
     * @param sourceDirection The source direction, specified in <strong>outgoing</strong> convention, same as {@link #connections()}.
     * @param sourceRotation        The rotation of this object. If {@code null}, this update is removing / stopping all rotation.
     * @return {@code true} if this node is compatible with the given parameters.
     */
    @CheckReturnValue
    public boolean update(long networkId, Direction sourceDirection, Rotation sourceRotation)
    {
        this.networkId = networkId;
        this.sourceDirection = sourceDirection;
        this.sourceRotation = sourceRotation;

        return true;
    }

    /**
     * Sets the rotation to a custom rotation when used outside the world, i.e. in patchouli multiblock rendering.
     */
    public void setRotationFromOutsideWorld()
    {
        this.sourceRotation = Rotation.ofFake();
    }

    /**
     * When removing this node from the network, stops all rotation and clears the network ID.
     */
    final void remove()
    {
        this.networkId = Node.NO_NETWORK;
        this.sourceDirection = null;
        this.sourceRotation = null;
    }

    final long posKey()
    {
        return posKey;
    }

    @Override
    public String toString()
    {
        return "Node[connections=%s, pos=[%d, %d, %d], network=%d, rotation=%s]".formatted(
            connections(),
            pos().getX(), pos().getY(), pos().getZ(),
            network(),
            sourceRotation == null ? "null" : "[%s, Rotation[direction=%s, speed=%s]]".formatted(sourceDirection, sourceRotation.direction(), sourceRotation.speed()));
    }
}
