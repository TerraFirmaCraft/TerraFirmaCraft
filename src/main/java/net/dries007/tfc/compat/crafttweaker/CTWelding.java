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
import net.dries007.tfc.api.recipes.WeldingRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.skills.SmithingSkill;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.terrafirmacraft.Welding")
@ZenRegister
public class CTWelding
{
    @ZenMethod
    public static void addRecipe(String registryName, crafttweaker.api.item.IIngredient input1, crafttweaker.api.item.IIngredient input2, IItemStack output, int minTier)
    {
        addRecipe(registryName, input1, input2, output, minTier, null);
    }

    @SuppressWarnings("unchecked")
    @ZenMethod
    public static void addRecipe(String registryName, crafttweaker.api.item.IIngredient input1, crafttweaker.api.item.IIngredient input2, IItemStack output, int minTier, String skillTypeName)
    {
        if (output == null || input1 == null || input2 == null)
            throw new IllegalArgumentException("Both inputs and output are not allowed to be empty");
        if (input1 instanceof ILiquidStack || input2 instanceof ILiquidStack)
            throw new IllegalArgumentException("There is a fluid where it's supposed to be an item!");
        IIngredient ingredient1 = CTHelper.getInternalIngredient(input1);
        IIngredient ingredient2 = CTHelper.getInternalIngredient(input2);
        Metal.Tier tier = Metal.Tier.valueOf(minTier);
        ItemStack outputStack = (ItemStack) output.getInternal();
        SmithingSkill.Type skillType = skillTypeName == null ? null : SmithingSkill.Type.valueOf(skillTypeName.toUpperCase());
        WeldingRecipe recipe = new WeldingRecipe(new ResourceLocation(registryName), ingredient1, ingredient2, outputStack, tier, skillType);
        CraftTweakerAPI.apply(new IAction()
        {
            @Override
            public void apply()
            {
                TFCRegistries.WELDING.register(recipe);
            }

            @Override
            public String describe()
            {
                //noinspection ConstantConditions
                return "Adding welding recipe " + recipe.getRegistryName().toString();
            }
        });
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output)
    {
        if (output == null) throw new IllegalArgumentException("Output not allowed to be empty");
        ItemStack item = (ItemStack) output.getInternal();
        List<WeldingRecipe> removeList = new ArrayList<>();
        TFCRegistries.WELDING.getValuesCollection()
            .stream()
            .filter(x -> x.getOutputs().get(0).isItemEqual(item))
            .forEach(removeList::add);
        for (WeldingRecipe rem : removeList)
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.WELDING;
                    modRegistry.remove(rem.getRegistryName());
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Removing welding recipe " + rem.getRegistryName().toString();
                }
            });
        }
    }

    @ZenMethod
    public static void removeRecipe(String registryName)
    {
        WeldingRecipe recipe = TFCRegistries.WELDING.getValue(new ResourceLocation(registryName));
        if (recipe != null)
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.WELDING;
                    modRegistry.remove(recipe.getRegistryName());
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Removing welding recipe " + recipe.getRegistryName().toString();
                }
            });
        }
    }
}
