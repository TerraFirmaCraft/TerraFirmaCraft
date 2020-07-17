/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.patchouli;

import java.util.Objects;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import vazkii.patchouli.api.IComponentRenderContext;

public abstract class IngredientItemStackComponent extends CustomComponent
{
    protected transient Ingredient inputIngredient;
    protected transient ItemStack outputStack;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        super.build(componentX, componentY, pageNum);
        Objects.requireNonNull(inputIngredient, "Input ingredient is null?");
        Objects.requireNonNull(outputStack, "Output stack is null?");
    }

    @Override
    public void render(IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, 0);
        GlStateManager.color(1f, 1f, 1f, 1f);

        context.getGui().mc.getTextureManager().bindTexture(TFCPatchouliPlugin.BOOK_UTIL_TEXTURES);
        Gui.drawModalRectWithCustomSizedTexture(9, 0, 0, 90, 98, 26, 256, 256);

        context.renderIngredient(14, 5, mouseX, mouseY, inputIngredient);
        context.renderItemStack(86, 5, mouseX, mouseY, outputStack);
        GlStateManager.popMatrix();
    }
}
