/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.extensions.IForgeRecipeSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * This is simply a convenience class because that extends clause is rather bothersome
 */
public abstract class RecipeSerializerImpl<R extends Recipe<?>> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<R>, IForgeRecipeSerializer<R>
{
}