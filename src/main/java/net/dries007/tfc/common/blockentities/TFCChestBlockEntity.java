/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.InventoryWrapper;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.common.container.PestContainer;
import net.dries007.tfc.common.container.RestrictedChestContainer;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.config.TFCConfig;

public class TFCChestBlockEntity extends ChestBlockEntity implements PestContainer, ISlotCallback
{
    public static boolean isValid(ItemStack stack)
    {
        return ItemSizeManager.get(stack).getSize(stack).isEqualOrSmallerThan(TFCConfig.SERVER.chestMaximumItemSize.get());
    }

    private @Nullable IItemHandler inventoryHandler = null;

    public TFCChestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public TFCChestBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.CHEST.get(), pos, state);
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
    public boolean canPlaceItem(int slot, ItemStack stack)
    {
        return isValid(stack);
    }

    @Override
    public void setBlockState(BlockState state)
    {
        super.setBlockState(state);
        invalidateInventoryHandler();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return isValid(stack);
    }

    private void invalidateInventoryHandler()
    {
        if (inventoryHandler != null)
        {
            inventoryHandler = null;
        }
    }

    @Nullable
    private IItemHandler getInventoryHandler()
    {
        if (inventoryHandler != null)
        {
            return inventoryHandler;
        }

        assert level != null;

        final BlockState state = getBlockState();
        if (!(state.getBlock() instanceof ChestBlock chest))
        {
            return null;
        }

        @Nullable Container chestContainer = ChestBlock.getContainer(chest, state, level, getBlockPos(), true);
        if (chestContainer == null)
        {
            chestContainer = this;
        }

        return inventoryHandler = new InventoryWrapper(chestContainer, this);
    }
}
