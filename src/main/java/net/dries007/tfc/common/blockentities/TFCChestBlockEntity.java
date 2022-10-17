/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.container.RestrictedChestContainer;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.config.TFCConfig;

public class TFCChestBlockEntity extends ChestBlockEntity
{
    public static boolean isValid(ItemStack stack)
    {
        return ItemSizeManager.get(stack).getSize(stack).isEqualOrSmallerThan(TFCConfig.SERVER.chestMaximumItemSize.get());
    }

    public TFCChestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public TFCChestBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.CHEST.get(), pos, state);
    }

    @Override
    public int getContainerSize()
    {
        return 18;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory)
    {
        return new RestrictedChestContainer(TFCContainerTypes.CHEST_9x2.get(), id, inventory, this, 2);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) // should be isItemValid but no access here
    {
        return isValid(stack);
    }

}
