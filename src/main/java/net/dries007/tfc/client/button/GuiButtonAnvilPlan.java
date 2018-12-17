/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.button;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.dries007.tfc.client.gui.GuiAnvilTFC.ANVIL_BACKGROUND;

@SideOnly(Side.CLIENT)
public class GuiButtonAnvilPlan extends GuiButton implements IButtonTooltip
{
    private final int textureU;
    private final int textureV;
    private final String tooltip;

    public GuiButtonAnvilPlan(int id, int guiLeft, int guiTop)
    {
        // Plan Button
        super(id, guiLeft + 97, guiTop + 49, 18, 18, "");

        this.textureU = 218;
        this.textureV = 0;
        this.tooltip = I18n.format("tfc.tooltip.anvil_plan");
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(ANVIL_BACKGROUND);
            hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            drawModalRectWithCustomSizedTexture(x, y, textureU, textureV, 16, 16, 256, 256);
            mouseDragged(mc, mouseX, mouseY);
        }
    }

    public String getTooltip()
    {
        return tooltip;
    }

    public boolean hasTooltip()
    {
        return tooltip != null;
    }
}
