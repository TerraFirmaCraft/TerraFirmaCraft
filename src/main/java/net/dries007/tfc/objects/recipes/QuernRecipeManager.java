/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class QuernRecipeManager
{
    private static final QuernRecipeManager INSTANCE = new QuernRecipeManager();

    public static QuernRecipeManager getInstance()
    {
        return INSTANCE;
    }

    private final Map<Ingredient, ItemStack> grindingList;
    private final Map<ItemStack, Float> experienceList;

    private QuernRecipeManager()
    {
        grindingList = Maps.newHashMap();
        experienceList = Maps.newHashMap();
    }

    public QuernRecipe addGrindingRecipe(String group, Ingredient ingredient, ItemStack output, float experience)
    {
        if (getGrindingResult(ingredient) != ItemStack.EMPTY)
        {
            for (ItemStack stack : ingredient.getMatchingStacks())
                throw new JsonParseException("Grinding recipe has conflicting input: {" + stack + "} = {" + output + "}" + System.lineSeparator() +
                    "                                                                           {" + stack + "} = {" + getGrindingResult(ingredient) + "}");
        }
        this.grindingList.put(ingredient, output);
        this.experienceList.put(output, experience);

        return new QuernRecipe(group.isEmpty() ? null : new ResourceLocation(group), ingredient, output);
    }

    public boolean getIsValidGrindingIngredient(ItemStack input)
    {
        for (Map.Entry<Ingredient, ItemStack> entry : this.grindingList.entrySet())
        {
            for (ItemStack stack : entry.getKey().getMatchingStacks())
            {
                if (this.compareItemStacks(input, stack))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public ItemStack getGrindingResult(Ingredient ingredient)
    {
        for (Map.Entry<Ingredient, ItemStack> entry : this.grindingList.entrySet())
        {
            if (this.compareIngredients(ingredient, entry.getKey()))
            {
                return entry.getValue();
            }
        }

        return ItemStack.EMPTY;
    }

    public ItemStack getGrindingResult(ItemStack input)
    {
        for (Map.Entry<Ingredient, ItemStack> entry : this.grindingList.entrySet())
        {
            for (ItemStack stack : entry.getKey().getMatchingStacks())
            {
                if (this.compareItemStacks(input, stack))
                {
                    return entry.getValue();
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @SuppressWarnings("unused")
    public Map<Ingredient, ItemStack> getGrindingList()
    {
        return this.grindingList;
    }

    public float getGrindingExperience(ItemStack stack)
    {
        float ret = stack.getItem().getSmeltingExperience(stack);
        if (ret != -1) return ret;

        for (Map.Entry<ItemStack, Float> entry : this.experienceList.entrySet())
        {
            if (this.compareItemStacks(stack, entry.getKey()))
            {
                return entry.getValue();
            }
        }

        return 0.0F;
    }

    private boolean compareItemStacks(ItemStack stack1, ItemStack stack2)
    {
        return stack2.getItem() == stack1.getItem() && (stack2.getMetadata() == 32767 || stack2.getMetadata() == stack1.getMetadata()) && stack2.getItemDamage() == stack1.getItemDamage();
    }

    private boolean compareIngredients(Ingredient ingredient1, Ingredient ingredient2)
    {
        for (ItemStack stack1 : ingredient1.getMatchingStacks())
        {
            for (ItemStack stack2 : ingredient2.getMatchingStacks())
            {
                if (!compareItemStacks(stack1, stack2))
                {
                    return false;
                }
            }
        }
        return true;
    }
}
