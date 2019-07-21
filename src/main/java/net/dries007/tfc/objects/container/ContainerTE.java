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

    private final boolean shouldSyncCaps;
    private final boolean shouldSyncFields;
    private final int yOffset; // The number of pixels higher than normal (If the gui is larger than normal, see Anvil)

    private int[] cachedFields;

    ContainerTE(InventoryPlayer playerInv, T tile)
    {
        this(playerInv, tile, false, 0);
    }

    ContainerTE(InventoryPlayer playerInv, T tile, boolean shouldSyncCaps)
    {
        this(playerInv, tile, shouldSyncCaps, 0);
    }

    ContainerTE(InventoryPlayer playerInv, T tile, boolean shouldSyncCaps, int yOffset)
    {
        this.tile = tile;
        this.player = playerInv.player;
        this.shouldSyncCaps = shouldSyncCaps;
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
        if (shouldSyncCaps)
        {
            detectAndSendAllChanges();
        }
        else
        {
            super.detectAndSendChanges();
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
        // Instead of overriding this, consider overriding transferIntoContainer if that is the only necessary change

        // Slot that was clicked
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack())
            return ItemStack.EMPTY;

        ItemStack stack = slot.getStack();
        ItemStack stackCopy = stack.copy();

        // Transfer out of the container
        int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();
        if (index < containerSlots)
        {
            if (!this.mergeItemStack(stack, containerSlots, inventorySlots.size(), true))
            {
                return ItemStack.EMPTY;
            }
            // This is already called in SlotCallback (which should be used by the Container anyway)
            //tile.setAndUpdateSlots(index);
        }
        // Transfer into the container
        else
        {
            for (int i : getSlotShiftOrder(containerSlots))
            {
                if (inventorySlots.get(i).isItemValid(stack))
                {
                    this.mergeItemStack(stack, i, i + 1, false);
                }
            }
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

    protected void detectAndSendAllChanges()
    {
        for (int i = 0; i < inventorySlots.size(); ++i)
        {
            ItemStack stack = inventorySlots.get(i).getStack();
            ItemStack newStack = inventoryItemStacks.get(i);

            if (!ItemStack.areItemStacksEqual(newStack, stack))
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

    /**
     * Helper method for overriding just container insertion priority
     * The method {@link ContainerTE#transferStackInSlot(EntityPlayer, int)} will search through slots given by this order.
     * Slots with constrained input should be first, general purpose slots later in order to have the best shift-click experience
     */
    protected int[] getSlotShiftOrder(int containerSlots)
    {
        return IntStream.range(0, containerSlots).toArray();
    }
}
