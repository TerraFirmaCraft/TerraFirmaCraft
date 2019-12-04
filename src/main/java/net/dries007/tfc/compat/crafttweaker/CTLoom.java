/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.crafttweaker;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryModifiable;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.dries007.tfc.api.recipes.LoomRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.terrafirmacraft.Loom")
@ZenRegister
public class CTLoom
{
    @SuppressWarnings("unchecked")
    @ZenMethod
    public static void addRecipe(String registryName, crafttweaker.api.item.IIngredient input, IItemStack output, int steps, String loomTexture)
    {
        if (output == null || input == null)
            throw new IllegalArgumentException("Input and output are not allowed to be empty");
        if (input instanceof ILiquidStack)
            throw new IllegalArgumentException("There is a fluid where it's supposed to be an item!");
        IIngredient ingredient = CTHelper.getInternalIngredient(input);
        ItemStack outputStack = (ItemStack) output.getInternal();
        LoomRecipe recipe = new LoomRecipe(new ResourceLocation(registryName), ingredient, outputStack, steps, new ResourceLocation(loomTexture));
        CraftTweakerAPI.apply(new IAction()
        {
            @Override
            public void apply()
            {
                TFCRegistries.LOOM.register(recipe);
            }

            @Override
            public String describe()
            {
                //noinspection ConstantConditions
                return "Adding loom recipe " + recipe.getRegistryName().toString();
            }
        });
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output)
    {
        if (output == null) throw new IllegalArgumentException("Output not allowed to be empty");
        ItemStack item = (ItemStack) output.getInternal();
        List<LoomRecipe> removeList = new ArrayList<>();
        TFCRegistries.LOOM.getValuesCollection()
            .stream()
            .filter(x -> x.getOutputs().get(0).isItemEqual(item))
            .forEach(removeList::add);
        for (LoomRecipe rem : removeList)
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.LOOM;
                    modRegistry.remove(rem.getRegistryName());
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Removing loom recipe " + rem.getRegistryName().toString();
                }
            });
        }
    }

    @ZenMethod
    public static void removeRecipe(String registryName)
    {
        LoomRecipe recipe = TFCRegistries.LOOM.getValue(new ResourceLocation(registryName));
        if (recipe != null)
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.LOOM;
                    modRegistry.remove(recipe.getRegistryName());
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Removing loom recipe " + recipe.getRegistryName().toString();
                }
            });
        }
    }
}
