/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.extension;

import java.util.Collections;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.recipes.AdvancedShapelessRecipe;
import net.dries007.tfc.compat.jei.JEIIntegration;

public record AdvancedShapelessExtension(AdvancedShapelessRecipe recipe) implements ICraftingCategoryExtension
{
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper helper, IFocusGroup focuses)
    {
        final NonNullList<Ingredient> ingredients = recipe.getIngredients();
        final List<List<ItemStack>> inputs = ingredients.stream()
            .map(ingredient -> List.of(ingredient.getItems()))
            .toList();
        final List<IRecipeSlotBuilder> inputSlots = helper.createAndSetInputs(builder, JEIIntegration.ITEM_STACK, inputs, 0, 0);

        final Ingredient primaryIngredient = recipe.getPrimaryIngredient();
        if (primaryIngredient == null)
        {
            // This recipe, despite being advanced, does not identity a primary ingredient
            // It most likely relies on the crafting container instead
            // todo: can we support setting the crafting container here?

            final List<ItemStack> outputItemNoPrimary = Collections.singletonList(recipe.getResult().getSingleStack(ItemStack.EMPTY));
            helper.createAndSetOutputs(builder, JEIIntegration.ITEM_STACK, outputItemNoPrimary);
            return;
        }

        // locate a matching ingredient to the primary ingredient
        List<ItemStack> primaryItems = List.of(primaryIngredient.getItems());
        IRecipeSlotBuilder primary = null;
        int i = 0;
        for (List<ItemStack> testItems : inputs)
        {
            IRecipeSlotBuilder slot = inputSlots.get(i);
            if (testItems.size() != primaryItems.size()) continue;
            boolean valid = true;
            for (int j = 0; j < testItems.size(); j++)
            {
                if (testItems.get(j).getItem() != primaryItems.get(j).getItem())
                {
                    valid = false;
                }
            }
            if (valid)
            {
                primary = slot;
                break;
            }
            i++;
        }

        // a focus link here essentially says, this item causes that output
        if (primary != null)
        {
            List<ItemStack> outputItem = inputs.get(i).stream().map(stack -> recipe.getResult().getSingleStack(stack)).toList();
            IRecipeSlotBuilder outputSlot = helper.createAndSetOutputs(builder, JEIIntegration.ITEM_STACK, outputItem);

            builder.createFocusLink(primary, outputSlot);
        }
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return recipe.getId();
    }

    @Override
    public int getWidth()
    {
        return 0; // shapeless
    }

    @Override
    public int getHeight()
    {
        return 0; // shapeless
    }
}
