/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.util.forge.ForgeRule;

public final class AnvilRecipeManager
{
    private static final List<AnvilRecipe> recipes = new ArrayList<>();
    private static long workingSeed = 0;

    @Nullable
    public static AnvilRecipe get(ItemStack input)
    {
        return recipes.stream().filter(x -> x.matches(input)).findFirst().orElse(null);
    }

    public static void add(AnvilRecipe recipe)
    {
        recipes.add(recipe.withSeed(++workingSeed));
    }

    public static void add(Metal.ItemType inputType, Metal.ItemType outputType, boolean onlyToolMetals, ForgeRule... rules)
    {
        // Helper method for adding all recipes that take ItemType -> ItemType (mostly used by TFC)
        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
        {
            if (onlyToolMetals && !metal.isToolMetal())
                continue;

            // Create a recipe for each metal / item type combination
            ItemStack input = new ItemStack(ItemMetal.get(metal, inputType));
            ItemStack output = new ItemStack(ItemMetal.get(metal, outputType));
            if (!input.isEmpty() && !output.isEmpty())
            {
                add(new AnvilRecipe(output.getItem().getRegistryName(), input, output, metal.getTier(), rules));
            }
        }
    }

    public static void add(Metal.ItemType inputType, Function<Metal, ItemStack> outputGenerator, boolean onlyToolMetals, ForgeRule... rules)
    {
        // Helper method for adding all recipes that take ItemType -> Another Item (mostly used by addons or non Item-Type items)
        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
        {
            if (onlyToolMetals && !metal.isToolMetal())
                continue;

            // Create a recipe for each metal / item type combination
            ItemStack input = new ItemStack(ItemMetal.get(metal, inputType));
            ItemStack output = outputGenerator.apply(metal);
            if (!input.isEmpty() && output != null && !output.isEmpty())
            {
                add(new AnvilRecipe(output.getItem().getRegistryName(), input, output, metal.getTier(), rules));
            }
        }
    }


}
