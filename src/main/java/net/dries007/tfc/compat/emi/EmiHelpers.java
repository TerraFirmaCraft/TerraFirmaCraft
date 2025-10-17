/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi;

import java.util.Arrays;
import java.util.List;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.compat.emi.stack.EmiSizedIngredient;

public class EmiHelpers
{
    public static RegistryAccess registryAccess()
    {
        return ClientHelpers.getLevelOrThrow().registryAccess();
    }

    public static EmiIngredient toIngredient(SizedFluidIngredient ingredient)
    {
        return EmiIngredient.of(Arrays.stream(ingredient.getFluids()).map(s -> EmiStack.of(s.getFluid())).toList(), ingredient.amount());
    }

    public static EmiIngredient toIngredient(SizedIngredient ingredient)
    {
        return new EmiSizedIngredient(ingredient);
    }

    public static EmiIngredient toIngredient(BlockIngredient ingredient)
    {
        return EmiIngredient.of(ingredient.all().map(ItemStack::new).filter(item -> !item.isEmpty()).map(EmiStack::of).toList());
    }

    public static EmiIngredient toIngredient(List<Ingredient> ingredients)
    {
        return EmiIngredient.of(ingredients.stream().map(EmiIngredient::of).toList());
    }

    public static List<ItemStack> collapse(List<ItemStack> inputs, ItemStackProvider output)
    {
        if (inputs.isEmpty())
        {
            return List.of(output.getEmptyStack());
        }
        return inputs.stream()
            .map(output::getStack)
            .map(EmiHelpers::setDefaultNonDecay)
            .toList();
    }

    public static List<ItemStack> collapse(ItemStackProvider output)
    {
        return List.of(output.getEmptyStack());
    }

    public static List<ItemStack> collapse(ItemStackProvider provider, Ingredient ing)
    {
        return provider.dependsOnInput()
            ? collapse(List.of(ing.getItems()), provider)
            : collapse(provider);
    }

    public static ItemStack setDefaultNonDecay(ItemStack stack)
    {
        return setNewOrCurrentFoodFlag(stack, IFood.TRANSIENT_NEVER_DECAY_FLAG);
    }

    public static EmiStack nonDecayStack(ItemStack stack)
    {
        return EmiStack.of(setDefaultNonDecay(stack));
    }

    /**
     * Sets a food creation date flag without overwriting the current one if present.
     * Should be used for input stacks because their ingredients MIGHT require they be rotten, and we don't want to override that.
     */
    public static ItemStack setNewOrCurrentFoodFlag(ItemStack stack, long flag)
    {
        @Nullable IFood food = FoodCapability.get(stack);
        if (food != null)
        {
            long creationDate = food.getCreationDate();
            if (creationDate == IFood.TRANSIENT_NEVER_DECAY_FLAG || creationDate == IFood.INVISIBLE_NEVER_DECAY_FLAG || creationDate == IFood.NEVER_DECAY_FLAG || creationDate == IFood.ROTTEN_FLAG)
            {
                // Keep current flag
                return stack;
            }
            return FoodCapability.setCreationDate(stack, flag);
        }
        // Not food
        return stack;
    }
}
