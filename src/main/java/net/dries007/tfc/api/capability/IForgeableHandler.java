/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.objects.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.util.forge.ForgeStep;
import net.dries007.tfc.util.forge.ForgeSteps;

/**
 * This is an advanced IItemHeat implementation that is used by items that can be forged
 * To get this capability, call getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null), then cast to IForgeableHandler if it is safe
 */
public interface IForgeableHandler extends IItemHeat
{
    /**
     * Gets the current amount of work on the object
     */
    int getWork();

    /**
     * Sets the current amount of work on the object
     */
    void setWork(int work);

    /**
     * Gets the current saved recipe name.
     * This can be used with {@link net.dries007.tfc.objects.recipes.anvil.AnvilRecipeManager} to get the actual recipe on TE loading
     * Returns null if no recipe name is currently saved
     */
    @Nullable
    String getRecipeName();

    /**
     * Sets the recipe name from an {@link AnvilRecipe}. If null, sets the recipe name to null
     */
    void setRecipe(@Nullable AnvilRecipe recipe);

    /**
     * Gets the last three steps, wrapped in a {@link ForgeSteps} instance.
     * The return value is nonnull, however the individual steps might be
     */
    @Nonnull
    ForgeSteps getSteps();

    /**
     * Adds a step to the object, shuffling the last three steps down
     */
    void addStep(ForgeStep step);

    /**
     * Resets the object's Forgeable componenets. Used if an item falls out of an anvil without getting worked
     * Purpose is to preserve stackability on items that haven't been worked yet.
     */
    void reset();
}
