/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.crafttweaker;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryModifiable;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.dries007.tfc.api.recipes.ChiselRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.terrafirmacraft.Chisel")
@ZenRegister
public class CTChisel
{

    @ZenMethod
    public static void addRecipe(String registryName, IItemStack input, IItemStack output)
    {
        if (output == null || input == null)
            throw new IllegalArgumentException("Input and output are not allowed to be empty");
        ItemStack ingredient = (ItemStack) input.getInternal();
        ItemStack outputStack = (ItemStack) output.getInternal();
        if (!(ingredient.getItem() instanceof ItemBlock) || !(outputStack.getItem() instanceof ItemBlock))
        {
            throw new IllegalArgumentException("Input and output must be blocks!");
        }
        Block inputBlock = ((ItemBlock) ingredient.getItem()).getBlock();
        Block outputBlock = ((ItemBlock) outputStack.getItem()).getBlock();
        ChiselRecipe recipe = new ChiselRecipe(inputBlock, outputBlock.getDefaultState()).setRegistryName(registryName);
        CraftTweakerAPI.apply(new IAction()
        {
            @Override
            public void apply()
            {
                TFCRegistries.CHISEL.register(recipe);
            }

            @Override
            public String describe()
            {
                //noinspection ConstantConditions
                return "Adding chisel recipe " + recipe.getRegistryName().toString();
            }
        });
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output)
    {
        if (output == null) throw new IllegalArgumentException("Output not allowed to be empty");
        ItemStack outputStack = (ItemStack) output.getInternal();
        if (!(outputStack.getItem() instanceof ItemBlock))
        {
            throw new IllegalArgumentException("Output must be a block!");
        }
        Block outputBlock = ((ItemBlock) outputStack.getItem()).getBlock();
        ItemStack item = (ItemStack) output.getInternal();
        List<ChiselRecipe> removeList = new ArrayList<>();
        TFCRegistries.CHISEL.getValuesCollection()
            .stream()
            .filter(x -> x.getOutputState().getBlock().equals(outputBlock))
            .forEach(removeList::add);
        for (ChiselRecipe rem : removeList)
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.CHISEL;
                    modRegistry.remove(rem.getRegistryName());
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Removing chisel recipe " + rem.getRegistryName().toString();
                }
            });
        }
    }

    @ZenMethod
    public static void removeRecipe(String registryName)
    {
        ChiselRecipe recipe = TFCRegistries.CHISEL.getValue(new ResourceLocation(registryName));
        if (recipe != null)
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.CHISEL;
                    modRegistry.remove(recipe.getRegistryName());
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Removing chisel recipe " + recipe.getRegistryName().toString();
                }
            });
        }
    }
}
