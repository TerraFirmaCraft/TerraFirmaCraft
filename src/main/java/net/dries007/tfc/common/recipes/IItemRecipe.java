/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.world.World;

public interface IItemRecipe extends ISimpleRecipe<ItemStackRecipeWrapper>
{
    @Override
    default boolean matches(ItemStackRecipeWrapper inv, World worldIn)
    {
        return false;
    }
}
