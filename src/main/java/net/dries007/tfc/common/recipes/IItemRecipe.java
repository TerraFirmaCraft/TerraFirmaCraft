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
