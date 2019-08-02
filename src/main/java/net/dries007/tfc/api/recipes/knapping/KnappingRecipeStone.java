/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes.knapping;

import java.util.function.Function;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.api.util.IRockObject;

public class KnappingRecipeStone extends KnappingRecipe
{
    private final Function<RockCategory, ItemStack> supplier;

    public KnappingRecipeStone(IKnappingType type, Function<RockCategory, ItemStack> supplier, String... pattern)
    {
        super(type, false, pattern);
        this.supplier = supplier;
    }

    @Override
    public ItemStack getOutput(ItemStack input)
    {
        if (input.getItem() instanceof IRockObject)
        {
            return supplier.apply(((IRockObject) input.getItem()).getRockCategory(input));
        }
        return ItemStack.EMPTY;
    }
}
