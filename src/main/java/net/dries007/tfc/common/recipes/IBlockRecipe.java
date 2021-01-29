/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A simple {@link IRecipe} extension for {@link BlockRecipeWrapper}
 */
public interface IBlockRecipe extends ISimpleRecipe<BlockRecipeWrapper>
{
    @Override
    default boolean matches(BlockRecipeWrapper inv, World worldIn)
    {
        return matches(worldIn, inv.getPos(), inv.getState());
    }

    @Override
    default ItemStack getResultItem()
    {
        return new ItemStack(getBlockRecipeOutput());
    }

    @Override
    default ItemStack assemble(BlockRecipeWrapper inv)
    {
        return new ItemStack(getBlockCraftingResult(inv).getBlock());
    }

    /**
     * Specific parameter version of {@link net.minecraft.item.crafting.IRecipe#matches(IInventory, World)} for block recipes
     */
    default boolean matches(World worldIn, BlockPos pos, BlockState state)
    {
        return false;
    }

    /**
     * Specific parameter version of {@link net.minecraft.item.crafting.IRecipe#getCraftingResult(IInventory)} for block recipes.
     */
    default BlockState getBlockCraftingResult(BlockRecipeWrapper wrapper)
    {
        return getBlockRecipeOutput().defaultBlockState();
    }

    /**
     * Specific parameter version of {@link IRecipe#getRecipeOutput()} for block recipes.
     */
    default Block getBlockRecipeOutput()
    {
        return Blocks.AIR;
    }
}