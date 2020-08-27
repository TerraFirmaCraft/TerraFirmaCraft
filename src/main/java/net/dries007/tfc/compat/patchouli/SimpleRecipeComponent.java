/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.patchouli;

import java.util.Collections;
import javax.annotation.Nullable;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import net.dries007.tfc.compat.jei.IJEISimpleRecipe;
import vazkii.patchouli.api.IComponentRenderContext;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public abstract class SimpleRecipeComponent<T extends IJEISimpleRecipe> extends CustomComponent
{
    @Nullable
    protected transient T recipe;

    @Override
    public void render(IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        // Render the recipe
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.translate(posX, posY, 0);
        GlStateManager.color(1f, 1f, 1f, 1f);

        context.getGui().mc.getTextureManager().bindTexture(TFCPatchouliPlugin.BOOK_UTIL_TEXTURES);
        Gui.drawModalRectWithCustomSizedTexture(9, 0, 0, 90, 98, 26, 256, 256);

        if (recipe != null)
        {
            Ingredient inputIngredient = TFCPatchouliPlugin.getIngredient(recipe.getIngredients());
            ItemStack outputStack = recipe.getOutputs().get(0);

            context.renderIngredient(14, 5, mouseX, mouseY, inputIngredient);
            context.renderItemStack(86, 5, mouseX, mouseY, outputStack);
        }
        else
        {
            // If removed, render the indicator instead
            Gui.drawModalRectWithCustomSizedTexture(11, 2, 2, 144, 22, 22, 256, 256);
            Gui.drawModalRectWithCustomSizedTexture(83, 2, 2, 144, 22, 22, 256, 256);
            if (context.isAreaHovered(mouseX, mouseY, 11, 2, 22, 22) || context.isAreaHovered(mouseX, mouseY, 83, 2, 22, 22))
            {
                context.setHoverTooltip(Collections.singletonList(I18n.format(MOD_ID + ".patchouli.recipe_removed")));
            }
        }

        GlStateManager.popMatrix();
    }
}
