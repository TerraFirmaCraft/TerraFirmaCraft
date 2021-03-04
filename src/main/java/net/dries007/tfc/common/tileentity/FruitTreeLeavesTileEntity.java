package net.dries007.tfc.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

public class FruitTreeLeavesTileEntity extends BerryBushTileEntity
{
    private boolean onYear;

    public FruitTreeLeavesTileEntity()
    {
        super(TFCTileEntities.FRUIT_TREE.get());
        onYear = false;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        onYear = nbt.getBoolean("onYear");
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.putBoolean("onYear", onYear);
        return super.save(nbt);
    }

    public void setOnYear(boolean onYearIn)
    {
        onYear = onYearIn;
    }

    public boolean isOnYear()
    {
        return onYear;
    }
}
