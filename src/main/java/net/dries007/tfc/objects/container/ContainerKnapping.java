/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.KnappingRecipe;
import net.dries007.tfc.objects.inventory.SlotKnappingOutput;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.SimpleCraftMatrix;

public class ContainerKnapping extends ContainerItemStack
{
    private final SimpleCraftMatrix matrix;
    private final KnappingRecipe.Type type;
    private final ItemStack stackCopy;

    private boolean hasBeenModified;
    public boolean requiresReset;

    public ContainerKnapping(KnappingRecipe.Type type, InventoryPlayer playerInv, ItemStack stack)
    {
        super(playerInv, stack);
        this.itemIndex += 1;
        this.type = type;
        this.stackCopy = this.stack.copy();

        matrix = new SimpleCraftMatrix();
        hasBeenModified = false;
        requiresReset = false;
    }

    public void onUpdate(int slotIdx)
    {
        matrix.set(slotIdx, false);

        if (!hasBeenModified)
        {
            ItemStack stack = Helpers.consumeItem(this.stack, type.getAmountToConsume());
            if (isOffhand)
                player.setHeldItem(EnumHand.OFF_HAND, stack);
            else
                player.setHeldItem(EnumHand.MAIN_HAND, stack);
            hasBeenModified = true;
        }

        // check the pattern
        Slot slot = inventorySlots.get(0);
        if (slot != null)
        {
            KnappingRecipe recipe = getMatchingRecipe();
            if (recipe != null)
            {
                slot.putStack(recipe.getOutput(this.stackCopy));
            }
            else
            {
                slot.putStack(ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        Slot slot = inventorySlots.get(0);
        ItemStack stack = slot.getStack();
        if (!stack.isEmpty())
        {
            player.addItemStackToInventory(stack);
        }
        super.onContainerClosed(player);
    }

    @Override
    protected void addContainerSlots()
    {
        addSlotToContainer(new SlotKnappingOutput(new ItemStackHandler(1), 0, 128, 44, this::resetMatrix));
    }

    private void resetMatrix()
    {
        matrix.setAll(false);
        requiresReset = true;
    }

    private KnappingRecipe getMatchingRecipe()
    {
        return TFCRegistries.KNAPPING.getValuesCollection()
            .stream()
            .filter(x -> x.getType() == type && matrix.matches(x.getMatrix()))
            .findFirst()
            .orElse(null);
    }

    @Override
    protected void addPlayerInventorySlots(InventoryPlayer playerInv)
    {
        // Add Player Inventory Slots (lower down)
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 18));
            }
        }

        for (int k = 0; k < 9; k++)
        {
            addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142 + 18));
        }
    }
}
