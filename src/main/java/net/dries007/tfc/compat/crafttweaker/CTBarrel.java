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
import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.calendar.ICalendar;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.terrafirmacraft.Barrel")
@ZenRegister
public class CTBarrel
{
    @SuppressWarnings("unchecked")
    @ZenMethod
    public static void addRecipe(String registryName, crafttweaker.api.item.IIngredient itemInput, ILiquidStack fluidInput, IItemStack itemOutput, ILiquidStack fluidOutput, int hours)
    {
        if (itemOutput == null && fluidOutput == null)
            throw new IllegalArgumentException("At least one output must be supplied");
        if (fluidInput == null) throw new IllegalArgumentException("Fluid input must be non-null");
        if (itemInput instanceof ILiquidStack)
            throw new IllegalArgumentException("There is a fluid where it's supposed to be an item!");
        IIngredient itemIngredient = itemInput == null ? IIngredient.of(ItemStack.EMPTY) : CTHelper.getInternalIngredient(itemInput);
        IIngredient fluidIngredient = CTHelper.getInternalIngredient(fluidInput);
        ItemStack outputStack = itemOutput == null ? ItemStack.EMPTY : (ItemStack) itemOutput.getInternal();
        FluidStack outputFluid = fluidOutput == null ? null : (FluidStack) fluidOutput.getInternal();
        if (outputFluid != null) registryName += "_" + outputFluid.getUnlocalizedName();
        BarrelRecipe recipe = new BarrelRecipe(fluidIngredient, itemIngredient, outputFluid, outputStack, hours * ICalendar.TICKS_IN_HOUR).setRegistryName(registryName);
        CraftTweakerAPI.apply(new IAction()
        {
            @Override
            public void apply()
            {
                TFCRegistries.BARREL.register(recipe);
            }

            @Override
            public String describe()
            {
                //noinspection ConstantConditions
                return "Adding barrel recipe " + recipe.getRegistryName().toString();
            }
        });
    }

    @ZenMethod
    public static void addRecipe(String registryName, ILiquidStack fluidInput, IItemStack itemOutput, ILiquidStack fluidOutput, int hours)
    {
        addRecipe(registryName, null, fluidInput, itemOutput, fluidOutput, hours);
    }

    @ZenMethod
    public static void addRecipe(String registryName, ILiquidStack fluidInput, IItemStack itemOutput, int hours)
    {
        addRecipe(registryName, null, fluidInput, itemOutput, null, hours);
    }

    @ZenMethod
    public static void addRecipe(String registryName, ILiquidStack fluidInput, ILiquidStack fluidOutput, int hours)
    {
        addRecipe(registryName, null, fluidInput, null, fluidOutput, hours);
    }

    @ZenMethod
    public static void addRecipe(String registryName, crafttweaker.api.item.IIngredient itemInput, ILiquidStack fluidInput, IItemStack itemOutput, int hours)
    {
        addRecipe(registryName, itemInput, fluidInput, itemOutput, null, hours);
    }

    @ZenMethod
    public static void addRecipe(String registryName, crafttweaker.api.item.IIngredient itemInput, ILiquidStack fluidInput, ILiquidStack fluidOutput, int hours)
    {
        addRecipe(registryName, itemInput, fluidInput, null, fluidOutput, hours);
    }

    @ZenMethod
    public static void removeRecipe(ILiquidStack outputLiquid)
    {
        removeRecipe(null, outputLiquid);
    }

    @ZenMethod
    public static void removeRecipe(IItemStack outputItem)
    {
        removeRecipe(outputItem, null);
    }

    @ZenMethod
    public static void removeRecipe(IItemStack outputItem, ILiquidStack outputLiquid)
    {
        ItemStack item = outputItem != null ? (ItemStack) outputItem.getInternal() : ItemStack.EMPTY;
        FluidStack fluid = outputLiquid != null ? (FluidStack) outputLiquid.getInternal() : null;
        if (fluid == null && item == ItemStack.EMPTY)
            throw new IllegalArgumentException("At least one output must be supplied");
        List<BarrelRecipe> removeList = new ArrayList<>();
        TFCRegistries.BARREL.getValuesCollection()
            .stream()
            .filter(x -> (x.getOutputStack() == item || x.getOutputStack().isItemEqual(item)) && ((fluid == null && x.getOutputFluid() == null) || (fluid != null && fluid.isFluidEqual(x.getOutputFluid()))))
            .forEach(removeList::add);
        for (BarrelRecipe rem : removeList)
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.BARREL;
                    modRegistry.remove(rem.getRegistryName());
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Removing barrel recipe " + rem.getRegistryName().toString();
                }
            });
        }
    }

    @ZenMethod
    public static void removeRecipe(String registryName)
    {
        BarrelRecipe recipe = TFCRegistries.BARREL.getValue(new ResourceLocation(registryName));
        if (recipe != null)
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.BARREL;
                    modRegistry.remove(recipe.getRegistryName());
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Removing barrel recipe " + recipe.getRegistryName().toString();
                }
            });
        }
    }
}
