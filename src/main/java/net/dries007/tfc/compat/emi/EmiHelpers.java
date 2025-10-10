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

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

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
        return EmiIngredient.of(ingredient.ingredient(), ingredient.count());
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
            .map(FoodCapability::setTransientNonDecaying) // Avoid decaying in JEI views
            .toList();
    }

    public static List<ItemStack> collapse(ItemStackProvider output)
    {
        return List.of(output.getEmptyStack());
    }
}
