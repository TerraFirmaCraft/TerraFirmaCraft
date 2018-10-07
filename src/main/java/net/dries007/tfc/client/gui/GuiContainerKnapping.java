/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.client.button.GuiButtonKnapping;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.util.Helpers;

public class GuiContainerKnapping extends GuiContainerTFC
{
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(TFCConstants.MOD_ID, "textures/gui/knapping.png");
    private final int slotIdx;
    private final Rock rock;

    private final boolean[] matrix; // true = clicked away
    private boolean hasBeenModified;

    public GuiContainerKnapping(Container container, EntityPlayer player)
    {
        super(container, player.inventory, BG_TEXTURE, "");
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() instanceof ItemRock)
        {
            slotIdx = playerInv.currentItem;
        }
        else
        {
            stack = player.getHeldItemOffhand();
            slotIdx = 40;
        }
        rock = ((ItemRock) stack.getItem()).ore;
        matrix = new boolean[25];
        hasBeenModified = false;

        ySize = 184; // Bigger than normal gui
    }

    @Override
    public void initGui()
    {
        super.initGui();
        for (int x = 0; x < 5; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                int bx = (width - xSize) / 2 + 12 + 16 * x;
                int by = (height - ySize) / 2 + 12 + 16 * y;
                addButton(new GuiButtonKnapping(x + 5 * y, bx, by, 16, 16, rock));
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        // do something with the buttons
        // todo: this needs to all happen on serverside: look up AbstractPacker or some gib
        TerraFirmaCraft.getLog().debug("Pressed button of id: {}", button.id);
        if (button instanceof GuiButtonKnapping)
        {
            ((GuiButtonKnapping) button).onClick();
            matrix[button.id] = true;

            if (!hasBeenModified)
            {
                ItemStack stack = playerInv.getStackInSlot(slotIdx);
                playerInv.setInventorySlotContents(slotIdx, Helpers.consumeItem(stack, 1));
                hasBeenModified = true;
            }

            // check the pattern
            Slot slot = inventorySlots.getSlot(0);
            if (matches())
            {
                slot.putStack(new ItemStack(ItemsTFC.GOLDPAN)); // todo: make based on recipe
            }
            else
            {
                slot.putStack(ItemStack.EMPTY);
            }
        }
    }

    private boolean matches()
    {
        // todo: replace with an actual recipe type deal
        return matrix[0] && !matrix[1];
    }
}
