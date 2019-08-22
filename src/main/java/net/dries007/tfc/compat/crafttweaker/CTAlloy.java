/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.crafttweaker;

import net.minecraftforge.registries.IForgeRegistryModifiable;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import net.dries007.tfc.api.recipes.AlloyRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.terrafirmacraft.Alloy")
@ZenRegister
public class CTAlloy
{
    @ZenMethod
    public static CTAlloyRecipeBuilder addAlloy(String metal)
    {
        //noinspection ConstantConditions
        Metal result = TFCRegistries.METALS.getValuesCollection().stream()
            .filter(x -> x.getRegistryName().getPath().equalsIgnoreCase(metal)).findFirst().orElse(null);
        if (result == null)
        {
            throw new IllegalArgumentException("Metal specified not found!");
        }
        AlloyRecipe recipe = TFCRegistries.ALLOYS.getValue(result.getRegistryName());
        if (recipe != null)
        {
            throw new IllegalStateException("Alloy already has a recipe!");
        }
        return new CTAlloyRecipeBuilder(result);
    }

    @ZenMethod
    public static void removeAlloy(String metal) //Since alloys can only have one recipe only, we remove by the metal registry name
    {
        //noinspection ConstantConditions
        Metal result = TFCRegistries.METALS.getValuesCollection().stream()
            .filter(x -> x.getRegistryName().getPath().equalsIgnoreCase(metal)).findFirst().orElse(null);
        if (result == null)
        {
            throw new IllegalArgumentException("Metal specified not found!");
        }
        AlloyRecipe recipe = TFCRegistries.ALLOYS.getValue(result.getRegistryName());
        if (recipe != null)
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.ALLOYS;
                    modRegistry.remove(recipe.getRegistryName());
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Removing alloy recipe " + recipe.getRegistryName().toString();
                }
            });
        }
    }

    @ZenClass("mods.terrafirmacraft.AlloyRecipeBuilder")
    public static class CTAlloyRecipeBuilder
    {
        private AlloyRecipe.Builder internal;

        public CTAlloyRecipeBuilder(Metal result)
        {
            this.internal = new AlloyRecipe.Builder(result);
        }

        @ZenMethod
        public CTAlloyRecipeBuilder addMetal(String metal, double min, double max)
        {
            //noinspection ConstantConditions
            Metal result = TFCRegistries.METALS.getValuesCollection().stream()
                .filter(x -> x.getRegistryName().getPath().equalsIgnoreCase(metal)).findFirst().orElse(null);
            if (result == null)
            {
                throw new IllegalArgumentException("Metal specified not found!");
            }
            this.internal = this.internal.add(result, min, max);
            return this;
        }

        @ZenMethod
        public void build()
        {
            AlloyRecipe recipe = internal.build();
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    TFCRegistries.ALLOYS.register(recipe);
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Adding alloy recipe for " + recipe.getResult().getRegistryName().getPath();
                }
            });
        }
    }
}
