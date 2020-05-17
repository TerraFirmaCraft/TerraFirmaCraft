/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.egg.CapabilityEgg;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.network.PacketCapabilityContainerUpdate;

/**
 * This is a central synchronization manager for item stack capability data that needs to be synced in containers (inventories) of all kinds
 * Since capability data is not synced by default, but a lot of our applications require it to be client visible, we do two things:
 * - On player tick, we perform a second pass of {@link Container#detectAndSendChanges()}, in order to detect and send updates for cases where ONLY capabilities have changed. These are not detected by vanilla's implementation of this method and as a result no packets are sent
 * - This listener itself is used to sync capability data WITHOUT overwriting the client side item stack. It uses {@link INBTSerializable} capabilities and calls deserialization on the client to accomplish this. This avoids issues with packets arriving out of order resulting in perceived "flickering" on the client.
 *
 * To register a capability for synchronization, add it to {@link CapabilityContainerListener#SYNC_CAPS}
 * This will automatically sync any containers it can, as it is added during various spots from {@link net.dries007.tfc.CommonEventHandler}
 */
@ParametersAreNonnullByDefault
public class CapabilityContainerListener implements IContainerListener
{
    /**
     * Capability instances that need syncing, via calling deserializeNBT() on client
     */
    public static final Map<String, Capability<? extends INBTSerializable<? extends NBTBase>>> SYNC_CAPS = new HashMap<>();

    /**
     * Container listeners for each player.
     */
    private static final Map<EntityPlayerMP, IContainerListener> CAPABILITY_LISTENERS = new HashMap<>();

    static
    {
        SYNC_CAPS.put(CapabilityItemHeat.KEY.toString(), CapabilityItemHeat.ITEM_HEAT_CAPABILITY);
        SYNC_CAPS.put(CapabilityForgeable.KEY.toString(), CapabilityForgeable.FORGEABLE_CAPABILITY);
        SYNC_CAPS.put(CapabilityFood.KEY.toString(), CapabilityFood.CAPABILITY);
        SYNC_CAPS.put(CapabilityEgg.KEY.toString(), CapabilityEgg.CAPABILITY);
    }

    /**
     * Adds the listener for a given player to the current container
     * Executes server side
     */
    public static void addTo(Container container, EntityPlayerMP player)
    {
        IContainerListener listener = CAPABILITY_LISTENERS.computeIfAbsent(player, CapabilityContainerListener::new);
        try
        {
            container.addListener(listener);
        }
        catch (IllegalArgumentException e)
        {
            // Listener already listening, this is simpler than ATing and checking if it is
            listener.sendAllContents(container, container.getInventory());
            container.detectAndSendChanges();
        }
    }

    /**
     * Removes the listener for a given player on log out
     * Executes server side
     */
    public static void removeFrom(EntityPlayerMP player)
    {
        CAPABILITY_LISTENERS.remove(player);
    }

    /**
     * Called from various places to do what {@link Container#detectAndSendChanges()} does not: syncs changes in capabilities only.
     */
    public static void syncCapabilityOnlyChanges(Container container, EntityPlayerMP player)
    {
        IContainerListener listener = CAPABILITY_LISTENERS.computeIfAbsent(player, CapabilityContainerListener::new);
        for (int i = 0; i < container.inventorySlots.size(); ++i)
        {
            ItemStack newStack = container.inventorySlots.get(i).getStack();
            ItemStack cachedStack = container.inventoryItemStacks.get(i);

            if (!ItemStack.areItemStacksEqual(cachedStack, newStack))
            {
                // Duplicated from Container#detectAndSendChanges
                boolean clientStackChanged = !ItemStack.areItemStacksEqualUsingNBTShareTag(cachedStack, newStack);
                cachedStack = newStack.isEmpty() ? ItemStack.EMPTY : newStack.copy();

                // If true, the difference will already be handled by vanilla's sync
                if (!clientStackChanged)
                {
                    // There's a capability difference ONLY that needs to be synced, so we use our own handler here, as to not conflict with vanilla's sync, because this won't overwrite the client side item stack
                    // The listener will check if the item actually needs a sync based on capabilities we know we need to sync
                    listener.sendSlotContents(container, i, cachedStack);
                }
            }
        }
    }

    /**
     * Reads capability data and the stack compound tag into a joint share tag. Should be called by {@link net.minecraft.item.Item#getNBTShareTag(ItemStack)}
     */
    @Nonnull
    public static NBTTagCompound readShareTag(ItemStack stack)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagCompound stackNbt = stack.getTagCompound();
        if (stackNbt != null)
        {
            if (stackNbt.isEmpty())
            {
                nbt.setBoolean("empty", true);
            }
            nbt.setTag("stack", stackNbt);
        }
        NBTTagCompound capNbt = readCapabilityData(stack);
        if (!capNbt.isEmpty())
        {
            nbt.setTag("caps", capNbt);
        }
        return nbt;
    }

    /**
     * Applies the share tag from a stack to an item.
     * This should be called via {@link net.minecraft.item.Item#readNBTShareTag(ItemStack, NBTTagCompound)}
     */
    public static void applyShareTag(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            NBTTagCompound stackNbt = nbt.getCompoundTag("stack");
            if (!stackNbt.isEmpty() || nbt.getBoolean("empty"))
            {
                stack.setTagCompound(stackNbt);
            }
            NBTTagCompound capNbt = nbt.getCompoundTag("caps");
            if (!capNbt.isEmpty())
            {
                applyCapabilityData(stack, capNbt);
            }
        }
    }

    @Nonnull
    public static NBTTagCompound readCapabilityData(ItemStack stack)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        SYNC_CAPS.forEach((name, cap) -> {
            INBTSerializable<? extends NBTBase> capability = stack.getCapability(cap, null);
            if (capability != null)
            {
                nbt.setTag(name, capability.serializeNBT());
            }
        });
        return nbt;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void applyCapabilityData(ItemStack stack, NBTTagCompound nbt)
    {
        SYNC_CAPS.forEach((name, cap) -> {
            INBTSerializable<? extends NBTBase> capability = stack.getCapability(cap, null);
            if (capability != null)
            {
                ((INBTSerializable) capability).deserializeNBT(nbt.getTag(name));
            }
        });
    }

    public static boolean shouldSyncItem(ItemStack stack)
    {
        for (Capability<?> capability : SYNC_CAPS.values())
        {
            if (stack.hasCapability(capability, null))
            {
                return true;
            }
        }
        return false;
    }

    private final EntityPlayerMP player;

    public CapabilityContainerListener(EntityPlayerMP player)
    {
        this.player = player;
    }

    /**
     * This is called to send the entire container.
     * It does not update every tick
     */
    @Override
    public void sendAllContents(final Container container, final NonNullList<ItemStack> items)
    {
        // Filter out any items from the list that shouldn't be synced
        final NonNullList<ItemStack> filteredItems = NonNullList.withSize(items.size(), ItemStack.EMPTY);
        for (int index = 0; index < items.size(); index++)
        {
            final ItemStack stack = items.get(index);
            if (shouldSyncItem(stack))
            {
                filteredItems.set(index, stack);
            }
            else
            {
                filteredItems.set(index, ItemStack.EMPTY);
            }
        }

        final PacketCapabilityContainerUpdate message = new PacketCapabilityContainerUpdate(container.windowId, filteredItems);
        if (message.hasData())
        {
            TerraFirmaCraft.getNetwork().sendTo(message, player);
        }
    }

    /**
     * This is called to send a single slot contents. It uses a modified packet factory method to accept a capability instance
     * This only gets called when a slot changes (only non-capability changes count)
     */
    @Override
    public void sendSlotContents(Container container, int slotIndex, ItemStack stack)
    {
        if (shouldSyncItem(stack))
        {
            final PacketCapabilityContainerUpdate message = new PacketCapabilityContainerUpdate(container.windowId, slotIndex, stack);
            if (message.hasData())
            {
                // Don't send the message if there's nothing to update
                TerraFirmaCraft.getNetwork().sendTo(message, player);
            }
        }
    }

    @Override
    public void sendWindowProperty(Container container, int ID, int value) {}

    @Override
    public void sendAllWindowProperties(Container container, IInventory inventory) {}
}