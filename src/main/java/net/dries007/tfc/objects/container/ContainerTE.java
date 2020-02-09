/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.objects.te.ITileFields;
import net.dries007.tfc.objects.te.TEInventory;

/**
 * This is the mother of all Container-with-a-Tile-Entity implementations
 *
 * @param <T> The Tile Entity class
 */
@ParametersAreNonnullByDefault
public abstract class ContainerTE<T extends TEInventory> extends ContainerSimple
{
    protected final T tile;
    protected final EntityPlayer player;

    private final boolean shouldSyncFields;
    private final int yOffset; // The number of pixels higher than normal (If the gui is larger than normal, see Anvil)

    private int[] cachedFields;

    protected ContainerTE(InventoryPlayer playerInv, T tile)
    {
        this(playerInv, tile, 0);
    }

    protected ContainerTE(InventoryPlayer playerInv, T tile, int yOffset)
    {
        this.tile = tile;
        this.player = playerInv.player;
        this.shouldSyncFields = tile instanceof ITileFields;
        this.yOffset = yOffset;

        addContainerSlots();
        addPlayerInventorySlots(playerInv);
    }

    @Override
    public void detectAndSendChanges()
    {
        if (shouldSyncFields)
        {
            detectAndSendFieldChanges();
        }
        for (int i = 0; i < inventorySlots.size(); ++i)
        {
            ItemStack stack = inventorySlots.get(i).getStack();
            ItemStack newStack = inventoryItemStacks.get(i);

            if (!ItemStack.areItemStacksEqual(newStack, stack))
            {
                // Since heat temperatures are updated every tick, it can cause network issues (server sending too many update packets = overriding slots, ghost items, etc)
                // To alleviate that, we're gonna update the client on tooltip changes only
                boolean updateClient = true;
                IItemHeat cap1 = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                IItemHeat cap2 = newStack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                if (cap1 != null && cap2 != null && Heat.compareHeat(cap1.getTemperature(), cap2.getTemperature()))
                {
                    updateClient = false;
                }
                // May need to do the same for food decay?
                if (updateClient)
                {
                    newStack = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
                    inventoryItemStacks.set(i, newStack);
                    for (IContainerListener listener : listeners)
                    {
                        listener.sendSlotContents(this, i, newStack);
                    }
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        if (shouldSyncFields)
        {
            ((ITileFields) tile).setField(id, data);
        }
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        // Slot that was clicked
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            ItemStack stackCopy = stack.copy();

            // Transfer out of the container
            int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();
            if (index < containerSlots)
            {
                if (transferStackOutOfContainer(stack, containerSlots))
                {
                    return ItemStack.EMPTY;
                }
            }
            // Transfer into the container
            else if (transferStackIntoContainer(stack, containerSlots))
            {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0)
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
            if (stack.getCount() == stackCopy.getCount())
            {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
            return stackCopy;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return tile.canInteractWith(player);
    }

    @Override
    protected void addPlayerInventorySlots(InventoryPlayer playerInv)
    {
        super.addPlayerInventorySlots(playerInv, yOffset);
    }

    protected abstract void addContainerSlots();

    protected void detectAndSendFieldChanges()
    {
        ITileFields tileFields = (ITileFields) tile;
        boolean allFieldsHaveChanged = false;
        boolean[] fieldHasChanged = new boolean[tileFields.getFieldCount()];

        if (cachedFields == null)
        {
            cachedFields = new int[tileFields.getFieldCount()];
            allFieldsHaveChanged = true;
        }

        for (int i = 0; i < cachedFields.length; ++i)
        {
            if (allFieldsHaveChanged || cachedFields[i] != tileFields.getField(i))
            {
                cachedFields[i] = tileFields.getField(i);
                fieldHasChanged[i] = true;
            }
        }

        // go through the list of listeners (players using this container) and update them if necessary
        for (IContainerListener listener : this.listeners)
        {
            for (int fieldID = 0; fieldID < tileFields.getFieldCount(); ++fieldID)
            {
                if (fieldHasChanged[fieldID])
                {
                    // Note that although sendWindowProperty takes 2 ints on a server these are truncated to shorts
                    listener.sendWindowProperty(this, fieldID, cachedFields[fieldID]);
                }
            }
        }
    }

    protected boolean transferStackOutOfContainer(ItemStack stack, int containerSlots)
    {
        return !mergeItemStack(stack, containerSlots, inventorySlots.size(), true);
    }

    protected boolean transferStackIntoContainer(ItemStack stack, int containerSlots)
    {
        return !mergeItemStack(stack, 0, containerSlots, false);
    }

    @Deprecated
    protected int[] getSlotShiftOrder(int containerSlots)
    {
        return IntStream.range(0, containerSlots).toArray();
    }
}
