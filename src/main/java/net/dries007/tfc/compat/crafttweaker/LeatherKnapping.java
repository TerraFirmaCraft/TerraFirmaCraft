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
import net.dries007.tfc.api.recipes.KnappingRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.terrafirmacraft.LeatherKnapping")
@ZenRegister
public class LeatherKnapping
{
    @ZenMethod
    public static void addRecipe(IItemStack output, String... pattern)
    {
        if (output == null || pattern.length == 0)
            throw new IllegalArgumentException("Output item must be non-null and at least one pattern must be supplied");
        ItemStack outputStack = (ItemStack) output.getInternal();
        KnappingRecipe recipe = new KnappingRecipe.Simple(KnappingRecipe.Type.LEATHER, true, outputStack, pattern).setRegistryName("crafttweaker", outputStack.getTranslationKey());
        CraftTweakerAPI.apply(new Add(recipe));
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output)
    {
        if (output == null) throw new IllegalArgumentException("Output not allowed to be empty");
        ItemStack item = (ItemStack) output.getInternal();
        List<Remove> removeList = new ArrayList<>();
        TFCRegistries.KNAPPING.getValuesCollection()
            .stream()
            .filter(x -> x.getType() == KnappingRecipe.Type.LEATHER && x.getOutput(ItemStack.EMPTY).isItemEqual(item))
            .forEach(x -> removeList.add(new Remove(x.getRegistryName())));
        for (Remove rem : removeList)
        {
            CraftTweakerAPI.apply(rem);
        }
    }

    private static class Add implements IAction
    {
        private final KnappingRecipe recipe;

        Add(KnappingRecipe recipe)
        {
            this.recipe = recipe;
        }

        @Override
        public void apply()
        {
            TFCRegistries.KNAPPING.register(recipe);
        }

        @Override
        public String describe()
        {
            return "Adding leather knapping recipe for " + recipe.getOutput(ItemStack.EMPTY).getDisplayName();
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
            IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.KNAPPING;
            modRegistry.remove(location);
        }

        @Override
        public String describe()
        {
            return "Removing leather knapping recipe " + location.toString();
        }
    }
}
