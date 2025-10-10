package net.dries007.tfc.compat.emi.recipe;

import java.util.Arrays;
import java.util.List;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public abstract class GenericRecipe<T extends Recipe<?>> extends BasicEmiRecipe
{
    protected final T recipe;

    public GenericRecipe(EmiRecipeCategory category, ResourceLocation id, T recipe, int width, int height)
    {
        super(category, id, width, height);
        this.recipe = recipe;
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        int x = 6;
        int y = 6;

        for (EmiIngredient ingredient : getInputs())
        {
            Widget slot = widgets.addSlot(ingredient, x, y);
            Bounds bounds = slot.getBounds();
            x = bounds.right() + 3;
        }

        Widget arrow = widgets.addFillingArrow(x, y, 3000);
        x = arrow.getBounds().right() + 3;

        for (EmiIngredient stack : getOutputs())
        {
            Widget slot = widgets.addSlot(stack, x, y).recipeContext(this);
            Bounds bounds = slot.getBounds();
            x = bounds.right() + 3;
        }
    }

    protected static RegistryAccess registryAccess()
    {
        return ClientHelpers.getLevelOrThrow().registryAccess();
    }

    protected static EmiIngredient toIngredient(SizedFluidIngredient ingredient)
    {
        return EmiIngredient.of(Arrays.stream(ingredient.getFluids()).map(s -> EmiStack.of(s.getFluid())).toList(), ingredient.amount());
    }

    protected static EmiIngredient toIngredient(SizedIngredient ingredient)
    {
        return EmiIngredient.of(ingredient.ingredient(), ingredient.count());
    }

    protected static EmiIngredient toIngredient(BlockIngredient ingredient)
    {
        return EmiIngredient.of(ingredient.all().map(ItemStack::new).filter(item -> !item.isEmpty()).map(EmiStack::of).toList());
    }

    protected static EmiIngredient toIngredient(List<Ingredient> ingredients)
    {
        return EmiIngredient.of(ingredients.stream().map(EmiIngredient::of).toList());
    }

    public int compareTo(EmiRecipe other)
    {
        if (other instanceof GenericRecipe<?> generic)
        {
            return id.compareTo(generic.id);
        }
        return 0;
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
