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
public class Loom
{
    @SuppressWarnings("unchecked")
    @ZenMethod
    public static void addRecipe(IItemStack output, crafttweaker.api.item.IIngredient input, String loomTexture)
    {
        if (output == null || input == null)
            throw new IllegalArgumentException("Input and output are not allowed to be empty");
        if (input instanceof ILiquidStack)
            throw new IllegalArgumentException("There is a fluid where it's supposed to be an item!");
        IIngredient ingredient = CTHelper.getInternalIngredient(input);
        ItemStack outputStack = (ItemStack) output.getInternal();
        LoomRecipe recipe = new LoomRecipe(new ResourceLocation("crafttweaker", outputStack.getTranslationKey()), ingredient, ingredient.getAmount(), outputStack, ingredient.getAmount(), new ResourceLocation(loomTexture));
        CraftTweakerAPI.apply(new Add(recipe));
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output)
    {
        if (output == null) throw new IllegalArgumentException("Output not allowed to be empty");
        ItemStack item = (ItemStack) output.getInternal();
        List<Remove> removeList = new ArrayList<>();
        TFCRegistries.LOOM.getValuesCollection()
            .stream()
            .filter(x -> x.getOutputs().get(0).isItemEqual(item))
            .forEach(x -> removeList.add(new Remove(x.getRegistryName())));
        for (Remove rem : removeList)
        {
            CraftTweakerAPI.apply(rem);
        }
    }

    private static class Add implements IAction
    {
        private final LoomRecipe recipe;

        Add(LoomRecipe recipe)
        {
            this.recipe = recipe;
        }

        @Override
        public void apply()
        {
            TFCRegistries.LOOM.register(recipe);
        }

        @Override
        public String describe()
        {
            return "Adding loom recipe for " + recipe.getOutputs().get(0).getDisplayName();
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
            IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.LOOM;
            modRegistry.remove(location);
        }

        @Override
        public String describe()
        {
            return "Removing loom recipe " + location.toString();
        }
    }
}
