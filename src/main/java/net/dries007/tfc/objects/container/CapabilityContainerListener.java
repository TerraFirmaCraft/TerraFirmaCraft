/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
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
 * In TFC, own items use {@link net.minecraft.item.Item#getNBTShareTag(ItemStack)} to sync internal capabilities such as inventory, fluids, etc.
 * Capabilities which are applied to all items (heat, forgeable, food, etc) are synced via this handler
 * We do NOT use {@link ItemStack#setTagCompound(NBTTagCompound)} to save capability data, as that results in duplication of information
 *
 * Below is a forge PR that would solve all these problems, but will likely not get implemented in 1.12.
 * <a href="https://github.com/MinecraftForge/MinecraftForge/pull/5009/files">Capability Sync PR for 1.12</>
 *
 * @author Choonster
 * @author AlcatrazEscapee
 */
@ParametersAreNonnullByDefault
public class CapabilityContainerListener implements IContainerListener
{
    private static final Map<String, Capability<? extends INBTSerializable<? extends NBTBase>>> CAPABILITIES = new HashMap<>();

    static
    {
        CAPABILITIES.put(CapabilityItemHeat.KEY.toString(), CapabilityItemHeat.ITEM_HEAT_CAPABILITY);
        CAPABILITIES.put(CapabilityForgeable.KEY.toString(), CapabilityForgeable.FORGEABLE_CAPABILITY);
        CAPABILITIES.put(CapabilityFood.KEY.toString(), CapabilityFood.CAPABILITY);
    }

    @Nonnull
    public static NBTTagCompound readCapabilityData(ItemStack stack)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        CAPABILITIES.forEach((name, cap) -> {
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
        CAPABILITIES.forEach((name, cap) -> {
            INBTSerializable<? extends NBTBase> capability = stack.getCapability(cap, null);
            if (capability != null)
            {
                ((INBTSerializable) capability).deserializeNBT(nbt.getTag(name));
            }
        });
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

    private boolean shouldSyncItem(ItemStack stack)
    {
        for (Capability<?> capability : CAPABILITIES.values())
        {
            if (stack.hasCapability(capability, null))
            {
                return true;
            }
        }
        return false;
    }
}