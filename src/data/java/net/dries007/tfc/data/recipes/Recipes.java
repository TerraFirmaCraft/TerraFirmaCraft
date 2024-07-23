package net.dries007.tfc.data.recipes;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;

import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.data.providers.BuiltinItemHeat;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.data.FluidHeat;

public interface Recipes extends Accessors
{
    List<BuiltinItemHeat.WithMelting> withMelting();
    HolderLookup.Provider lookup();

    default float temperatureOf(Metal metal)
    {
        return FluidHeat.MANAGER.getOrThrow(Helpers.identifier(metal.getSerializedName())).meltTemperature();
    }

    default void add(Recipe<?> recipe)
    {
        add(nameOf(recipe.getResultItem(lookup()).getItem()), recipe);
    }

    default void add(String name, Recipe<?> recipe)
    {
        add(Objects.requireNonNull(BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType()), "No recipe type").getPath(), name, recipe);
    }

    void add(String prefix, String name, Recipe<?> recipe);

    void remove(String... names);

    void replace(String name, Recipe<?> recipe);
}
