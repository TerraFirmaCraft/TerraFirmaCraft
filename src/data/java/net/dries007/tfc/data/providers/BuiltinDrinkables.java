/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.effect.TFCEffects;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.util.data.Drinkable;
import net.dries007.tfc.util.data.Drinkable.Effect;

public class BuiltinDrinkables extends DataManagerProvider<Drinkable> implements Accessors
{
    public BuiltinDrinkables(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(Drinkable.MANAGER, output, lookup);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add("water",
            FluidIngredient.of(Fluids.WATER, TFCFluids.RIVER_WATER.get()),
            false,
            FoodData.ofDrink(10, 0));
        add("salt_water",
            FluidIngredient.of(TFCFluids.SALT_WATER.getSource()),
            true,
            FoodData.ofDrink(-1, 0),
            new Effect(TFCEffects.THIRST.holder(), 600, 1, 0.25f));
        add("alcohol",
            FluidIngredient.tag(TFCTags.Fluids.ALCOHOLS),
            true,
            FoodData.ofDrink(5, 4000));
        add("milk",
            FluidIngredient.of(NeoForgeMod.MILK.get()),
            false,
            FoodData.ofDrink(10, 0).dairy(1f));
        add("vinegar",
            FluidIngredient.of(fluidOf(SimpleFluid.VINEGAR)),
            false,
            FoodData.ofDrink(5, 0),
            new Effect(TFCEffects.THIRST.holder(), 80, 1, 0.8f));
    }

    private void add(String name, FluidIngredient fluid, boolean mayDrinkWhenFull, FoodData food, Effect... effects)
    {
        add(name, new Drinkable(fluid, 0, mayDrinkWhenFull, food, List.of(effects)));
    }
}
