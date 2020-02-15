/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.button;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class GuiButtonPage extends GuiButton implements IButtonTooltip
{
    private static final ResourceLocation ICONS = new ResourceLocation(MOD_ID, "textures/gui/icons.png");
    private final Type type;
    private final String tooltip; // Lang key

    public GuiButtonPage(int buttonId, int x, int y, Type type, String tooltip)
    {
        super(buttonId, x, y, 14, 14, "");
        this.type = type;
        this.tooltip = tooltip;
    }

    public GuiButtonPage(int buttonId, int x, int y, Type type)
    {
        this(buttonId, x, y, type, null);
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(ICONS);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, type.x, type.y + i * 14, this.width, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    @Override
    public String getTooltip()
    {
        return tooltip;
    }

    @Override
    public boolean hasTooltip()
    {
        return tooltip != null;
    }

    public enum Type
    {
        LEFT(0, 32),
        RIGHT(14, 32);

        private final int x, y;

        Type(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }
    }
}
