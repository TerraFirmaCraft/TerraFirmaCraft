/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.common.recipes.SimplePotRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.compat.emi.EmiHelpers;

public class EmiSimplePotRecipe extends EmiBasePotRecipe<SimplePotRecipe>
{
    public EmiSimplePotRecipe(ResourceLocation id, SimplePotRecipe recipe)
    {
        super(id, recipe, 113, 80);
        List<Ingredient> ing = recipe.getItemIngredients();
        inputs.addAll(groupSimilar(ing, EmiIngredient::of, Objects::equals));

        List<ItemStack> unsortedStacks = new ArrayList<>();
        int j = 0;
        for (ItemStackProvider provider : recipe.getOutputItems())
        {
            final List<ItemStack> stacks = EmiHelpers.collapse(provider, ing.get(j));

            for (ItemStack stack : stacks)
            {
                if (!stack.isEmpty())
                {
                    unsortedStacks.add(stack);
                }
            }
        }
        outputs.addAll(groupSimilar(unsortedStacks, EmiSimplePotRecipe::toStack, ItemStack::matches));
        FluidStack fluidOut = recipe.getDisplayFluid();
        if (!fluidOut.isEmpty())
        {
            outputs.add(EmiStack.of(fluidOut.getFluid(), fluidOut.getAmount()));
        }

    }

    private static EmiStack toStack(ItemStack stack, int size)
    {
        return EmiStack.of(EmiHelpers.setDefaultNonDecay(stack), size);
    }

}
