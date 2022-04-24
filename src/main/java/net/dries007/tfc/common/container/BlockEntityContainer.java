/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;

public abstract class BlockEntityContainer<T extends InventoryBlockEntity<?>> extends Container
{
    protected final T blockEntity;

    protected BlockEntityContainer(MenuType<?> containerType, int windowId, T blockEntity)
    {
        super(containerType, windowId);

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

    // Container#moveItemStackTo doesn't check if the slot can accept items before merging stacks, thus this exists
    // without this, stacks can be shift clicked into locked slots (like barrels) that contain the same items
    @Override
    protected boolean moveItemStackTo(ItemStack stack, int min, int max, boolean reversed)
    {
        boolean flag = false;
        int i = min;
        if (reversed)
        {
            i = max - 1;
        }

        if (stack.isStackable())
        {
            while (!stack.isEmpty())
            {
                if (reversed)
                {
                    if (i < min)
                    {
                        break;
                    }
                }
                else if (i >= max)
                {
                    break;
                }

                Slot slot = this.slots.get(i);
                ItemStack containedStack = slot.getItem();
                // Vanilla doesn't check if the stack can be placed in the slot before adding to it
                if (!containedStack.isEmpty() && ItemStack.isSameItemSameTags(stack, containedStack) && slot.mayPlace(stack))
                {
                    int totalCount = containedStack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), stack.getMaxStackSize());
                    if (totalCount <= maxSize)
                    {
                        stack.setCount(0);
                        containedStack.setCount(totalCount);
                        slot.setChanged();
                        flag = true;
                    }
                    else if (containedStack.getCount() < maxSize)
                    {
                        stack.shrink(maxSize - containedStack.getCount());
                        containedStack.setCount(maxSize);
                        slot.setChanged();
                        flag = true;
                    }
                }

                if (reversed)
                {
                    --i;
                }
                else
                {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty())
        {
            if (reversed)
            {
                i = max - 1;
            }
            else
            {
                i = min;
            }

            while (true)
            {
                if (reversed)
                {
                    if (i < min)
                    {
                        break;
                    }
                }
                else if (i >= max)
                {
                    break;
                }

                Slot slot = this.slots.get(i);
                ItemStack containedStack = slot.getItem();
                if (containedStack.isEmpty() && slot.mayPlace(stack))
                {
                    if (stack.getCount() > slot.getMaxStackSize())
                    {
                        slot.set(stack.split(slot.getMaxStackSize()));
                    }
                    else
                    {
                        slot.set(stack.split(stack.getCount()));
                    }

                    slot.setChanged();
                    flag = true;
                    break;
                }

                if (reversed)
                {
                    --i;
                }
                else
                {
                    ++i;
                }
            }
        }

        return flag;
    }

    @FunctionalInterface
    public interface Factory<T extends InventoryBlockEntity<?>, C extends BlockEntityContainer<T>>
    {
        C create(T tile, Inventory playerInventory, int windowId);
    }
}
