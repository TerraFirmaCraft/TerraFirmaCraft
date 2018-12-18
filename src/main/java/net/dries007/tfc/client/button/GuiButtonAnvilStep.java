/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.button;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.util.forge.ForgeStep;

import static net.dries007.tfc.client.gui.GuiAnvilTFC.ANVIL_BACKGROUND;

@SideOnly(Side.CLIENT)
public class GuiButtonAnvilStep extends GuiButtonTFC implements IButtonTooltip
{
    private final int textureU;
    private final int textureV;
    private final String tooltip;

    public GuiButtonAnvilStep(int id, int guiLeft, int guiTop, ForgeStep step)
    {
        super(id, guiLeft + step.getX(), guiTop + step.getY(), 16, 16, "");

        this.textureU = step.getU();
        this.textureV = step.getV();
        this.tooltip = I18n.format("tfc.enum.forge_step." + step.name().toLowerCase());
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(ANVIL_BACKGROUND);
            hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            //drawModalRectWithCustomSizedTexture(x, y, textureU, textureV, 32, 32, 256, 256);
            drawScaledCustomSizeModalRect(x, y, textureU, textureV, 32, 32, 16, 16, 256, 256);
            mouseDragged(mc, mouseX, mouseY);
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
}
