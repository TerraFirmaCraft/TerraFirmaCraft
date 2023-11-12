/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.rotation.ClutchBlock;
import net.dries007.tfc.util.rotation.NetworkAction;

public class ClutchBlockEntity extends AxleBlockEntity
{
    private boolean powered = false;

    public ClutchBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.CLUTCH.get(), pos, state);
    }

    public ClutchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    protected void loadAdditional(CompoundTag tag)
    {
        super.loadAdditional(tag);
        updateDirection(getBlockState().setValue(ClutchBlock.POWERED, tag.contains("powered", Tag.TAG_BYTE) && tag.getBoolean("powered")));
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putBoolean("powered", powered);
    }

    public void updateDirection(BlockState state)
    {
        if (state.getValue(ClutchBlock.POWERED))
        {
            node.connections().clear();
        }
        else
        {
            node.connections().addAll(getConnections(state));
        }
        performNetworkAction(NetworkAction.UPDATE);

        assert level != null;
        if (!level.isClientSide)
            markForSync();
        powered = state.getValue(ClutchBlock.POWERED);
    }
}
