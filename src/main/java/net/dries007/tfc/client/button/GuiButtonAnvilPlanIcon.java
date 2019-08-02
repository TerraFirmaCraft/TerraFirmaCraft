/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.button;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;

import static net.dries007.tfc.client.gui.GuiAnvilPlan.PLAN_BACKGROUND;

@SideOnly(Side.CLIENT)
public class GuiButtonAnvilPlanIcon extends GuiButtonTFC implements IButtonTooltip
{
    private final ItemStack displayItem;
    private final ResourceLocation recipeName;
    private final String tooltip;

    public GuiButtonAnvilPlanIcon(AnvilRecipe recipe, int id, int x, int y)
    {
        super(id, x, y, 18, 18, "");

        this.displayItem = recipe.getPlanIcon();
        this.recipeName = recipe.getRegistryName();
        this.tooltip = displayItem.getDisplayName();
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(PLAN_BACKGROUND);
            hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            drawModalRectWithCustomSizedTexture(x, y, 176, 0, 18, 18, 256, 256);
            drawItemStack(displayItem, x + 1, y + 1);
            mouseDragged(mc, mouseX, mouseY);
        }
    }

    public ResourceLocation getRecipeName()
    {
        return recipeName;
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
