/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.dries007.tfc.common.blockentities.TFCBlockEntity;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.Rotation;
import net.dries007.tfc.util.rotation.RotationNetworkManager;

/**
 * The primary implementation of a block entity which can connect to the rotational network. While it is not required to be implemented, this has a lot of convenience implementation which reduces boilerplate required.
 * A rotating block entity needs the following features:
 * <ul>
 *     <li>It needs to call {@link #performNetworkAction(NetworkAction)}, or similar, with a {@link Node} owned by the block entity</li>
 *     <li>If this is not a {@link RotationSinkBlockEntity}, it needs to react to possible add/update events causing this node to become invalid, and break itself. This is handled through {@link #destroyIfInvalid(Level, BlockPos)} called when the block is ticked.</li>
 * </ul>
 */
@Deprecated
public interface RotatingBlockEntity
{
    int DELAY_FOR_INVALID_IN_NETWORK = 4;

    /**
     * Handles updates to the rotation network. Must be called through methods which update the block entity's state within the network.
     * <ul>
     *     <li>In {@link BlockEntity#onLoad()}, should be called with {@link NetworkAction#ADD} or {@link NetworkAction#ADD_SOURCE}</li>
     *     <li>In {@link BlockEntity#onChunkUnloaded()}, should be called with {@link NetworkAction#REMOVE}</li>
     *     <li>In {@link BlockEntity#setRemoved()}, should be called with {@link NetworkAction#REMOVE}</li>
     *     <li>Whenever the contents of {@code node.connections()} or {@link Node#rotation(Direction)} become updated, should be called with {@link NetworkAction#UPDATE}</li>
     * </ul>
     * <p>
     * Note that when implementing on {@link TFCBlockEntity}, this should be implemented through overrides on {@link TFCBlockEntity#onLoadAdditional()} and {@link TFCBlockEntity#onUnloadAdditional()}, which cover add and remove actions, respectively.
     *
     * @param action The action to be performed.
     */
    default void performNetworkAction(NetworkAction action)
    {
        if (isInvalidInNetwork())
        {
            // Perform no actions if the entity is invalid, as it will be broken soon.
            return;
        }

        final BlockEntity entity = self();
        final Level level = entity.getLevel();

        assert level != null;

        final RotationNetworkManager manager = RotationNetworkManager.get(level);

        if (!manager.performAction(getRotationNode(), action))
        {
            markAsInvalidInNetwork();
            level.scheduleTick(entity.getBlockPos(), entity.getBlockState().getBlock(), DELAY_FOR_INVALID_IN_NETWORK);
        }
    }

    /**
     * Marks this block as invalid in the network.
     */
    void markAsInvalidInNetwork();

    /**
     * Is this block currently invalid? This marked state has to exist as we may mark before we can physically remove the block entity.
     */
    boolean isInvalidInNetwork();

    default void destroyIfInvalid(Level level, BlockPos pos)
    {
        if (isInvalidInNetwork())
        {
            level.destroyBlock(pos, true);
        }
    }

    /**
     * @return The network node for this block.
     */
    Node getRotationNode();

    /**
     * @return The current rotation angle, in radians.
     */
    default float getRotationAngle(float partialTick)
    {
        return Rotation.angle(getRotationNode().rotation(), partialTick);
    }

    private BlockEntity self()
    {
        return (BlockEntity) this;
    }
}
