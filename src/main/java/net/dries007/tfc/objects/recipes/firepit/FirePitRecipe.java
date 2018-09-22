/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.recipes.firepit;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;

public class FirePitRecipe
{
    private final ItemStack output;

    private ItemStack input;

    public FirePitRecipe(ItemStack input, ItemStack output)
    {
        IItemHeat cap = input.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
        if (cap == null)
            throw new IllegalArgumentException("The input stack must implement IItemHeat capability for it to be used in a fire pit recipe!");

        this.input = input;
        this.output = output;
    }

    public boolean matchesInput(ItemStack stack)
    {
        return stack.isItemEqual(input);
    }

    public boolean matchesInput(FirePitRecipe recipeOther)
    {
        return matchesInput(recipeOther.input);
    }

    public ItemStack getOutput()
    {
        return output.copy();
    }
}
