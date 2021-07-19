/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;

import net.dries007.tfc.common.recipes.knapping.KnappingRecipe;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCRecipeTypes
{
    public static final IRecipeType<CollapseRecipe> COLLAPSE = register("collapse");
    public static final IRecipeType<LandslideRecipe> LANDSLIDE = register("landslide");
    public static final IRecipeType<HeatingRecipe> HEATING = register("heating");
    public static final IRecipeType<QuernRecipe> QUERN = register("quern");
    public static final IRecipeType<PotRecipe> POT = register("pot");
    public static final IRecipeType<KnappingRecipe> CLAY_KNAPPING = register("clay_knapping");
    public static final IRecipeType<KnappingRecipe> FIRE_CLAY_KNAPPING = register("fire_clay_knapping");
    public static final IRecipeType<KnappingRecipe> LEATHER_KNAPPING = register("leather_knapping");

    private static <R extends IRecipe<?>> IRecipeType<R> register(String name)
    {
        return IRecipeType.register(MOD_ID + ":" + name);
    }
}