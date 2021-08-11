/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * A simple {@link IRecipe} extension for {@link BlockRecipeWrapper}
 */
public interface IBlockRecipe extends ISimpleRecipe<BlockRecipeWrapper>
{
    @Override
    default boolean matches(BlockRecipeWrapper inv, Level worldIn)
    {
        return matches(worldIn, inv.getPos(), inv.getState());
    }

    @Override
    default ItemStack getResultItem()
    {
        return new ItemStack(getBlockRecipeOutput());
    }

    @Override
    default ItemStack assemble(BlockRecipeWrapper inventory)
    {
        return new ItemStack(getBlockCraftingResult(inventory).getBlock());
    }

    /**
     * Specific parameter version of {@link net.minecraft.item.crafting.IRecipe#matches(IInventory, World)} for block recipes
     */
    default boolean matches(Level worldIn, BlockPos pos, BlockState state)
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