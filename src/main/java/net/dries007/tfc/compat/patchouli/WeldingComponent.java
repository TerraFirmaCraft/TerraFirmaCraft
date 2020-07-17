/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.patchouli;

import com.google.gson.annotations.SerializedName;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.recipes.WeldingRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.VariableHolder;

@SuppressWarnings("unused")
public class WeldingComponent extends CustomComponent
{
    @VariableHolder
    @SerializedName("recipe")
    public String recipeName;

    protected transient Ingredient ingredient1, ingredient2;
    protected transient ItemStack outputStack;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        super.build(componentX, componentY, pageNum);
        WeldingRecipe recipe = TFCRegistries.WELDING.getValue(new ResourceLocation(recipeName));
        if (recipe == null)
        {
            throw new IllegalStateException("Unknown recipe in WeldingComponent: " + recipeName);
        }
        ingredient1 = TFCPatchouliPlugin.getIngredient(recipe.getIngredients().get(0));
        ingredient2 = TFCPatchouliPlugin.getIngredient(recipe.getIngredients().get(1));
        outputStack = recipe.getOutput();
    }

    @Override
    public void render(IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, 0);
        GlStateManager.enableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);

        context.getGui().mc.getTextureManager().bindTexture(TFCPatchouliPlugin.BOOK_UTIL_TEXTURES);
        Gui.drawModalRectWithCustomSizedTexture(9, 0, 0, 116, 98, 26, 256, 256);

        context.renderIngredient(14, 5, mouseX, mouseY, ingredient1);
        context.renderIngredient(42, 5, mouseX, mouseY, ingredient2);
        context.renderItemStack(86, 5, mouseX, mouseY, outputStack);
        GlStateManager.popMatrix();
    }
}
