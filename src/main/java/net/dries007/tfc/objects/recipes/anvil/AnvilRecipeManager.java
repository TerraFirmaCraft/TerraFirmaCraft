/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes.anvil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    public static boolean hasRecipe(ItemStack input)
    {
        return get(input) != null;
    }

    public static void add(AnvilRecipe recipe)
    {
        if (AnvilRecipe.assertValid(recipe))
        {
            recipes.add(recipe.withSeed(++workingSeed));
        }
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
                add(new AnvilRecipe(input, output, metal.getTier(), rules));
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
                add(new AnvilRecipe(input, output, metal.getTier(), rules));
            }
        }
    }

    @Nullable
    public AnvilRecipe getByName(@Nullable String name)
    {
        return recipes.stream().filter(x -> x.getName().equals(name)).findFirst().orElse(null);
    }

    @Nullable
    public AnvilRecipe getPrevious(@Nullable AnvilRecipe recipe, ItemStack input)
    {
        List<AnvilRecipe> list = getAllMatching(input);
        if (list.size() == 0)
            return null;

        int idx = list.indexOf(recipe);
        if (idx == -1)
            return recipe;
        else if (idx == 0)
            return list.get(list.size() - 1);
        else
            return list.get(idx - 1);
    }

    @Nullable
    public AnvilRecipe getNext(@Nullable AnvilRecipe recipe, ItemStack input)
    {
        List<AnvilRecipe> list = getAllMatching(input);
        if (list.size() == 0)
            return null;

        int idx = list.indexOf(recipe);
        if (idx == -1)
            return recipe;
        else if (idx + 1 >= list.size())
            return list.get(0);
        else
            return list.get(idx + 1);
    }

    private List<AnvilRecipe> getAllMatching(ItemStack input)
    {
        return recipes.stream().filter(x -> x.matches(input)).collect(Collectors.toList());
    }

}
