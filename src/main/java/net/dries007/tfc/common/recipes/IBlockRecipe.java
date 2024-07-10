/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.recipes.inventory.BlockInput;

/**
 * A base implementation of {@link ISimpleRecipe} that takes a {@link BlockInput}
 */
public interface IBlockRecipe extends ISimpleRecipe<BlockInput>
{
    /**
     * @param input The input to this recipe
     * @return {@code true} if the input matches the recipe. Use over {@link #matches(BlockInput, Level)} as this is specific to the recipe
     */
    boolean matches(BlockState input);

    /**
     * @param input The input to this recipe
     * @return The output of this recipe, given the input. Use over {@link #assemble(BlockInput, HolderLookup.Provider)} as this is specific to the recipe.
     */
    BlockState assembleBlock(BlockState input);

    // Vanilla Overrides

    @Override
    default boolean matches(BlockInput input, @Nullable Level level)
    {
        return matches(input.state());
    }

    @Override
    default ItemStack assemble(BlockInput input, HolderLookup.Provider registryAccess)
    {
        return new ItemStack(assembleBlock(input.state()).getBlock());
    }

    @Override
    default ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return ItemStack.EMPTY;
    }
}