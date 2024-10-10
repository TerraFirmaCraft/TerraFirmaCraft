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
import net.dries007.tfc.util.network.Action;
import net.dries007.tfc.util.network.RotationNetworkManager;
import net.dries007.tfc.util.network.RotationNode;
import net.dries007.tfc.util.network.RotationOwner;

public class AxleBlockEntity extends TFCBlockEntity implements RotationOwner
{
    private final RotationNode.Axle node;

    public AxleBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.AXLE.get(), pos, state);
    }

    protected AxleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.node = new RotationNode.Axle(this, state.getValue(AxleBlock.AXIS), RotationNetworkManager.AXLE_TORQUE);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        node.saveAdditional(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        node.loadAdditional(tag);
    }

    @Override
    protected void loadAdditionalOnClient(CompoundTag tag, HolderLookup.Provider provider)
    {
        node.loadAdditionalOnClient(tag);
    }

    @Override
    protected void onLoadAdditional()
    {
        performNetworkAction(Action.ADD);
    }

    @Override
    protected void onUnloadAdditional()
    {
        performNetworkAction(Action.REMOVE);
    }

    @Override
    public RotationNode.Axle getRotationNode()
    {
        return node;
    }
}
