/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.recipes.inventory.BlockInventory;

/**
 * A simple {@link net.minecraft.world.item.crafting.Recipe} extension for {@link BlockInventory}
 */
public interface IBlockRecipe extends ISimpleRecipe<BlockInventory>
{
    @Override
    default boolean matches(BlockInventory inv, @Nullable Level level)
    {
        return matches(inv.getState());
    }

    @Override
    default ItemStack getResultItem()
    {
        return new ItemStack(getBlockRecipeOutput());
    }

    @Override
    default ItemStack assemble(BlockInventory inventory)
    {
        return new ItemStack(getBlockCraftingResult(inventory).getBlock());
    }

    /**
     * Specific parameter version of {@link net.minecraft.world.item.crafting.Recipe#matches(Container, Level)} for block recipes
     */
    default boolean matches(BlockState state)
    {
        return false;
    }

    /**
     * Specific parameter version of {@link Recipe#assemble(Container)} for block recipes.
     */
    default BlockState getBlockCraftingResult(BlockInventory inventory)
    {
        return getBlockCraftingResult(inventory.getState());
    }

    default BlockState getBlockCraftingResult(BlockState state)
    {
        return getBlockRecipeOutput().defaultBlockState();
    }

    /**
     * Specific parameter version of {@link Recipe#getResultItem()} for block recipes.
     */
    default Block getBlockRecipeOutput()
    {
        return Blocks.AIR;
    }
}