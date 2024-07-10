/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A recipe that operates on a {@link BlockState} as input and produces a {@link BlockState} as output.
 */
public interface IBlockRecipe extends INoopInputRecipe
{
    /**
     * @param input The input to this recipe
     * @return {@code true} if the input matches the recipe.
     */
    boolean matches(BlockState input);

    /**
     * @param input The input to this recipe
     * @return The output of this recipe, given the input.
     */
    BlockState assembleBlock(BlockState input);

    @Override
    default ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return ItemStack.EMPTY;
    }
}