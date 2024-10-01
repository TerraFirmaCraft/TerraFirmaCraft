/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.network;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.dries007.tfc.common.blockentities.TFCBlockEntity;

/**
 * This is the bridge between a {@link Node} and an external block entity. It abstracts the callbacks to the owner of
 * the node in order to be mocked effectively for testing.
 */
public interface RotationOwner
{
    int DELAY_FOR_UPDATE = 1;
    int DELAY_FOR_INVALID_IN_NETWORK = 4;

    static void onTick(ServerLevel level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof RotationOwner owner)
        {
            if (!owner.getRotationNode().valid)
            {
                level.destroyBlock(pos, true);
            }
            else
            {
                // On an update that does not destroy the block, we sync. This is one-tick delayed to avoid syncing repeat updates,
                // as will happen during network calculations.
                ((TFCBlockEntity) owner).markForSync();
            }
        }
    }

    /**
     * Handles updates to the rotation network. Must be called through methods which update the block entity's state within the network.
     * <ul>
     *     <li>In {@link BlockEntity#onLoad()}, should be called with {@link Action#ADD}</li>
     *     <li>In {@link BlockEntity#onChunkUnloaded()}, should be called with {@link Action#REMOVE}</li>
     *     <li>In {@link BlockEntity#setRemoved()}, should be called with {@link Action#REMOVE}</li>
     *     <li>Whenever the contents of {@code node.connections()} are updated, should be called with {@link Action#UPDATE}</li>
     * </ul>
     * <p>
     * Note that when implementing on {@link TFCBlockEntity}, this should be implemented through overrides on {@link TFCBlockEntity#onLoadAdditional()}
     * and {@link TFCBlockEntity#onUnloadAdditional()}, which cover add and remove actions, respectively.
     *
     * @param action The action to be performed.
     */
    default void performNetworkAction(Action action)
    {
        final BlockEntity entity = self();
        if (entity.getLevel() instanceof ServerLevel level && getRotationNode().valid)
        {
            final RotationNetworkManager manager = RotationNetworkManager.get(level);
            if (!manager.performAction(getRotationNode(), action))
            {
                getRotationNode().valid = false;
                level.scheduleTick(entity.getBlockPos(), entity.getBlockState().getBlock(), DELAY_FOR_INVALID_IN_NETWORK);
            }
        }
    }

    default void onUpdate()
    {
        final BlockEntity entity = self();
        if (entity.getLevel() instanceof ServerLevel level)
        {
            level.scheduleTick(entity.getBlockPos(), entity.getBlockState().getBlock(), DELAY_FOR_UPDATE);
        }
    }

    RotationNode getRotationNode();

    BlockPos getBlockPos();

    private TFCBlockEntity self()
    {
        return (TFCBlockEntity) this;
    }
}
