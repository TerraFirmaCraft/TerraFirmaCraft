/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.IShapedRecipe;

public abstract class NoRemainderCraftingRecipe<R extends Recipe<CraftingContainer>> extends DelegateRecipe<R, CraftingContainer> implements CraftingRecipe
{
    protected NoRemainderCraftingRecipe(ResourceLocation id, R recipe)
    {
        super(id, recipe);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv)
    {
        return NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public CraftingBookCategory category()
    {
        return CraftingBookCategory.MISC;
    }

    public static class Shapeless extends NoRemainderCraftingRecipe<Recipe<CraftingContainer>>
    {
        public Shapeless(ResourceLocation id, Recipe<CraftingContainer> recipe)
        {
            super(id, recipe);
        }

        @Override
        public RecipeSerializer<?> getSerializer()
        {
            return TFCRecipeSerializers.NO_REMAINDER_SHAPELESS_CRAFTING.get();
        }
    }

    public static class Shaped extends NoRemainderCraftingRecipe<IShapedRecipe<CraftingContainer>> implements IRecipeDelegate.Shaped<CraftingContainer>
    {
        public Shaped(ResourceLocation id, IShapedRecipe<CraftingContainer> recipe)
        {
            super(id, recipe);
        }

        @Override
        public RecipeSerializer<?> getSerializer()
        {
            return TFCRecipeSerializers.NO_REMAINDER_SHAPED_CRAFTING.get();
        }
    }
}
