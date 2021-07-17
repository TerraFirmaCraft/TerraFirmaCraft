/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.dries007.tfc.util.collections.IndirectHashCollection;

public class QuernRecipe extends SimpleItemRecipe
{
    public static final IndirectHashCollection<Item, QuernRecipe> CACHE = new IndirectHashCollection<>(QuernRecipe::getValidItems);

    @Nullable
    public static QuernRecipe getRecipe(World world, ItemStackRecipeWrapper wrapper)
    {
        for (QuernRecipe recipe : CACHE.getAll(wrapper.getStack().getItem()))
        {
            if (recipe.matches(wrapper, world))
            {
                return recipe;
            }
        }
        return null;
    }

    public QuernRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result)
    {
        super(id, ingredient, result);
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.QUERN.get();
    }

    @Override
    public IRecipeType<?> getType()
    {
        return TFCRecipeTypes.QUERN;
    }
}
