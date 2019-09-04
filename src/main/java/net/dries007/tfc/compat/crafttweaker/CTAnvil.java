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
import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.forge.ForgeRule;
import net.dries007.tfc.util.skills.SmithingSkill;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.terrafirmacraft.Anvil")
@ZenRegister
public class CTAnvil
{
    @SuppressWarnings("unchecked")
    @ZenMethod
    public static void addRecipe(String registryName, crafttweaker.api.item.IIngredient input, IItemStack output, int minTier, String skillTypeName, String... rules)
    {
        if (output == null || input == null)
            throw new IllegalArgumentException("Input and output are not allowed to be empty");
        if (input instanceof ILiquidStack)
            throw new IllegalArgumentException("There is a fluid where it's supposed to be an item!");
        IIngredient ingredient = CTHelper.getInternalIngredient(input);
        if (rules.length == 0 || rules.length > 3)
            throw new IllegalArgumentException("Rules length must be within the closed interval [1, 3]");
        ForgeRule[] forgeRules = new ForgeRule[rules.length];
        for (int i = 0; i < rules.length; i++)
        {
            String str = rules[i];
            ForgeRule rl = ForgeRule.valueOf(str.toUpperCase());
            forgeRules[i] = rl;
        }
        Metal.Tier tier = Metal.Tier.valueOf(minTier);
        ItemStack outputItem = (ItemStack) output.getInternal();
        SmithingSkill.Type skillType = null;
        if (skillTypeName != null)
        {
            skillType = SmithingSkill.Type.valueOf(skillTypeName.toUpperCase());
        }
        AnvilRecipe recipe = new AnvilRecipe(new ResourceLocation(registryName), ingredient, outputItem, tier, skillType, forgeRules);
        CraftTweakerAPI.apply(new IAction()
        {
            @Override
            public void apply()
            {
                TFCRegistries.ANVIL.register(recipe);
            }

            @Override
            public String describe()
            {
                return "Adding anvil recipe for " + outputItem.getDisplayName();
            }
        });
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output)
    {
        if (output == null) throw new IllegalArgumentException("Output not allowed to be empty");
        ItemStack item = (ItemStack) output.getInternal();
        List<AnvilRecipe> removeList = new ArrayList<>();
        TFCRegistries.ANVIL.getValuesCollection()
            .stream()
            .filter(x -> x.getOutputs().get(0).isItemEqual(item))
            .forEach(removeList::add);
        for (AnvilRecipe rem : removeList)
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.ANVIL;
                    modRegistry.remove(rem.getRegistryName());
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Removing anvil recipe " + rem.getRegistryName().toString();
                }
            });
        }
    }

    @ZenMethod
    public static void removeRecipe(String registryName)
    {
        AnvilRecipe recipe = TFCRegistries.ANVIL.getValue(new ResourceLocation(registryName));
        if (recipe != null)
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.ANVIL;
                    modRegistry.remove(recipe.getRegistryName());
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Removing anvil recipe " + recipe.getRegistryName().toString();
                }
            });
        }
    }
}
