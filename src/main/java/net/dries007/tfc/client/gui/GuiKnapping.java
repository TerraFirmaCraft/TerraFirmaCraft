/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.client.button.GuiButtonKnapping;
import net.dries007.tfc.objects.items.rock.ItemRock;

public class GuiKnapping extends GuiContainerTFC
{
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(TFCConstants.MOD_ID, "textures/gui/knapping.png");
    private final int slotIdx;
    private final Rock rock;

    public GuiKnapping(Container container, EntityPlayer player)
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
        rock = ((ItemRock) stack.getItem()).getRock(stack);

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
        if (button instanceof GuiButtonKnapping)
        {
            ((GuiButtonKnapping) button).onClick();
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
        if (clickedMouseButton == 0)
        {
            for (GuiButton button : this.buttonList)
            {
                if (button instanceof GuiButtonKnapping && button.mousePressed(mc, mouseX, mouseY))
                {
                    GuiScreenEvent.ActionPerformedEvent.Pre event = new GuiScreenEvent.ActionPerformedEvent.Pre(this, button, buttonList);
                    if (MinecraftForge.EVENT_BUS.post(event))
                        break;
                    else if (selectedButton == event.getButton())
                        continue;

                    selectedButton = event.getButton();
                    event.getButton().mousePressed(mc, mouseX, mouseY);
                    actionPerformed(event.getButton());

                    MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), buttonList));
                }
            }
        }
    }
}
