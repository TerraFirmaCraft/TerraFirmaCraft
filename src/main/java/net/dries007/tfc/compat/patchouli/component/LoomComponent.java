/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import vazkii.patchouli.api.IComponentRenderContext;

import net.dries007.tfc.common.recipes.LoomRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;

public class LoomComponent extends RecipeComponent<LoomRecipe>
{
    private transient List<ItemStack> inputItems = Collections.emptyList();

    @Override
    public void render(GuiGraphics graphics, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        if (recipe == null) return;

        renderSetup(graphics);

        graphics.blit(PatchouliIntegration.TEXTURE, 9, 0, 0, 90, 98, 26, 256, 256);

        renderItemStacks(context, graphics, 14, 5, mouseX, mouseY, inputItems);
        context.renderItemStack(graphics, 86, 5, mouseX, mouseY, recipe.getResultItem(null));

        graphics.pose().popPose();
    }

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        super.build(componentX, componentY, pageNum);

        if (recipe == null) return;

        inputItems = unpackItemStackIngredient(recipe.getItemStackIngredient());
    }

    @Override
    protected RecipeType<LoomRecipe> getRecipeType()
    {
        return TFCRecipeTypes.LOOM.get();
    }
}
