/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes.knapping;

import java.util.function.Function;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.util.IRockObject;

public class KnappingRecipeStone extends KnappingRecipe
{
    private final Function<Rock, ItemStack> supplier;

    public KnappingRecipeStone(KnappingType type, Function<Rock, ItemStack> supplier, String... pattern)
    {
        super(type, false, pattern);
        this.supplier = supplier;
    }

    @Override
    public ItemStack getOutput(ItemStack input)
    {
        if (input.getItem() instanceof IRockObject)
        {
            return supplier.apply(((IRockObject) input.getItem()).getRock(input));
        }
        return ItemStack.EMPTY;
    }
}
