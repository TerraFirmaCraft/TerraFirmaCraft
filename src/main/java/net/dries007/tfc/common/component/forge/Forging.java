/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.forge;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.ComponentView;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.recipes.AnvilRecipe;


public final class Forging extends ComponentView<ForgingComponent>
{
    Forging(ItemStack stack)
    {
        super(stack, TFCComponents.FORGING, ForgingComponent.DEFAULT);
    }

    /**
     * Sets the current recipe and work target based on the provided anvil recipe and anvil inventory.
     * <strong>Important:</strong> should generally only be called on server, where the inventory seed is known.
     */
    public void setRecipe(@Nullable AnvilRecipe recipe, AnvilRecipe.Inventory inventory)
    {
        setRecipe(recipe, recipe == null ? -1 : recipe.computeTarget(inventory));
    }

    /**
     * Sets the current recipe and work target directly.
     */
    public void setRecipe(@Nullable AnvilRecipe recipe, int target)
    {
        apply(component.withRecipe(recipe, target));
    }

    public void addStep(@Nullable ForgeStep step)
    {
        addStep(step, step == null ? 0 : step.step());
    }

    public void addStep(@Nullable ForgeStep step, int amount)
    {
        apply(component.withStep(step, amount));
    }

    /**
     * This will clear the current recipe, if the item has not been additionally worked. Used when removing an item from an anvil, as it
     * makes the item stackable again - despite the fact we <strong>must</strong> persist the recipe on the item stack, even if it has
     * not been worked.
     */
    public void clearRecipeIfNotWorked()
    {
        apply(component.withNoRecipeIfNotWorked());
    }
}