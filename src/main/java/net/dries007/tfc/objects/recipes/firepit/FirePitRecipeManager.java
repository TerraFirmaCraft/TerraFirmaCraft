/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.recipes.firepit;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.objects.blocks.BlocksTFC;

public class FirePitRecipeManager
{
    private static List<FirePitRecipe> recipes = new ArrayList<>();

    @SuppressWarnings("ConstantConditions")
    public static void postInit()
    {
        recipes.clear();

        // todo: recipes here
        recipes.add(new FirePitRecipe(new ItemStack(BlocksTFC.TORCH, 2), "stickWood"));
        recipes.add(new FirePitRecipe(new ItemStack(Blocks.GLASS), "sand"));

        // todo: craft tweaker supported recipes (See NTP for a nice-ish way to do it)
    }

    public static boolean add(FirePitRecipe recipe)
    {
        for (FirePitRecipe r : recipes)
            if (r.matchesInput(recipe))
                return false;

        recipes.add(recipe);
        return true;
    }

    @Nullable
    public static FirePitRecipe get(ItemStack stack)
    {
        return recipes.stream().filter(x -> x.matchesInput(stack)).findFirst().orElse(null);
    }
}
