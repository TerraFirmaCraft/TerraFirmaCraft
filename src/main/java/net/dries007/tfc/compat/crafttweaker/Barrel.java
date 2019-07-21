/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.crafttweaker;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryModifiable;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.dries007.tfc.api.recipes.BarrelRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.calendar.ICalendar;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.terrafirmacraft.Barrel")
@ZenRegister
public class Barrel
{
    @SuppressWarnings("unchecked")
    @ZenMethod
    public static void addRecipe(IItemStack itemOutput, ILiquidStack fluidOutput, crafttweaker.api.item.IIngredient itemInput, ILiquidStack fluidInput, int hours)
    {
        if (itemOutput == null && fluidOutput == null)
            throw new IllegalArgumentException("At least one output must be supplied");
        if (fluidInput == null) throw new IllegalArgumentException("Fluid input must be non-null");
        if (itemInput instanceof ILiquidStack)
            throw new IllegalArgumentException("There is a fluid where it's supposed to be an item!");
        IIngredient itemIngredient = CTHelper.getInternalIngredient(itemInput);
        IIngredient fluidIngredient = CTHelper.getInternalIngredient(fluidInput);
        ItemStack outputStack = itemOutput == null ? ItemStack.EMPTY : (ItemStack) itemOutput.getInternal();
        FluidStack outputFluid = fluidOutput == null ? null : (FluidStack) fluidOutput.getInternal();
        String registryName = outputStack != ItemStack.EMPTY ? outputStack.getTranslationKey() : "empty";
        if (outputFluid != null) registryName += "_" + outputFluid.getUnlocalizedName();
        BarrelRecipe recipe = new BarrelRecipe(fluidIngredient, itemIngredient, outputFluid, outputStack, hours * ICalendar.TICKS_IN_HOUR).setRegistryName("crafttweaker", registryName);
        CraftTweakerAPI.apply(new Add(recipe));
    }

    @ZenMethod
    public static void removeRecipe(IItemStack outputItem, ILiquidStack outputLiquid)
    {
        ItemStack item = outputItem != null ? (ItemStack) outputItem.getInternal() : ItemStack.EMPTY;
        FluidStack fluid = outputLiquid != null ? (FluidStack) outputLiquid.getInternal() : null;
        if (fluid == null && item == ItemStack.EMPTY)
            throw new IllegalArgumentException("At least one output must be supplied");
        List<Remove> removeList = new ArrayList<>();
        TFCRegistries.BARREL.getValuesCollection()
            .stream()
            .filter(x -> (x.getOutputStack() == item || x.getOutputStack().isItemEqual(item)) && ((fluid == null && x.getOutputFluid() == null) || (fluid != null && fluid.isFluidEqual(x.getOutputFluid()))))
            .forEach(x -> removeList.add(new Remove(x.getRegistryName())));
        for (Remove rem : removeList)
        {
            CraftTweakerAPI.apply(rem);
        }
    }

    private static class Add implements IAction
    {
        private final BarrelRecipe recipe;

        Add(BarrelRecipe recipe)
        {
            this.recipe = recipe;
        }

        @Override
        public void apply()
        {
            TFCRegistries.BARREL.register(recipe);
        }

        @Override
        public String describe()
        {
            return "Adding barrel recipe for " + recipe.getResultName();
        }
    }

    private static class Remove implements IAction
    {
        private final ResourceLocation location;

        Remove(ResourceLocation location)
        {
            this.location = location;
        }

        @Override
        public void apply()
        {
            IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.BARREL;
            modRegistry.remove(location);
        }

        @Override
        public String describe()
        {
            return "Removing barrel recipe " + location.toString();
        }
    }
}
