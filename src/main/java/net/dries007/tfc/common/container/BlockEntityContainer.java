/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

import net.dries007.tfc.common.tileentity.InventoryTileEntity;

public abstract class BlockEntityContainer<T extends InventoryTileEntity<?>> extends SimpleContainer
{
    protected final T blockEntity;

    protected BlockEntityContainer(MenuType<?> containerType, T blockEntity, Inventory playerInventory, int windowId)
    {
        this(containerType, blockEntity, playerInventory, windowId, 0);
    }

    protected BlockEntityContainer(MenuType<?> containerType, T blockEntity, Inventory playerInventory, int windowId, int yOffset)
    {
        super(containerType, windowId, playerInventory, yOffset);

        this.blockEntity = blockEntity;
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        return blockEntity.canInteractWith(playerIn);
    }

    public T getBlockEntity()
    {
        return blockEntity;
    }

    @FunctionalInterface
    public interface Factory<T extends InventoryTileEntity<?>, C extends BlockEntityContainer<T>>
    {
        C create(T tile, Inventory playerInventory, int windowId);
    }
}
