/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import javax.annotation.Nullable;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.recipes.inventory.ItemStackRecipeWrapper;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class ScrapingRecipe extends SimpleItemRecipe
{
    public static final IndirectHashCollection<Item, ScrapingRecipe> CACHE = new IndirectHashCollection<>(ScrapingRecipe::getValidItems);

    @Nullable
    public static ScrapingRecipe getRecipe(Level world, ItemStackRecipeWrapper wrapper)
    {
        for (ScrapingRecipe recipe : CACHE.getAll(wrapper.getStack().getItem()))
        {
            if (recipe.matches(wrapper, world))
            {
                return recipe;
            }
        }
        return null;
    }

    public ScrapingRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result)
    {
        super(id, ingredient, result);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.SCRAPING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.SCRAPING;
    }
}
