/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.button;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.te.TELargeVessel;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
import static net.dries007.tfc.client.gui.GuiLargeVesselFluid.LARGE_VESSEL_BACKGROUND;


public class GuiButtonLargeVesselModeTab extends GuiButtonTFC implements IButtonTooltip
{
    private final TFCGuiHandler.Type guiType;
    private final boolean isActive;
    private int renderHeight;

    public GuiButtonLargeVesselModeTab(TFCGuiHandler.Type guiType, TELargeVessel tile, int buttonId, int guiTop, int guiLeft, boolean isActive)
    {
        super(buttonId, guiLeft, guiTop - 19, 31, 20, "");
        this.guiType = guiType;
        this.isActive = isActive;

        switch (guiType)
        {
            case LARGE_VESSEL_FLUID:
                this.x += 4;
                break;
            case LARGE_VESSEL_SOLID:
                this.x += 36;
                break;
            default:
                throw new IllegalArgumentException("Invalid gui type: " + guiType);
        }


        if (isActive)
        {
            this.renderHeight = 19;
        }
        else
        {
            this.renderHeight = 22;
        }
    }

    @Override
    public String getTooltip()
    {
        return MOD_ID + ".tooltip." + guiType.name().toLowerCase();
    }

    @Override
    public boolean hasTooltip()
    {
        return true;
    }

    public boolean isActive()
    {
        return isActive;
    }

    public TFCGuiHandler.Type getGuiType()
    {
        return guiType;
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible && this.enabled)
        {
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(LARGE_VESSEL_BACKGROUND);
            hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            drawModalRectWithCustomSizedTexture(x, y, 194, 0, 31, renderHeight, 256, 256);

            if (guiType == TFCGuiHandler.Type.LARGE_VESSEL_FLUID)
            {
                drawModalRectWithCustomSizedTexture(x + 7, y + 3, 240, 72, 16, 16, 256, 256);
            }
            else if (guiType == TFCGuiHandler.Type.LARGE_VESSEL_SOLID)
            {
                drawModalRectWithCustomSizedTexture(x + 7, y + 3, 112, 20, 16, 16, 128, 128);
            }

            mouseDragged(mc, mouseX, mouseY);
        }
    }
}
