/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;

public class StackFoodPacket
{
    private final int index;

    public StackFoodPacket(int index)
    {
        this.index = index;
    }

    StackFoodPacket(FriendlyByteBuf buffer)
    {
        this.index = buffer.readVarInt();
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(index);
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null)
        {
            if (!(player.containerMenu instanceof InventoryMenu menu) || index < 0 || index >= menu.slots.size())
            {
                return;
            }

            final Slot targetSlot = menu.getSlot(index);
            final ItemStack targetStack = targetSlot.getItem();
            final @Nullable IFood targetCap = FoodCapability.get(targetStack);

            if (targetCap == null || targetStack.getMaxStackSize() == targetStack.getCount() || targetCap.isRotten())
            {
                return;
            }

            List<Slot> stackableSlots = getStackableSlots(targetSlot, menu.slots);
            int currentAmount = targetStack.getCount();
            int remaining = targetStack.getMaxStackSize() - currentAmount;
            long minCreationDate = targetCap.getCreationDate();

            Iterator<Slot> slotIterator = stackableSlots.iterator();
            while (remaining > 0 && slotIterator.hasNext())
            {
                final Slot slot = slotIterator.next();
                final ItemStack stack = slot.getItem();
                final @Nullable IFood cap = FoodCapability.get(stack);

                if (cap == null || cap.isRotten()) continue;

                if (cap.getCreationDate() < minCreationDate)
                {
                    minCreationDate = cap.getCreationDate();
                }

                if (remaining < stack.getCount())
                {
                    currentAmount += remaining;
                    stack.shrink(remaining);
                    remaining = 0;
                }
                else
                {
                    currentAmount += stack.getCount();
                    remaining -= stack.getCount();
                    stack.shrink(stack.getCount());
                }
            }

            targetStack.setCount(currentAmount);
            targetCap.setCreationDate(minCreationDate);

            menu.slotsChanged(menu.getCraftSlots());
        }
    }

    private List<Slot> getStackableSlots(Slot targetSlot, List<Slot> inventorySlots)
    {
        List<Slot> stackableSlots = new ArrayList<>();
        for (Slot slot : inventorySlots)
        {
            if (slot.getSlotIndex() != targetSlot.getSlotIndex() && !(slot instanceof ResultSlot))
            {
                ItemStack stack = slot.getItem();
                if (FoodCapability.areStacksStackableExceptCreationDate(targetSlot.getItem(), stack))
                {
                    stackableSlots.add(slot);
                }
            }
        }
        stackableSlots.sort(Comparator.comparingInt(slot -> slot.getItem().getCount()));
        return stackableSlots;
    }
}
