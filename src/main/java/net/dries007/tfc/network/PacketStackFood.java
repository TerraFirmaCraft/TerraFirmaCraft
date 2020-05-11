/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;

public class PacketStackFood implements IMessage
{
    private int slotNumber;

    @SuppressWarnings("unused")
    @Deprecated
    public PacketStackFood()
    {
    }

    public PacketStackFood(int slotNumber)
    {
        this.slotNumber = slotNumber;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf)
    {
        slotNumber = byteBuf.readInt();
    }

    @Override
    public void toBytes(ByteBuf byteBuf)
    {
        byteBuf.writeInt(slotNumber);
    }

    public static final class Handler implements IMessageHandler<PacketStackFood, IMessage>
    {
        @Override
        public IMessage onMessage(PacketStackFood message, MessageContext ctx)
        {
            TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
                if (player != null)
                {
                    if (!(player.openContainer instanceof ContainerPlayer) || message.slotNumber < 0 || message.slotNumber >= player.openContainer.inventorySlots.size())
                    {
                        return;
                    }

                    Slot targetSlot = player.openContainer.getSlot(message.slotNumber);
                    ItemStack targetStack = targetSlot.getStack();
                    IFood targetCap = targetStack.getCapability(CapabilityFood.CAPABILITY, null);

                    if (targetCap == null || targetStack.getMaxStackSize() == targetStack.getCount() || targetCap.isRotten())
                    {
                        return;
                    }

                    List<Slot> stackableSlots = getStackableSlots(targetSlot, player.openContainer.inventorySlots);
                    int currentAmount = targetStack.getCount();
                    int remaining = targetStack.getMaxStackSize() - currentAmount;
                    long minCreationDate = targetCap.getCreationDate();

                    Iterator<Slot> slotIterator = stackableSlots.iterator();
                    while (remaining > 0 && slotIterator.hasNext())
                    {
                        Slot slot = slotIterator.next();
                        ItemStack stack = slot.getStack();
                        IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);

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
                }
            });
            return null;
        }

        private List<Slot> getStackableSlots(Slot targetSlot, List<Slot> inventorySlots)
        {
            List<Slot> stackableSlots = new ArrayList<>();
            for (Slot slot : inventorySlots)
            {
                if (slot.getSlotIndex() == targetSlot.getSlotIndex()) continue;
                ItemStack stack = slot.getStack();
                if (CapabilityFood.areStacksStackableExceptCreationDate(targetSlot.getStack(), stack))
                {
                    stackableSlots.add(slot);
                }
            }
            stackableSlots.sort(Comparator.comparingInt(slot -> slot.getStack().getCount()));
            return stackableSlots;
        }

    }
}
