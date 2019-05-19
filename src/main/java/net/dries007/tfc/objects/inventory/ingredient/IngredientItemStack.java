/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class IngredientItemStack implements IIngredient<ItemStack>
{
    private final ItemStack inputStack;

    public IngredientItemStack(ItemStack inputStack)
    {
        this.inputStack = inputStack;
    }

    @Override
    public ItemStack consume(ItemStack input)
    {
        input.shrink(inputStack.getCount());
        return input;
    }

    @Override
    public int getAmount()
    {
        return inputStack.getCount();
    }

    @Override
    public boolean test(ItemStack stack)
    {
        if (stack != null && !stack.isEmpty())
        {
            if (inputStack.getItem() == stack.getItem())
            {
                return inputStack.getMetadata() == OreDictionary.WILDCARD_VALUE || inputStack.getMetadata() == stack.getMetadata();
            }
        }
        return false;
    }
}
