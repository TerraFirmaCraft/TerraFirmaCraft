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
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;

public record StackFoodPacket(int index) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<StackFoodPacket> TYPE = PacketHandler.type("stack_food");
    public static final StreamCodec<ByteBuf, StackFoodPacket> CODEC = ByteBufCodecs.VAR_INT.map(StackFoodPacket::new, StackFoodPacket::index);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null)
        {
            // Only allow stacking food in the inventory - this is the only real way we can ensure that we won't
            // run into slots that behave weirdly, which can lead to duplication issues or other behavior we can't
            // easily predict here
            if (!(player.containerMenu instanceof InventoryMenu menu) || index < 0 || index >= menu.slots.size())
            {
                return;
            }

            // This excludes the inventory crafting output slot - as we won't be able to insert excess / remainder
            // into that slot, so we can't target it to start. If we target another slot, it won't be included.
            final Slot targetSlot = menu.getSlot(index);
            if (targetSlot instanceof ResultSlot)
            {
                return;
            }

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
            FoodCapability.setCreationDate(targetStack, minCreationDate);

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
