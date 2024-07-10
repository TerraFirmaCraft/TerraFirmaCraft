/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.recipes.IBlockRecipe;

/**
 * This is a recipe input that provides a single block state.
 */
public record BlockInput(BlockState state) implements RecipeInput
{
    @Override
    public ItemStack getItem(int index)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public int size()
    {
        return 1;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }
}