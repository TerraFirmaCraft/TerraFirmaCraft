/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.ingredient;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.util.OreDictionaryHelper;

public class IngredientOreDict implements IIngredient<ItemStack>
{
    private final String oreName;
    private final int amount;

    public IngredientOreDict(String oreName)
    {
        this(oreName, 1);
    }

    public IngredientOreDict(String oreName, int amount)
    {
        this.oreName = oreName;
        this.amount = amount;
    }

    @Override
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

    @Override
    public boolean test(ItemStack stack)
    {
        if (stack != null && !stack.isEmpty())
        {
            return OreDictionaryHelper.doesStackMatchOre(stack, oreName) && stack.getCount() >= amount;
        }
        return false;
    }
}
