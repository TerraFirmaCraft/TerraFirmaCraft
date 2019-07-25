/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.crafttweaker;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryModifiable;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.dries007.tfc.api.recipes.KnappingRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.objects.items.rock.ItemRock;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.terrafirmacraft.StoneKnapping")
@ZenRegister
public class StoneKnapping
{
    @SuppressWarnings("ConstantConditions")
    @ZenMethod
    public static void addRecipe(IItemStack[] output, String[] rockCategories, String... pattern)
    {
        if (output == null) throw new IllegalArgumentException("Input not allowed to be empty");
        if (rockCategories == null || rockCategories.length != output.length)
            throw new IllegalArgumentException("You must specify a rock category for each output!");
        if (pattern.length == 0) throw new IllegalArgumentException("You must specify the pattern to craft!");
        Function<RockCategory, ItemStack> supplier;
        ResourceLocation registryName;
        if (rockCategories[0].equalsIgnoreCase("all"))
        {
            ItemStack outputStack = (ItemStack) output[0].getInternal();
            supplier = c -> outputStack;
            registryName = new ResourceLocation("crafttweaker", outputStack.getItem().getRegistryName().getPath());
        }
        else
        {
            Set<String> rockSet = Sets.newHashSet(rockCategories); //Fast querry
            Set<RockCategory> categories = TFCRegistries.ROCK_CATEGORIES.getValuesCollection()
                .stream().filter(r -> rockSet.contains(r.getRegistryName().getPath()))
                .collect(Collectors.toSet());
            if (rockSet.size() != categories.size())
                throw new IllegalArgumentException("Invalid rock category specified!");

            Map<RockCategory, ItemStack> outputMap = new HashMap<>();
            for (int i = 0; i < output.length; i++)
            {
                ItemStack outputStack = (ItemStack) output[i].getInternal();
                String rockCat = rockCategories[i];
                for (RockCategory cat : categories)
                {
                    if (rockCat.equals(cat.getRegistryName().getPath()))
                    {
                        outputMap.put(cat, outputStack);
                        break;
                    }
                }
            }
            ItemStack dummy = (ItemStack) output[0].getInternal();
            registryName = new ResourceLocation("crafttweaker", dummy.getItem().getRegistryName().getPath());
            supplier = c -> outputMap.get(c) != null ? outputMap.get(c) : ItemStack.EMPTY;
        }
        KnappingRecipe recipe = new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, supplier, pattern).setRegistryName(registryName);
        CraftTweakerAPI.apply(new Add(recipe));
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output)
    {
        if (output == null) throw new IllegalArgumentException("Output not allowed to be empty");
        Set<RockCategory> added = new HashSet<>();
        List<Rock> sampleRocks = new ArrayList<>(); //So we can use one per rock category
        TFCRegistries.ROCKS.getValuesCollection().forEach(rock -> {
            if (!added.contains(rock.getRockCategory()))
            {
                added.add(rock.getRockCategory());
                sampleRocks.add(rock);
            }
        });
        ItemStack item = (ItemStack) output.getInternal();
        List<Remove> removeList = new ArrayList<>();
        TFCRegistries.KNAPPING.getValuesCollection()
            .stream()
            .filter(x -> {
                if (x.getType() != KnappingRecipe.Type.STONE)
                {
                    return false;
                }
                else
                {
                    for (Rock rock : sampleRocks)
                    {
                        ItemStack outputStack = x.getOutput(new ItemStack(ItemRock.get(rock)));
                        if (outputStack != ItemStack.EMPTY && outputStack.isItemEqual(item)) return true;
                    }
                    return false;
                }
            })
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
            return "Adding stone knapping recipe for " + recipe.getOutput(new ItemStack(ItemRock.get(Rock.GRANITE))).getDisplayName();
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
            return "Removing stone knapping recipe " + location.toString();
        }
    }
}
