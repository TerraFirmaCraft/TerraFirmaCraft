/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.recipes.firepit;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.util.Helpers;

public class FirePitRecipe
{
    // todo: add functionality for output to return liquid metal
    private final ItemStack output;

    private ItemStack input;
    private final boolean isOreInput;
    private String oreInput;

    public FirePitRecipe(ItemStack output, ItemStack input)
    {
        IItemHeat cap = input.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
        if (cap == null)
            throw new IllegalArgumentException("The input stack must implement IItemHeat capability for it to be used in a fire pit recipe!");

        this.input = input;
        this.output = output;
        this.isOreInput = false;
    }

    public FirePitRecipe(ItemStack output, String oreInput)
    {
        this.oreInput = oreInput;
        this.output = output;
        this.isOreInput = true;
    }

    public boolean matchesInput(ItemStack stack)
    {
        return isOreInput ? Helpers.doesStackMatchOre(stack, oreInput) : stack.isItemEqual(input);
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
