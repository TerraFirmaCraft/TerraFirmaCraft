/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import net.dries007.tfc.api.recipes.knapping.IKnappingType;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.client.button.GuiButtonKnapping;
import net.dries007.tfc.objects.container.ContainerKnapping;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class GuiKnapping extends GuiContainerTFC
{
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/knapping.png");
    private static final ResourceLocation ALT_BG_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/knapping_clay.png");
    private final ResourceLocation buttonTexture;

    public GuiKnapping(Container container, EntityPlayer player, IKnappingType type, ResourceLocation buttonTexture)
    {
        super(container, player.inventory, type == KnappingRecipe.Type.CLAY || type == KnappingRecipe.Type.FIRE_CLAY ? ALT_BG_TEXTURE : BG_TEXTURE);
        this.buttonTexture = buttonTexture;
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
                addButton(new GuiButtonKnapping(x + 5 * y, bx, by, 16, 16, buttonTexture));
            }
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

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        // Check if the container has been updated
        if (inventorySlots instanceof ContainerKnapping && ((ContainerKnapping) inventorySlots).requiresReset)
        {
            for (GuiButton button : buttonList)
            {
                if (button instanceof GuiButtonKnapping)
                {
                    button.visible = false;
                }
            }
            ((ContainerKnapping) inventorySlots).requiresReset = false;
        }
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button instanceof GuiButtonKnapping)
        {
            ((GuiButtonKnapping) button).onClick();
            button.playPressSound(mc.getSoundHandler());
        }
    }
}
