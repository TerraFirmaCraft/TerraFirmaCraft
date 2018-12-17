/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.button;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import static net.dries007.tfc.client.gui.GuiAnvilTFC.ANVIL_BACKGROUND;

public class GuiButtonAnvilPlanIcon extends GuiButton implements IButtonTooltip
{
    private final ItemStack displayItem;
    private final String tooltip;

    public GuiButtonAnvilPlanIcon(ItemStack displayItem, int id, int x, int y)
    {
        super(id, x, y, "");

        this.displayItem = displayItem;
        this.tooltip = displayItem.getDisplayName();
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(ANVIL_BACKGROUND);
            hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            drawModalRectWithCustomSizedTexture(x, y, 176, 0, 16, 16, 256, 256);
            // todo: draw item stack ontop of button
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
