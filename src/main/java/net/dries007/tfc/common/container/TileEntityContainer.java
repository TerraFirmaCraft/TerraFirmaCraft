package net.dries007.tfc.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.common.tileentity.InventoryTileEntity;

public abstract class TileEntityContainer<T extends InventoryTileEntity> extends SimpleContainer
{
    protected final T tile;

    protected TileEntityContainer(ContainerType<?> containerType, T tile, PlayerInventory playerInventory, int windowId)
    {
        super(containerType, windowId);

        this.tile = tile;

        addContainerSlots();
        addPlayerInventorySlots(playerInventory);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index)
    {
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem())
        {
            ItemStack stack = slot.getItem();
            ItemStack stackCopy = stack.copy();

            int containerSlots = slots.size() - playerIn.inventory.items.size();
            if (index < containerSlots)
            {
                if (transferStackOutOfContainer(stack, containerSlots))
                {
                    return ItemStack.EMPTY;
                }
            }
            else
            {
                if (transferStackIntoContainer(stack, containerSlots))
                {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.getCount() == 0)
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }
            if (stack.getCount() == stackCopy.getCount())
            {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, stackCopy);
            return stackCopy;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn)
    {
        return tile.canInteractWith(playerIn);
    }

    public T getTileEntity()
    {
        return tile;
    }

    protected void addContainerSlots() {}

    protected boolean transferStackOutOfContainer(ItemStack stack, int containerSlots)
    {
        return !moveItemStackTo(stack, containerSlots, slots.size(), true);
    }

    protected boolean transferStackIntoContainer(ItemStack stack, int containerSlots)
    {
        return !moveItemStackTo(stack, 0, containerSlots, false);
    }

    @FunctionalInterface
    public interface IFactory<T extends InventoryTileEntity, C extends TileEntityContainer<T>>
    {
        C create(T tile, PlayerInventory playerInventory, int windowId);
    }
}
