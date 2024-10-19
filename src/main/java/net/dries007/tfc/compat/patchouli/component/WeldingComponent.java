/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.RecipeType;
import vazkii.patchouli.api.IComponentRenderContext;

import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;

public class WeldingComponent extends RecipeComponent<WeldingRecipe>
{
    @Override
    public void render(GuiGraphics graphics, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        if (recipe == null) return;

        renderSetup(graphics);

        graphics.blit(PatchouliIntegration.TEXTURE, x + 9, y, 0, 116, 98, 26, 256, 256);

        context.renderIngredient(graphics, x + 14, y + 5, mouseX, mouseY, recipe.getFirstInput());
        context.renderIngredient(graphics, x + 42, y + 5, mouseX, mouseY, recipe.getSecondInput());
        context.renderItemStack(graphics, x + 86, y + 5, mouseX, mouseY, recipe.getResultItem(null));

        graphics.pose().popPose();
    }

    @Override
    protected RecipeType<WeldingRecipe> getRecipeType()
    {
        return TFCRecipeTypes.WELDING.get();
    }
}
