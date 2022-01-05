/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class FruitTreeLeavesBlockEntity extends BerryBushBlockEntity
{
    private boolean onYear;

    public FruitTreeLeavesBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.FRUIT_TREE.get(), pos, state);
        onYear = false;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        onYear = nbt.getBoolean("onYear");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putBoolean("onYear", onYear);
        super.saveAdditional(nbt);
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
