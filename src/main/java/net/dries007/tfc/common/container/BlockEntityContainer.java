/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;

public abstract class BlockEntityContainer<T extends InventoryBlockEntity<?>> extends Container
{
    protected final T blockEntity;

    protected BlockEntityContainer(MenuType<?> containerType, int windowId, T blockEntity)
    {
        super(blockEntity, containerType, windowId);

        this.blockEntity = blockEntity;
    }

    @Override
    public boolean stillValid(Player player)
    {
        return blockEntity.canInteractWith(player);
    }

    public T getBlockEntity()
    {
        return blockEntity;
    }

    @FunctionalInterface
    public interface Factory<T extends InventoryBlockEntity<?>, C extends BlockEntityContainer<T>>
    {
        C create(T tile, Inventory playerInventory, int windowId);
    }
}
