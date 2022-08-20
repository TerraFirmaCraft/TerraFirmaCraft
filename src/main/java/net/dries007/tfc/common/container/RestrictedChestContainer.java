/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.blockentities.TFCChestBlockEntity;

public class RestrictedChestContainer extends ChestMenu
{
    public static RestrictedChestContainer twoRows(int windowId, Inventory inv, FriendlyByteBuf data)
    {
        return new RestrictedChestContainer(TFCContainerTypes.CHEST_9x2.get(), windowId, inv, 2);
    }

    public static RestrictedChestContainer fourRows(int windowId, Inventory inv, FriendlyByteBuf data)
    {
        return new RestrictedChestContainer(TFCContainerTypes.CHEST_9x4.get(), windowId, inv, 4);
    }

    // Default value of false when checked via super(), which calls addSlot()
    // Only set to true to allow this constructor to add slots.
    private final boolean allowAddSlot;

    public RestrictedChestContainer(MenuType<?> type, int id, Inventory inv, int rows)
    {
        this(type, id, inv, new SimpleContainer(9 * rows), rows);
    }

    public RestrictedChestContainer(MenuType<?> type, int id, Inventory inv, net.minecraft.world.Container container, int rows)
    {
        super(type, id, inv, container, rows);
        checkContainerSize(container, rows * 9);
        container.startOpen(inv.player);

        allowAddSlot = true;

        // Container
        for (int row = 0; row < rows; ++row)
        {
            for (int col = 0; col < 9; ++col)
            {
                this.addSlot(new RestrictedSlot(container, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

        // Player Inventory + Hotbar
        final int yOffset = (rows - 4) * 18;
        for (int row = 0; row < 3; ++row)
        {
            for (int col = 0; col < 9; ++col)
            {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 103 + row * 18 + yOffset));
            }
        }

        for (int col = 0; col < 9; ++col)
        {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 161 + yOffset));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        Slot slot = this.slots.get(index);
        if (slot instanceof RestrictedSlot rest && slot.hasItem())
        {
            ItemStack item = slot.getItem();
            if (!rest.mayPlace(item))
            {
                return ItemStack.EMPTY;
            }
        }
        return super.quickMoveStack(player, index);
    }

    @Override
    protected Slot addSlot(Slot slot)
    {
        return allowAddSlot ? super.addSlot(slot) : slot;
    }

    private static class RestrictedSlot extends Slot
    {
        public RestrictedSlot(Container container, int slot, int x, int y)
        {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return super.mayPlace(stack) && TFCChestBlockEntity.isValid(stack);
        }
    }
}
