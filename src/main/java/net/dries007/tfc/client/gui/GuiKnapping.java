/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.client.button.GuiButtonKnapping;
import net.dries007.tfc.objects.container.ContainerKnapping;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class GuiKnapping extends GuiContainerTFC
{
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/knapping.png");

    private final ResourceLocation buttonTexture;
    private final KnappingType type;

    public GuiKnapping(Container container, EntityPlayer player, KnappingType type, ResourceLocation buttonTexture)
    {
        super(container, player.inventory, BG_TEXTURE);
        this.buttonTexture = buttonTexture;
        this.type = type;
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
        // JEI reloads this after it's recipe gui is closed
        if (inventorySlots instanceof ContainerKnapping)
        {
            ((ContainerKnapping) inventorySlots).requiresReset = true;
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
                    button.visible = ((ContainerKnapping) inventorySlots).getSlotState(button.id);
                }
            }
            ((ContainerKnapping) inventorySlots).requiresReset = false;
        }
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        if (type == KnappingType.CLAY || type == KnappingType.FIRE_CLAY)
        {
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(type == KnappingType.CLAY ? TFCGuiHandler.CLAY_DISABLED_TEXTURE : TFCGuiHandler.FIRE_CLAY_DISABLED_TEXTURE);
            for (GuiButton button : buttonList)
            {
                if (!button.visible)
                {
                    Gui.drawModalRectWithCustomSizedTexture(button.x, button.y, 0, 0, 16, 16, 16, 16);
                }
            }
        }
    }

    @Override
    protected void actionPerformed(@Nonnull GuiButton button)
    {
        if (button instanceof GuiButtonKnapping)
        {
            ((GuiButtonKnapping) button).onClick();
            button.playPressSound(mc.getSoundHandler());
            // Set the client-side matrix
            if (inventorySlots instanceof ContainerKnapping)
            {
                ((ContainerKnapping) inventorySlots).setSlotState(button.id, false);
            }
        }
    }
}
