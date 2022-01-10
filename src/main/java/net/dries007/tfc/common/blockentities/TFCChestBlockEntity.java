/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.capabilities.size.Size;

public class TFCChestBlockEntity extends ChestBlockEntity
{
    public TFCChestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public TFCChestBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.CHEST.get(), pos, state);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) // should be isItemValid but no access here
    {
        return ItemSizeManager.get(stack).getSize(stack).isSmallerThan(Size.VERY_LARGE);
    }
}
