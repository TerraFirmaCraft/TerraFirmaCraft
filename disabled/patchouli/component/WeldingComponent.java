/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.item.crafting.RecipeType;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import vazkii.patchouli.api.IComponentRenderContext;

public class WeldingComponent extends RecipeComponent<WeldingRecipe>
{
    @Override
    public void render(PoseStack poseStack, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        if (recipe == null) return;

        renderSetup(poseStack);

        GuiComponent.blit(poseStack, 9, 0, 0, 116, 98, 26, 256, 256);

        context.renderIngredient(poseStack, 14, 5, mouseX, mouseY, recipe.getFirstInput());
        context.renderIngredient(poseStack, 42, 5, mouseX, mouseY, recipe.getSecondInput());
        context.renderItemStack(poseStack, 86, 5, mouseX, mouseY, recipe.getResultItem());

        poseStack.popPose();
    }

    @Override
    protected RecipeType<WeldingRecipe> getRecipeType()
    {
        return TFCRecipeTypes.WELDING.get();
    }
}
