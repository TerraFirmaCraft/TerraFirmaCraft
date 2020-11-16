package net.dries007.tfc.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntityType;

public class SnowPileTileEntity extends TFCTileEntity
{
    private BlockState internalState;

    public SnowPileTileEntity()
    {
        this(TFCTileEntities.SNOW_PILE.get());
    }

    protected SnowPileTileEntity(TileEntityType<?> type)
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
        int prevLayers = prevState.getValue(SnowBlock.LAYERS);
        if (prevLayers == 1)
        {
            return internalState;
        }
        return prevState.setValue(SnowBlock.LAYERS, prevLayers - 1);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        internalState = NBTUtil.readBlockState(nbt.getCompound("internalState"));
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.put("internalState", NBTUtil.writeBlockState(internalState));
        return super.save(nbt);
    }
}
