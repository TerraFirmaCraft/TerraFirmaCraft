/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.button;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.client.TFCGuiHandler;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class GuiButtonPlayerInventoryTab extends GuiButtonTFC
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/icons.png");

    private final TFCGuiHandler.Type guiType;
    private final boolean isActive;
    private final int textureU;
    private final int textureV;
    private final int iconU;
    private final int iconV;
    private final int iconY;
    private int renderWidth;
    private int iconX;
    private int guiLeft;

    public GuiButtonPlayerInventoryTab(TFCGuiHandler.Type guiType, int guiLeft, int guiTop, int buttonId, boolean isActive)
    {
        super(buttonId, guiLeft + 176, guiTop, 20, 22, "");
        this.guiType = guiType;
        this.isActive = isActive;
        this.guiLeft = guiLeft;

        switch (guiType)
        {
            case INVENTORY:
                this.y += 4;
                this.iconU = 0;
                break;
            case SKILLS:
                this.y += 27;
                this.iconU = 32;
                break;
            case CALENDAR:
                this.y += 50;
                this.iconU = 64;
                break;
            case NUTRITION:
                this.y += 73;
                this.iconU = 96;
                break;
            default:
                throw new IllegalArgumentException("Invalid gui type: " + guiType);
        }

        this.renderWidth = 20;
        this.iconV = 0;
        this.textureV = 0;
        this.iconX = x + 1;
        this.iconY = y + 3;
        if (isActive)
        {
            this.textureU = 128;
        }
        else
        {
            this.x -= 3;
            this.renderWidth += 3;
            this.textureU = 148;
        }
    }

    public void updateGuiLeft(int newGuiLeft)
    {
        // Update variables that use guiLeft as input
        x += newGuiLeft - guiLeft;
        iconX += newGuiLeft - guiLeft;
        guiLeft = newGuiLeft;
        enabled = false;
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
        if (this.visible)
        {
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(BACKGROUND);
            hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            // The button background
            drawModalRectWithCustomSizedTexture(x, y, textureU, textureV, renderWidth, 22, 256, 256);
            // The icon
            drawScaledCustomSizeModalRect(iconX, iconY, iconU, iconV, 32, 32, 16, 16, 256, 256);
            mouseDragged(mc, mouseX, mouseY);
            enabled = true;
        }
    }
}
