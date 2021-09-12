/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SnowPileBlockEntity extends TFCBlockEntity
{
    private BlockState internalState;

    public SnowPileBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.SNOW_PILE.get(), pos, state);
    }

    protected SnowPileBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);

        this.internalState = Blocks.AIR.defaultBlockState();
    }

    public void setInternalState(BlockState state)
    {
        this.internalState = state;
        markDirtyFast();
    }

    public BlockState getDestroyedState(BlockState prevState)
    {
        int prevLayers = prevState.getValue(SnowLayerBlock.LAYERS);
        if (prevLayers == 1)
        {
            return internalState;
        }
        return prevState.setValue(SnowLayerBlock.LAYERS, prevLayers - 1);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        internalState = NbtUtils.readBlockState(nbt.getCompound("internalState"));
        super.load(nbt);
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.put("internalState", NbtUtils.writeBlockState(internalState));
        return super.save(nbt);
    }
}
