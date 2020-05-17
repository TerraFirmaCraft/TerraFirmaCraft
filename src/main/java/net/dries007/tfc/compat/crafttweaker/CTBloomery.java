/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.crafttweaker;

import net.minecraftforge.registries.IForgeRegistryModifiable;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import net.dries007.tfc.api.recipes.BloomeryRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.terrafirmacraft.Bloomery")
@ZenRegister
public class CTBloomery
{
    @SuppressWarnings("unchecked")
    @ZenMethod
    public static void addRecipe(String metal, crafttweaker.api.item.IIngredient additive)
    {
        //noinspection ConstantConditions
        Metal result = TFCRegistries.METALS.getValuesCollection().stream()
            .filter(x -> x.getRegistryName().getPath().equalsIgnoreCase(metal)).findFirst().orElse(null);
        if (result == null)
        {
            throw new IllegalArgumentException("Metal specified not found!");
        }
        if (BloomeryRecipe.get(result) != null)
        {
            throw new IllegalStateException("Recipe for that metal already exists!");
        }
        if (additive == null)
            throw new IllegalArgumentException("Additive is not allowed to be empty");
        if (additive instanceof ILiquidStack)
            throw new IllegalArgumentException("There is a fluid where it's supposed to be an item!");
        //noinspection rawtypes
        IIngredient ingredient = CTHelper.getInternalIngredient(additive);
        BloomeryRecipe recipe = new BloomeryRecipe(result, ingredient);
        CraftTweakerAPI.apply(new IAction()
        {
            @Override
            public void apply()
            {
                TFCRegistries.BLOOMERY.register(recipe);
            }

            @Override
            public String describe()
            {
                //noinspection ConstantConditions
                return "Adding bloomery recipe for " + result.getRegistryName().getPath();
            }
        });
    }

    @ZenMethod
    public static void removeRecipe(String metal)
    {
        //noinspection ConstantConditions
        Metal result = TFCRegistries.METALS.getValuesCollection().stream()
            .filter(x -> x.getRegistryName().getPath().equalsIgnoreCase(metal)).findFirst().orElse(null);
        if (result == null)
        {
            throw new IllegalArgumentException("Metal specified not found!");
        }
        BloomeryRecipe recipe = BloomeryRecipe.get(result);
        if (recipe != null)
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    IForgeRegistryModifiable<BloomeryRecipe> modRegistry = (IForgeRegistryModifiable<BloomeryRecipe>) TFCRegistries.BLOOMERY;
                    modRegistry.remove(recipe.getRegistryName());
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Removing bloomery recipe " + recipe.getRegistryName().toString();
                }
            });
        }
    }
}
