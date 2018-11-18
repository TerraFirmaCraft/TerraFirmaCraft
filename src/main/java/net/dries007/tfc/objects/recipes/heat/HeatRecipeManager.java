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

public final class HeatRecipeManager
{
    private static final List<HeatRecipe> recipes = new ArrayList<>();

    @SuppressWarnings("ConstantConditions")
    public static void postInit()
    {
        recipes.clear();

        recipes.add(new HeatRecipe(new ItemStack(BlocksTFC.TORCH, 2), "stickWood"));
        recipes.add(new HeatRecipe(new ItemStack(Blocks.GLASS), "sand"));

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
        // These are for default behaviors (IMetalObject = it can melt)
        if (stack.getItem() instanceof IMetalObject)
        {
            return new HeatRecipe(((IMetalObject) stack.getItem()), stack);
        }
        return null;
    }
}
