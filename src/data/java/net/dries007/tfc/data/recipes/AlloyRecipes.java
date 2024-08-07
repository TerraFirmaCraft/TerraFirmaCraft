/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.recipes;

import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;

import net.dries007.tfc.common.recipes.AlloyRecipe;
import net.dries007.tfc.util.AlloyRange;
import net.dries007.tfc.util.Metal;

public interface AlloyRecipes extends Recipes
{
    default void alloyRecipes()
    {
        alloy(Metal.BISMUTH_BRONZE,
            rangeOf(Metal.ZINC, 0.2, 0.3),
            rangeOf(Metal.COPPER, 0.5, 0.65),
            rangeOf(Metal.BISMUTH, 0.1, 0.2));
        alloy(Metal.BLACK_BRONZE,
            rangeOf(Metal.COPPER, 0.5, 0.7),
            rangeOf(Metal.SILVER, 0.1, 0.25),
            rangeOf(Metal.GOLD, 0.1, 0.25));
        alloy(Metal.BRONZE,
            rangeOf(Metal.COPPER, 0.88, 0.92),
            rangeOf(Metal.TIN, 0.08, 0.12));
        alloy(Metal.BRASS,
            rangeOf(Metal.COPPER, 0.88, 0.92),
            rangeOf(Metal.ZINC, 0.08, 0.12));
        alloy(Metal.ROSE_GOLD,
            rangeOf(Metal.COPPER, 0.15, 0.3),
            rangeOf(Metal.GOLD, 0.7, 0.85));
        alloy(Metal.STERLING_SILVER,
            rangeOf(Metal.COPPER, 0.2, 0.4),
            rangeOf(Metal.SILVER, 0.6, 0.8));
        alloy(Metal.WEAK_STEEL,
            rangeOf(Metal.STEEL, 0.5, 0.7),
            rangeOf(Metal.NICKEL, 0.15, 0.25),
            rangeOf(Metal.BLACK_BRONZE, 0.15, 0.25));
        alloy(Metal.WEAK_BLUE_STEEL,
            rangeOf(Metal.BLACK_STEEL, 0.5, 0.55),
            rangeOf(Metal.STEEL, 0.2, 0.25),
            rangeOf(Metal.BISMUTH_BRONZE, 0.1, 0.15),
            rangeOf(Metal.STERLING_SILVER, 0.1, 0.15));
        alloy(Metal.WEAK_RED_STEEL,
            rangeOf(Metal.BLACK_STEEL, 0.5, 0.55),
            rangeOf(Metal.STEEL, 0.2, 0.25),
            rangeOf(Metal.BRASS, 0.1, 0.15),
            rangeOf(Metal.ROSE_GOLD, 0.1, 0.15));
    }

    private AlloyRange rangeOf(Metal metal, double min, double max)
    {
        return new AlloyRange(fluidOf(metal), min, max);
    }

    private void alloy(Metal metal, AlloyRange... ranges)
    {
        final AlloyRecipe recipe = new AlloyRecipe(List.of(ranges), fluidOf(metal));
        add(BuiltInRegistries.FLUID.getKey(recipe.result()).getPath().split("/")[1], recipe);
    }
}
