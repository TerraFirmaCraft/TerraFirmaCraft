/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes.knapping;

import net.minecraft.item.ItemStack;

public class KnappingRecipeSimple extends KnappingRecipe
{
    private final ItemStack output;

    public KnappingRecipeSimple(KnappingType type, boolean outsideSlotRequired, ItemStack output, String... pattern)
    {
        super(type, outsideSlotRequired, pattern);
        this.output = output;
    }

    @Override
    public ItemStack getOutput(ItemStack input)
    {
        return output.copy();
    }
}
