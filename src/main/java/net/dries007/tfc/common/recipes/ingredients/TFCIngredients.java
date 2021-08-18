/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import net.dries007.tfc.util.Helpers;

public final class TFCIngredients
{
    public static final IIngredientSerializer<NotRottenIngredient> NOT_ROTTEN = register("not_rotten", new NotRottenIngredient.Serializer());

    private static <T extends Ingredient> IIngredientSerializer<T> register(String name, IIngredientSerializer<T> serializer)
    {
        CraftingHelper.register(Helpers.identifier(name), serializer);
        return serializer;
    }
}
