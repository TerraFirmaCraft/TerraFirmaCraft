/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;

public class FruitTreeLeavesTileEntity extends BerryBushTileEntity
{
    private boolean onYear;

    public FruitTreeLeavesTileEntity()
    {
        super(TFCTileEntities.FRUIT_TREE.get());
        onYear = false;
    }

    @Override
    public void load(BlockState state, CompoundTag nbt)
    {
        onYear = nbt.getBoolean("onYear");
        super.load(state, nbt);
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.putBoolean("onYear", onYear);
        return super.save(nbt);
    }

    public boolean isOnYear()
    {
        return onYear;
    }

    public void setOnYear(boolean onYearIn)
    {
        onYear = onYearIn;
    }
}
