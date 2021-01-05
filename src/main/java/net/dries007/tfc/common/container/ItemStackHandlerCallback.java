package net.dries007.tfc.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackHandlerCallback extends ItemStackHandler implements ISlotCallback
{
    private final ISlotCallback callback;

    public ItemStackHandlerCallback(ISlotCallback callback, int slots)
    {
        super(slots);
        this.callback = callback;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return callback.getSlotStackLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return callback.isItemValid(slot, stack);
    }

    @Override
    protected void onContentsChanged(int slot)
    {
        callback.setAndUpdateSlots(slot);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return callback.getSlotStackLimit(slot);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        callback.setAndUpdateSlots(slot);
    }

    @Override
    public void onSlotTake(PlayerEntity player, int slot, ItemStack stack)
    {
        callback.onSlotTake(player, slot, stack);
    }
}
