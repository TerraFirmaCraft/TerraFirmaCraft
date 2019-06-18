/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.nutrient.CapabilityFood;
import net.dries007.tfc.network.PacketCapabilityContainerUpdate;

/**
 * This is a {@link IContainerListener} which will monitor containers and send any capability data changes for IForgeable or IItemHeat
 *
 * @author Choonster
 * @author AlcatrazEscapee
 */
@ParametersAreNonnullByDefault
public class CapabilityContainerListener implements IContainerListener
{
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
        final NonNullList<ItemStack> syncableItemsList = NonNullList.withSize(items.size(), ItemStack.EMPTY);
        for (int index = 0; index < syncableItemsList.size(); index++)
        {
            final ItemStack stack = syncableItemsList.get(index);
            if (shouldSyncItem(stack))
            {
                syncableItemsList.set(index, stack);
            }
            else
            {
                syncableItemsList.set(index, ItemStack.EMPTY);
            }
        }

        final PacketCapabilityContainerUpdate message = new PacketCapabilityContainerUpdate(container.windowId, syncableItemsList);
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
            { // Don't send the message if there's nothing to update
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
        return stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null) || stack.hasCapability(CapabilityFood.CAPABILITY_NUTRIENTS, null);
    }
}