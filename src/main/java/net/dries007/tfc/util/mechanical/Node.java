/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.mechanical;

import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

/**
 * The core element of a rotation network. This is supplied as a capability from a {@link net.minecraft.world.level.block.entity.BlockEntity} to expose that entity as connected to the rotation network.
 */
public abstract class Node
{
    public static final int NO_NETWORK = -1;

    // We don't invalidate these, because we're the only consumers, and I honestly cannot be bothered.
    private final LazyOptional<Node> handler;

    private final BlockPos pos;
    private final EnumSet<Direction> connections;

    protected @Nullable Direction sourceDirection;
    protected @Nullable Rotation sourceRotation;
    private long networkId;

    protected Node(BlockPos pos)
    {
        this(pos, EnumSet.noneOf(Direction.class));
    }

    protected Node(BlockPos pos, EnumSet<Direction> connections)
    {
        this.pos = pos;
        this.connections = connections;
        this.handler = LazyOptional.of(() -> this);
        this.sourceDirection = null;
        this.networkId = Node.NO_NETWORK;
    }

    /**
     * Returns the set of all possible (including source) directions in which this node connects.
     * Directions are specified in <strong>outgoing</strong> convention, and include possible source directions.
     */
    public Set<Direction> connections()
    {
        return connections;
    }

    /**
     * @return The position of this node. <strong>Immutable.</strong>
     */
    public BlockPos pos()
    {
        return pos;
    }

    /**
     * @return The source direction, in <strong>outgoing</strong> convention, from this node. {@code null} indicates this node is a source, or has no power (i.e. is not part of a network)
     */
    @Nullable
    public Direction source()
    {
        return sourceDirection;
    }

    /**
     * @param exitDirection A direction, in <strong>outgoing</strong> convention that is contained within {@link #connections()}
     * @return The rotation currently emitted in the target direction.
     */
    @Nullable
    public abstract Rotation rotation(Direction exitDirection);

    /**
     * @return The current source rotation of this node, or {@code null} if it is not rotating. This is the rotation provided incoming to this node by its source.
     */
    @Nullable
    public Rotation rotation()
    {
        return sourceRotation;
    }

    /**
     * @return The ID of the current network this node is owned by. Returns {@link Node#NO_NETWORK} if not within any network.
     */
    public long network()
    {
        return networkId;
    }

    /**
     * @return A handler to expose as a capability from this node.
     */
    public <T> LazyOptional<T> handler()
    {
        return handler.cast();
    }

    /**
     * Updates the current node with rotation parameters.
     *
     * @param sourceDirection The source direction, specified in <strong>outgoing</strong> convention, same as {@link #connections()}.
     * @param rotation        The rotation of this object. If {@code null}, this update is removing / stopping all rotation.
     */
    public final void update(long networkId, @Nullable Direction sourceDirection, @Nullable Rotation rotation)
    {
        this.networkId = networkId;
        this.sourceDirection = sourceDirection;
        this.sourceRotation = rotation;
    }

    /**
     * When removing this node from the network, stops all rotation and clears the network ID.
     */
    public final void remove()
    {
        update(NO_NETWORK, null, null);
    }
}
