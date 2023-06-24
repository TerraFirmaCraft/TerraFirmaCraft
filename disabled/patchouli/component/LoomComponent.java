/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.common.recipes.LoomRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import vazkii.patchouli.api.IComponentRenderContext;

public class LoomComponent extends RecipeComponent<LoomRecipe>
{
    private transient List<ItemStack> inputItems = Collections.emptyList();

    @Override
    public void render(PoseStack poseStack, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        if (recipe == null) return;

        renderSetup(poseStack);

        GuiComponent.blit(poseStack, 9, 0, 0, 90, 98, 26, 256, 256);

        renderItemStacks(context, poseStack, 14, 5, mouseX, mouseY, inputItems);
        context.renderItemStack(poseStack, 86, 5, mouseX, mouseY, recipe.getResultItem());

        poseStack.popPose();
    }

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        super.build(componentX, componentY, pageNum);

        if (recipe == null) return;

        inputItems = unpackItemStackIngredient(new ItemStackIngredient(recipe.getIngredient(), recipe.getInputCount()));
    }

    @Override
    protected RecipeType<LoomRecipe> getRecipeType()
    {
        return TFCRecipeTypes.LOOM.get();
    }
}
