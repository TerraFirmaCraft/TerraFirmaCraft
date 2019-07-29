/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.ingredient;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.util.OreDictionaryHelper;

public class IngredientOreDict implements IIngredient<ItemStack>
{
    private final String oreName;
    private final int amount;

    IngredientOreDict(@Nonnull String oreName)
    {
        this(oreName, 1);
    }

    IngredientOreDict(@Nonnull String oreName, int amount)
    {
        this.oreName = oreName;
        this.amount = amount;
    }

    @Override
    public boolean test(ItemStack stack)
    {
        return testIgnoreCount(stack) && stack.getCount() >= amount;
    }

    @Override
    public boolean testIgnoreCount(ItemStack stack)
    {
        return stack != null && !stack.isEmpty() && OreDictionaryHelper.doesStackMatchOre(stack, oreName);
    }

    @Override
    @Nonnull
    public ItemStack consume(ItemStack input)
    {
        input.shrink(amount);
        return input;
    }

    @Override
    public int getAmount()
    {
        return amount;
    }
}
