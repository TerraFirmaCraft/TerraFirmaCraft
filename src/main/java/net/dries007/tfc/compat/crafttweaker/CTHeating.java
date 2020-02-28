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
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.api.recipes.heat.HeatRecipeSimple;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.terrafirmacraft.Heating")
@ZenRegister
public class CTHeating
{
    @ZenMethod
    public static void addRecipe(String registryName, IItemStack input, IItemStack output, float transformTemp, float maxTemp)
    {
        if (input == null || output == null)
            throw new IllegalArgumentException("Input and output are not allowed to be empty!");
        ItemStack istack = ((ItemStack) input.getInternal());
        ItemStack ostack = ((ItemStack) output.getInternal());
        IItemHeat icap = istack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
        IItemHeat ocap = ostack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
        if (icap == null || ocap == null)
            throw new IllegalStateException("Input and output must have heating capabilities!");
        HeatRecipe recipe = new HeatRecipeSimple(IIngredient.of(istack), ostack, transformTemp, maxTemp, Metal.Tier.TIER_I).setRegistryName(registryName);
        CraftTweakerAPI.apply(new IAction()
        {
            @Override
            public void apply()
            {
                TFCRegistries.HEAT.register(recipe);
            }

            @Override
            public String describe()
            {
                //noinspection ConstantConditions
                return "Adding heating recipe " + recipe.getRegistryName().toString();
            }
        });
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output)
    {
        if (output == null) throw new IllegalArgumentException("Output not allowed to be empty");
        ItemStack item = (ItemStack) output.getInternal();
        List<HeatRecipe> removeList = new ArrayList<>();
        TFCRegistries.HEAT.getValuesCollection()
            .stream()
            .filter(x -> x instanceof HeatRecipeSimple)
            .filter(x -> x.getOutputs().get(0).isItemEqual(item))
            .forEach(removeList::add);
        for (HeatRecipe rem : removeList)
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.HEAT;
                    modRegistry.remove(rem.getRegistryName());
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Removing heating recipe " + rem.getRegistryName().toString();
                }
            });
        }
    }

    @ZenMethod
    public static void removeRecipe(String registryName)
    {
        HeatRecipe recipe = TFCRegistries.HEAT.getValue(new ResourceLocation(registryName));
        if (recipe instanceof HeatRecipeSimple)
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.HEAT;
                    modRegistry.remove(recipe.getRegistryName());
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Removing heating recipe " + recipe.getRegistryName().toString();
                }
            });
        }
    }
}
