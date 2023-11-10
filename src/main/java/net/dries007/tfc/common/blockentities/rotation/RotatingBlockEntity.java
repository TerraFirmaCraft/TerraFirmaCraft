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

import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.Rotation;
import net.dries007.tfc.util.rotation.RotationNetworkManager;

public interface RotatingBlockEntity
{
    /**
     * Handles updates to the rotation network. Must be called through methods which update the block entity's state within the network.
     * <ul>
     *     <li>In {@link BlockEntity#onLoad()}, should be called with {@link NetworkAction#ADD} or {@link NetworkAction#ADD_SOURCE}</li>
     *     <li>In {@link BlockEntity#onChunkUnloaded()}, should be called with {@link NetworkAction#REMOVE}</li>
     *     <li>In {@link BlockEntity#setRemoved()}, should be called with {@link NetworkAction#REMOVE}</li>
     *     <li>Whenever the contents of {@code node.connections()} or {@link Node#rotation(Direction)} become updated, should be called with {@link NetworkAction#UPDATE}</li>
     * </ul>
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

        if (manager != null && !manager.performAction(getRotationNode(), action))
        {
            markAsInvalidInNetwork();
            level.scheduleTick(entity.getBlockPos(), entity.getBlockState().getBlock(), 4);
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
