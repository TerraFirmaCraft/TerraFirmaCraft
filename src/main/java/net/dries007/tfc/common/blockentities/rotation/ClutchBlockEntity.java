/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.rotation.AxleBlock;
import net.dries007.tfc.common.blocks.rotation.ClutchBlock;
import net.dries007.tfc.util.network.Action;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;

public class ClutchBlockEntity extends AxleBlockEntity
{
    public ClutchBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.CLUTCH.get(), pos, state);
    }

    public ClutchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        updateConnections();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);

        // When we receive an update from client due to the state changing in neighborChanged(), we need to re-update connections
        updateConnections();
    }

    public void updateConnections()
    {
        final BlockState state = getBlockState();
        final Set<Direction> connections = getRotationNode().connections();

        if (state.getValue(ClutchBlock.POWERED))
        {
            connections.clear();
        }
        else
        {
            connections.addAll(Node.ofAxis(state.getValue(AxleBlock.AXIS)));
        }

        if (level != null)
        {
            performNetworkAction(Action.UPDATE);
            if (!level.isClientSide)
            {
                markForSync();
            }
        }
    }
}
