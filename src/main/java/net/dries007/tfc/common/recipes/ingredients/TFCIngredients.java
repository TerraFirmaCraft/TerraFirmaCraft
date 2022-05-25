/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import net.dries007.tfc.util.Helpers;

public final class TFCIngredients
{
    public static void registerIngredientTypes()
    {
        register("not_rotten", NotRottenIngredient.Serializer.INSTANCE);
        register("has_trait", TraitIngredient.TraitSerializer.HAS_TRAIT);
        register("lacks_trait", TraitIngredient.TraitSerializer.LACKS_TRAIT);
        register("heatable", HeatableIngredient.Serializer.INSTANCE);
        register("not", NotIngredient.Serializer.INSTANCE);
        register("fluid_item", FluidItemIngredient.Serializer.INSTANCE);
    }

    private static <T extends Ingredient> void register(String name, IIngredientSerializer<T> serializer)
    {
        CraftingHelper.register(Helpers.identifier(name), serializer);
    }
}
