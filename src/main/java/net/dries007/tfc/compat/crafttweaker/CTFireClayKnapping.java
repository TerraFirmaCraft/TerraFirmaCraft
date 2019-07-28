/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.crafttweaker;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryModifiable;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.dries007.tfc.api.recipes.KnappingRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.terrafirmacraft.FireClayKnapping")
@ZenRegister
public class CTFireClayKnapping
{
    @ZenMethod
    public static void addRecipe(String registryName, IItemStack output, String... pattern)
    {
        if (output == null || pattern.length == 0)
            throw new IllegalArgumentException("Output item must be non-null and at least one pattern must be supplied");
        ItemStack outputStack = (ItemStack) output.getInternal();
        KnappingRecipe recipe = new KnappingRecipe.Simple(KnappingRecipe.Type.FIRE_CLAY, true, outputStack, pattern).setRegistryName(registryName);
        CraftTweakerAPI.apply(new IAction()
        {
            @Override
            public void apply()
            {
                TFCRegistries.KNAPPING.register(recipe);
            }

            @Override
            public String describe()
            {
                return "Adding fire clay knapping recipe for " + recipe.getOutput(ItemStack.EMPTY).getDisplayName();
            }
        });
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output)
    {
        if (output == null) throw new IllegalArgumentException("Output not allowed to be empty");
        ItemStack item = (ItemStack) output.getInternal();
        List<KnappingRecipe> removeList = new ArrayList<>();
        TFCRegistries.KNAPPING.getValuesCollection()
            .stream()
            .filter(x -> x.getType() == KnappingRecipe.Type.FIRE_CLAY && x.getOutput(ItemStack.EMPTY).isItemEqual(item))
            .forEach(removeList::add);
        for (KnappingRecipe rem : removeList)
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.KNAPPING;
                    modRegistry.remove(rem.getRegistryName());
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Removing fire clay knapping recipe " + rem.getRegistryName().toString();
                }
            });
        }
    }
}
