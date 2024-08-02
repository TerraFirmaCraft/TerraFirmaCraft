/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.forge;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.ComponentView;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.recipes.AnvilRecipe;


public final class Forging extends ComponentView<ForgingComponent>
{
    Forging(ItemStack stack)
    {
        super(stack, TFCComponents.FORGING, ForgingComponent.EMPTY);
    }

    /**
     * Sets the current recipe and work target based on the provided anvil recipe and anvil inventory.
     * <strong>Important:</strong> should generally only be called on server, where the inventory seed is known.
     */
    public void setRecipe(@Nullable RecipeHolder<AnvilRecipe> recipe, AnvilRecipe.Inventory inventory)
    {
        setRecipe(recipe, recipe == null ? -1 : recipe.value().computeTarget(inventory));
    }

    /**
     * @return The current anvil recipe, or {@code null} if none is selected, possibly looking up by ID.
     */
    @Nullable
    public AnvilRecipe getRecipe()
    {
        return component.getRecipe();
    }

    /**
     * Sets the current recipe and work target directly.
     */
    public void setRecipe(@Nullable RecipeHolder<AnvilRecipe> recipe, int target)
    {
        apply(component.withRecipe(recipe, target));
    }

    public void addStep(ForgeStep step)
    {
        addStep(step, step.step());
    }

    public void addStep(ForgeStep step, int amount)
    {
        apply(component.withStep(step, amount));
    }

    public List<ForgeStep> lastSteps()
    {
        return component.steps.steps();
    }

    public boolean matches(List<ForgeRule> rules)
    {
        for (ForgeRule rule : rules)
            if (!matches(rule))
                return false;
        return true;
    }

    public boolean matches(ForgeRule rule)
    {
        final List<ForgeStep> steps = component.steps.steps();
        return rule.matches(
            steps.isEmpty() ? null : steps.getLast(),
            steps.size() <= 1 ? null : steps.get(steps.size() - 1),
            steps.size() <= 2 ? null : steps.get(steps.size() - 2)
        );
    }

    /**
     * @return {@code true} if this item has been worked at all.
     */
    public boolean isWorked()
    {
        return component.steps.isWorked();
    }

    /**
     * @return The total number of steps this item has been worked
     */
    public int totalWorked()
    {
        return component.steps.total();
    }

    /**
     * @return The current work value of the item
     */
    public int work()
    {
        return component.work;
    }

    /**
     * @return The current target value of the item
     */
    public int target()
    {
        return component.target;
    }

    public void restoreRecipeAndWork()
    {
        apply(component);
    }
}