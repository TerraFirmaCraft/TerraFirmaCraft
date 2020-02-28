/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.crafttweaker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryModifiable;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeStone;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.items.rock.ItemRock;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@ZenClass("mods.terrafirmacraft.StoneKnapping")
@ZenRegister
public class CTStoneKnapping
{
    @SuppressWarnings("ConstantConditions")
    @ZenMethod
    public static void addRecipe(String registryName, IItemStack[] output, String[] rocks, String... pattern)
    {
        if (output == null)
        {
            throw new IllegalArgumentException("Input not allowed to be empty");
        }
        if (rocks == null || rocks.length != output.length)
        {
            throw new IllegalArgumentException("You must specify an output for each rock!");
        }
        if (pattern.length < 1 || pattern.length > 5)
        {
            throw new IllegalArgumentException("Pattern must be between 1 and 5 in length!");
        }
        Function<Rock, ItemStack> rockOutputMapper;
        if ("all".equalsIgnoreCase(rocks[0]))
        {
            ItemStack outputStack = (ItemStack) output[0].getInternal();
            rockOutputMapper = rockIn -> outputStack.copy();
        }
        else
        {
            Map<Rock, ItemStack> outputMap = new HashMap<>(output.length);
            for (int i = 0; i < output.length; i++)
            {
                Rock rock = TFCRegistries.ROCKS.getValue(new ResourceLocation(rocks[i]));
                if (rock == null)
                {
                    // Guess the mod id if it doesn't match
                    rock = TFCRegistries.ROCKS.getValue(new ResourceLocation(MOD_ID, rocks[i]));
                }
                if (rock == null)
                {
                    throw new IllegalArgumentException("Unknown rock '" + rock + "'.");
                }
                ItemStack outputStack = (ItemStack) output[i].getInternal();
                outputMap.put(rock, outputStack.copy());
            }
            rockOutputMapper = rockIn -> outputMap.getOrDefault(rockIn, ItemStack.EMPTY);
        }
        KnappingRecipe recipe = new KnappingRecipeStone(KnappingType.STONE, rockOutputMapper, pattern).setRegistryName(registryName);
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
                return "Registered stone knapping recipe with name " + registryName;
            }
        });
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output)
    {
        if (output == null)
        {
            throw new IllegalArgumentException("Output not allowed to be empty");
        }
        ItemStack item = (ItemStack) output.getInternal();
        List<KnappingRecipe> removeList = TFCRegistries.KNAPPING.getValuesCollection()
            .stream()
            .filter(x -> {
                if (x.getType() != KnappingType.STONE)
                {
                    return false;
                }
                else
                {
                    for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
                    {
                        ItemStack outputStack = x.getOutput(new ItemStack(ItemRock.get(rock)));
                        if (outputStack != ItemStack.EMPTY && outputStack.isItemEqual(item))
                        {
                            return true;
                        }
                    }
                    return false;
                }
            })
            .collect(Collectors.toList());
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
                    return "Removing stone knapping recipe " + rem.getRegistryName().toString();
                }
            });
        }
    }

    @ZenMethod
    public static void removeRecipe(String registryName)
    {
        KnappingRecipe recipe = TFCRegistries.KNAPPING.getValue(new ResourceLocation(registryName));
        if (recipe != null)
        {
            CraftTweakerAPI.apply(new IAction()
            {
                @Override
                public void apply()
                {
                    IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) TFCRegistries.KNAPPING;
                    modRegistry.remove(recipe.getRegistryName());
                }

                @Override
                public String describe()
                {
                    //noinspection ConstantConditions
                    return "Removing stone knapping recipe " + recipe.getRegistryName().toString();
                }
            });
        }
    }
}
