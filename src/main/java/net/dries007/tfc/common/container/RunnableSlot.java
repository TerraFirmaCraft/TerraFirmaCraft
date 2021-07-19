package net.dries007.tfc.common.container;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class RunnableSlot extends SlotItemHandler
{
    private final Runnable onSlotTake;

    public RunnableSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Runnable onSlotTake)
    {
        super(itemHandler, index, xPosition, yPosition);
        this.onSlotTake = onSlotTake;
    }

    @Override
    @Nonnull
    public ItemStack remove(int amount)
    {
        onSlotTake.run();
        return super.remove(amount);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack)
    {
        return false;
    }
}
