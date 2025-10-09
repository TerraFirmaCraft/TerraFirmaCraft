package net.dries007.tfc.compat.emi.recipe;

import java.util.List;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.recipes.SimplePotRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiSimplePotRecipe extends EmiBasePotRecipe<SimplePotRecipe>
{
    public EmiSimplePotRecipe(ResourceLocation id, SimplePotRecipe recipe)
    {
        super(id, recipe, 113, 80);
        List<Ingredient> ing = recipe.getItemIngredients();
        for (Ingredient ingredient : ing)
        {
            inputs.add(EmiIngredient.of(ingredient));
        }

        int j = 0;
        for (ItemStackProvider provider : recipe.getOutputItems())
        {
            final List<ItemStack> stacks = provider.dependsOnInput()
                ? collapse(List.of(ing.get(j).getItems()), provider)
                : collapse(provider);

            for (ItemStack stack : stacks)
            {
                if (!stack.isEmpty())
                {
                    outputs.add(EmiStack.of(stack));
                }
            }
        }
        FluidStack fluidOut = recipe.getDisplayFluid();
        outputs.add(EmiStack.of(fluidOut.getFluid(), fluidOut.getAmount()));

    }
}
