/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes.heat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.util.IMetalObject;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.util.agriculture.Food;

public final class HeatRecipeManager
{
	private static final List<HeatRecipe> recipes = new ArrayList<>();

	public static void postInit()
	{
		recipes.clear();

		recipes.add(new HeatRecipe(new ItemStack(BlocksTFC.TORCH, 2), "stickWood"));
		recipes.add(new HeatRecipe(new ItemStack(Blocks.GLASS), "sand"));

		// Bread
		recipes.add(new HeatRecipe(new ItemStack(ItemFoodTFC.get(Food.BARLEY_BREAD)), new ItemStack(ItemFoodTFC.get(Food.BARLEY_DOUGH))));
		recipes.add(new HeatRecipe(new ItemStack(ItemFoodTFC.get(Food.CORNBREAD)), new ItemStack(ItemFoodTFC.get(Food.CORNMEAL_DOUGH))));
		recipes.add(new HeatRecipe(new ItemStack(ItemFoodTFC.get(Food.OAT_BREAD)), new ItemStack(ItemFoodTFC.get(Food.OAT_DOUGH))));
		recipes.add(new HeatRecipe(new ItemStack(ItemFoodTFC.get(Food.RICE_BREAD)), new ItemStack(ItemFoodTFC.get(Food.RICE_DOUGH))));
		recipes.add(new HeatRecipe(new ItemStack(ItemFoodTFC.get(Food.RYE_BREAD)), new ItemStack(ItemFoodTFC.get(Food.RYE_DOUGH))));
		recipes.add(new HeatRecipe(new ItemStack(ItemFoodTFC.get(Food.WHEAT_BREAD)), new ItemStack(ItemFoodTFC.get(Food.WHEAT_DOUGH))));

		// Meat
		recipes.add(new HeatRecipe(new ItemStack(ItemFoodTFC.get(Food.COOKED_BEEF)), new ItemStack(ItemFoodTFC.get(Food.BEEF))));
		recipes.add(new HeatRecipe(new ItemStack(ItemFoodTFC.get(Food.COOKED_PORK)), new ItemStack(ItemFoodTFC.get(Food.PORK))));
		recipes.add(new HeatRecipe(new ItemStack(ItemFoodTFC.get(Food.COOKED_CHICKEN)), new ItemStack(ItemFoodTFC.get(Food.CHICKEN))));
		recipes.add(new HeatRecipe(new ItemStack(ItemFoodTFC.get(Food.COOKED_MUTTON)), new ItemStack(ItemFoodTFC.get(Food.MUTTON))));
		recipes.add(new HeatRecipe(new ItemStack(ItemFoodTFC.get(Food.COOKED_FISH)), new ItemStack(ItemFoodTFC.get(Food.FISH))));
		recipes.add(new HeatRecipe(new ItemStack(ItemFoodTFC.get(Food.COOKED_BEAR)), new ItemStack(ItemFoodTFC.get(Food.BEAR))));
		recipes.add(new HeatRecipe(new ItemStack(ItemFoodTFC.get(Food.COOKED_CALAMARI)), new ItemStack(ItemFoodTFC.get(Food.CALAMARI))));
		recipes.add(new HeatRecipe(new ItemStack(ItemFoodTFC.get(Food.COOKED_HORSE_MEAT)), new ItemStack(ItemFoodTFC.get(Food.HORSE_MEAT))));
		recipes.add(new HeatRecipe(new ItemStack(ItemFoodTFC.get(Food.COOKED_PHEASANT)), new ItemStack(ItemFoodTFC.get(Food.PHEASANT))));
		recipes.add(new HeatRecipe(new ItemStack(ItemFoodTFC.get(Food.COOKED_VENISON)), new ItemStack(ItemFoodTFC.get(Food.VENISON))));

		// todo: craft tweaker supported recipes (See NTP for a nice-ish way to do it)
	}

	public static void add(HeatRecipe recipe)
	{
		recipes.add(recipe);
	}

	@Nullable
	public static HeatRecipe get(ItemStack stack)
	{
		Optional<HeatRecipe> recipe = recipes.stream().filter(x -> x.matchesInput(stack)).findFirst();
		if (recipe.isPresent())
		{
			return recipe.get();
		}
		// Default Recipes
		// These are for default behaviors (IMetalObject = it can melt into liquid
		// metal)
		if (stack.getItem() instanceof IMetalObject)
		{
			return new HeatRecipe(((IMetalObject) stack.getItem()), stack);
		}
		return null;
	}
}
