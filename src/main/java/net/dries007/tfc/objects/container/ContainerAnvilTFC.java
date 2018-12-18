/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.recipes.AnvilRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.inventory.slot.SlotTEInput;
import net.dries007.tfc.objects.te.TEAnvilTFC;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.forge.ForgeStep;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
import static net.dries007.tfc.client.gui.GuiAnvilTFC.BUTTON_ID_STEP_MAX;
import static net.dries007.tfc.client.gui.GuiAnvilTFC.BUTTON_ID_STEP_MIN;
import static net.dries007.tfc.objects.te.TEAnvilTFC.*;

public class ContainerAnvilTFC extends ContainerTE<TEAnvilTFC>
{
    public ContainerAnvilTFC(InventoryPlayer playerInv, TEAnvilTFC te)
    {
        super(playerInv, te, true, 25);
    }

    public void onReceivePacket(int buttonID)
    {
        if (buttonID >= BUTTON_ID_STEP_MIN && buttonID <= BUTTON_ID_STEP_MAX)
        {
            // Add a step to the anvil
            if (attemptWork())
                tile.addStep(ForgeStep.valueOf(buttonID - BUTTON_ID_STEP_MIN));
        }
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory != null)
        {
            addSlotToContainer(new SlotTEInput(inventory, SLOT_INPUT_1, 80, 50, tile));
            addSlotToContainer(new SlotTEInput(inventory, SLOT_INPUT_2, 62, 50, tile));
            addSlotToContainer(new SlotTEInput(inventory, SLOT_HAMMER, 16, 73, tile));
            addSlotToContainer(new SlotTEInput(inventory, SLOT_FLUX, 145, 73, tile));
        }
    }

    private boolean attemptWork()
    {
        // This only runs on server

        // Get the slot for input
        Slot slotInput = inventorySlots.get(SLOT_INPUT_1);
        if (slotInput == null)
            return false;

        ItemStack stack = slotInput.getStack();
        IForgeable cap = stack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);

        // The input must have the forge item capability
        if (cap == null)
            return false;

        // A recipe must exist
        AnvilRecipe recipe = TFCRegistries.ANVIL.getValue(cap.getRecipeName());
        if (recipe == null)
        {
            return false;
        }
        if (tile.getTier().ordinal() < recipe.getTier().ordinal())
        {
            player.sendMessage(new TextComponentString("" + TextFormatting.RED).appendSibling(new TextComponentTranslation(MOD_ID + ".tooltip.anvil_tier_too_low")));
            return false;
        }

        if (!cap.isWorkable())
        {
            player.sendMessage(new TextComponentString("" + TextFormatting.RED).appendSibling(new TextComponentTranslation(MOD_ID + ".tooltip.anvil_too_cold")));
            return false;
        }

        Slot slot = inventorySlots.get(SLOT_HAMMER);
        if (slot == null)
            return false;

        stack = slot.getStack();
        if (!stack.isEmpty())
        {
            stack.damageItem(1, player);
            if (stack.getCount() <= 0)
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.putStack(stack);
            }
            return true;
        }
        else
        {
            // Fallback to the held item if it is a hammer
            stack = player.inventory.mainInventory.get(player.inventory.currentItem);
            if (!stack.isEmpty() && OreDictionaryHelper.doesStackMatchOre(stack, "hammer"))
            {
                stack.damageItem(1, player);
                return true;
            }
            else
            {
                player.sendMessage(new TextComponentString("" + TextFormatting.RED).appendSibling(new TextComponentTranslation(MOD_ID + ".tooltip.anvil_no_hammer")));
                return false;
            }
        }
    }

}
