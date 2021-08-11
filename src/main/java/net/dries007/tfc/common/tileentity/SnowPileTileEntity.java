/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SnowPileTileEntity extends TFCTileEntity
{
    private BlockState internalState;

    public SnowPileTileEntity()
    {
        this(TFCTileEntities.SNOW_PILE.get());
    }

    protected SnowPileTileEntity(BlockEntityType<?> type)
    {
        super(type);

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
    public void load(BlockState state, CompoundTag nbt)
    {
        internalState = NbtUtils.readBlockState(nbt.getCompound("internalState"));
        super.load(state, nbt);
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.put("internalState", NbtUtils.writeBlockState(internalState));
        return super.save(nbt);
    }
}
