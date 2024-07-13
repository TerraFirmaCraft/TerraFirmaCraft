/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities.rotation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TFCBlockEntity;
import net.dries007.tfc.common.blocks.rotation.AxleBlock;
import net.dries007.tfc.util.rotation.AxleNode;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;

public class AxleBlockEntity extends TFCBlockEntity implements RotatingBlockEntity
{
    private final Node node;
    private boolean invalid;

    public AxleBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.AXLE.get(), pos, state);
    }

    protected AxleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);

        final Direction.Axis axis = state.getValue(AxleBlock.AXIS);

        this.invalid = false;
        this.node = new AxleNode(pos, Node.ofAxis(axis)) {

            @Override
            protected void onInvalidConnection()
            {
                AxleBlockEntity.this.onInvalidConnection();
            }

            @Override
            public String toString()
            {
                return "Axle[pos=%s, axis=%s]".formatted(axis, pos());
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        tag.putBoolean("invalid", invalid);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        invalid = tag.getBoolean("invalid");
    }

    @Override
    protected void onLoadAdditional()
    {
        performNetworkAction(NetworkAction.ADD);
    }

    @Override
    protected void onUnloadAdditional()
    {
        performNetworkAction(NetworkAction.REMOVE);
    }

    @Override
    public void markAsInvalidInNetwork()
    {
        invalid = true;
    }

    @Override
    public boolean isInvalidInNetwork()
    {
        return invalid;
    }

    @Override
    public Node getRotationNode()
    {
        return node;
    }

    private void onInvalidConnection()
    {
        if (level != null)
        {
            markAsInvalidInNetwork();
            level.scheduleTick(getBlockPos(), getBlockState().getBlock(), DELAY_FOR_INVALID_IN_NETWORK);
        }
    }
}
