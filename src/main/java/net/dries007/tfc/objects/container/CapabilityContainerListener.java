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
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.network.PacketCapabilityContainerUpdate;

/**
 * This is a {@link IContainerListener} which will monitor containers and send any capability data changes for included capabilities
 * Capabilities which are applied to all items (heat, forgeable, food, etc) are synced via this handler
 * We do NOT use {@link ItemStack#setTagCompound(NBTTagCompound)} to save capability data, as that results in duplication of information
 *
 * For containers under our control, they may need to send constant capability updates. In order to avoid ghost items, they ONLY call this listener when they need to sync changes.
 *
 * @author Choonster
 * @author AlcatrazEscapee
 * @see ICapabilityUpdateContainer
 *
 * Below is a forge PR that would solve all these problems, but will likely not get implemented in 1.12.
 * <a href="https://github.com/MinecraftForge/MinecraftForge/pull/5009/files">Capability Sync PR for 1.12</>
 */
@ParametersAreNonnullByDefault
public class CapabilityContainerListener implements IContainerListener
{
    /**
     * Capability instances that need syncing, via calling deserializeNBT() on client
     */
    public static final Map<String, Capability<? extends INBTSerializable<? extends NBTBase>>> SYNC_CAPS = new HashMap<>();

    static
    {
        SYNC_CAPS.put(CapabilityItemHeat.KEY.toString(), CapabilityItemHeat.ITEM_HEAT_CAPABILITY);
        SYNC_CAPS.put(CapabilityForgeable.KEY.toString(), CapabilityForgeable.FORGEABLE_CAPABILITY);
        SYNC_CAPS.put(CapabilityFood.KEY.toString(), CapabilityFood.CAPABILITY);
    }

    /**
     * Adds the listener to a container, and also handles any instances of {@link ICapabilityUpdateContainer}
     * Executes server side
     */
    public static void addTo(Container container, EntityPlayerMP player)
    {
        // Add the listener to the container, and also register it for containers that need additional sync logic
        CapabilityContainerListener listener = new CapabilityContainerListener(player);
        container.addListener(listener);
        if (container instanceof ICapabilityUpdateContainer)
        {
            ((ICapabilityUpdateContainer) container).setCapabilityListener(listener);
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