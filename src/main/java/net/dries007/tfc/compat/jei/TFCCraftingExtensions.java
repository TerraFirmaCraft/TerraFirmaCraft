/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import net.dries007.tfc.common.recipes.AdvancedShapelessRecipe;
import net.dries007.tfc.common.recipes.outputs.ExtraProductModifier;

// todo: advanced shaped recipes?
public final class TFCCraftingExtensions
{
    public static void register(IVanillaCategoryExtensionRegistration registry)
    {
        registry.getCraftingCategory().addExtension(AdvancedShapelessRecipe.class, new ICraftingCategoryExtension<>()
        {
            @Override
            public void setRecipe(RecipeHolder<AdvancedShapelessRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper helper, IFocusGroup focuses)
            {
                final AdvancedShapelessRecipe recipe = recipeHolder.value();
                final NonNullList<Ingredient> ingredients = recipe.getIngredients();
                final List<List<ItemStack>> inputs = ingredients.stream()
                    .map(ingredient -> List.of(ingredient.getItems()))
                    .toList();

                final Optional<Ingredient> primaryIngredient = recipe.getPrimaryIngredient();
                if (primaryIngredient.isEmpty() || primaryIngredient.get().isEmpty())
                {
                    // todo: this appears to be unused in any recipe as of 1.21, can it be removed?
//
//                    final CraftingInput vanillaInput = CraftingInput.of(3, 3, templateInput);
//                    final List<ItemStack> outputItemNoPrimary = Collections.singletonList(recipe.assemble(vanillaInput, registry));
//                    helper.createAndSetOutputs(builder, JEIIntegration.ITEM_STACK, outputItemNoPrimary);
                    helper.createAndSetInputs(builder, JEIIntegration.ITEM_STACK, inputs, 0, 0);
                    return;
                }

                // locate a matching ingredient to the primary ingredient
                final List<ItemStack> primaryItems = List.of(primaryIngredient.get().getItems());
                int primaryIndex = -1;
                for (int i = 0; i < inputs.size(); i++)
                {
                    final List<ItemStack> testItems = inputs.get(i);
                    if (testItems.size() != primaryItems.size()) continue;
                    boolean valid = true;
                    for (int j = 0; j < testItems.size(); j++)
                    {
                        if (testItems.get(j).getItem() != primaryItems.get(j).getItem())
                        {
                            valid = false;
                            break;
                        }
                    }
                    if (valid)
                    {
                        primaryIndex = i;
                        break;
                    }
                }

                final List<ItemStack> outputItems = new ArrayList<>();
                if (primaryIndex != -1)
                {
                    for (ItemStack item : inputs.get(primaryIndex))
                    {
                        for (ItemStack displayItem : recipe.getResult().displayInputs(item))
                        {
                            final ItemStack output = recipe.getResult().getSingleStackDisplayOnly(displayItem);
                            if (!output.isEmpty())
                            {
                                outputItems.add(output);
                            }
                        }
                    }
                }

                final List<IRecipeSlotBuilder> inputSlots = helper.createAndSetInputs(builder, JEIIntegration.ITEM_STACK, inputs, 0, 0);

                if (outputItems.isEmpty())
                {
                    return;
                }

                final IRecipeSlotBuilder outputSlot = helper.createAndSetOutputs(builder, JEIIntegration.ITEM_STACK, outputItems);

                // a focus link here essentially says, this item causes that output. It requires both slots to have the same number of
                // items, which is only true if every input produced exactly one output - a result that depends on its input, or that
                // displays more items than the ingredient itself matches, will not line up, and simply displays without the link.
                if (outputItems.size() == inputs.get(primaryIndex).size())
                {
                    builder.createFocusLink(inputSlots.get(primaryIndex), outputSlot);
                }

                recipe.getRemainder().ifPresent(r -> r.modifiers().forEach(mod -> {
                    if (mod instanceof ExtraProductModifier(ItemStack remainder))
                    {
                        builder.addOutputSlot(60, 0).addItemStack(remainder);
                    }
                }));
            }
        });
    }

}
