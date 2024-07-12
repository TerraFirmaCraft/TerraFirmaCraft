/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.extension;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforge.neoforged.common.crafting.IShapedRecipe;

import net.dries007.tfc.client.ClientHelpers;

public record ExtraProductsExtension(ExtraProductsCraftingRecipe<?> recipe) implements ICraftingCategoryExtension
{
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses)
    {
        // this is JEI's standard crafting recipe ingredient setting
        final List<List<ItemStack>> inputs = recipe.getIngredients().stream()
            .map(ingredient -> List.of(ingredient.getItems()))
            .toList();
        final int width = getWidth();
        final int height = getHeight();

        final List<ItemStack> outputs = new ArrayList<>(recipe.getExtraProducts());
        outputs.add(0, recipe.getResultItem(ClientHelpers.getLevelOrThrow().registryAccess()));
        craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, outputs);
        craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, inputs, width, height);
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return recipe.getId();
    }

    @Override
    public int getWidth()
    {
        return recipe.getDelegate() instanceof IShapedRecipe<?> shaped ? shaped.getRecipeWidth() : 0;
    }

    @Override
    public int getHeight()
    {
        return recipe.getDelegate() instanceof IShapedRecipe<?> shaped ? shaped.getRecipeHeight() : 0;
    }
}
