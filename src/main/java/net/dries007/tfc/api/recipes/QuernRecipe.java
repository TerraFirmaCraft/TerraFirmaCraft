/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

public class QuernRecipe extends IForgeRegistryEntry.Impl<QuernRecipe>
{
    @Nullable
    public static QuernRecipe get(ItemStack item)
    {
        return TFCRegistries.QUERN.getValuesCollection().stream().filter(x -> x.isValidInput(item)).findFirst().orElse(null);
    }

    private IIngredient<ItemStack> inputItem;
    private ItemStack outputItem;

    public QuernRecipe(IIngredient<ItemStack> input, ItemStack output)
    {
        this.inputItem = input;
        this.outputItem = output;

        if (inputItem == null || outputItem == null)
        {
            throw new IllegalArgumentException("Input and output are not allowed to be empty");
        }
    }

    @Nonnull
    public ItemStack getOutputItem(ItemStack stack)
    {
        ItemStack out = outputItem.copy();

        IFood capOut = out.getCapability(CapabilityFood.CAPABILITY, null);
        IFood capIn = stack.getCapability(CapabilityFood.CAPABILITY, null);
        if (capIn != null && capOut != null)
        {
            capOut.setCreationDate(capIn.getCreationDate());
        }
        return out;
    }

    private boolean isValidInput(ItemStack inputItem)
    {
        return this.inputItem.testIgnoreCount(inputItem);
    }
}
