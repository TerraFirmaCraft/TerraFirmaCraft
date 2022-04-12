/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class QuernRecipe extends SimpleItemRecipe
{
    public static final IndirectHashCollection<Item, QuernRecipe> CACHE = IndirectHashCollection.createForRecipe(QuernRecipe::getValidItems, TFCRecipeTypes.QUERN);

    @Nullable
    public static QuernRecipe getRecipe(Level world, ItemStackInventory wrapper)
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

    public QuernRecipe(ResourceLocation id, Ingredient ingredient, ItemStackProvider result)
    {
        super(id, ingredient, result);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.QUERN.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.QUERN.get();
    }
}
