/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.objects.items.food.ItemSandwich;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.CalendarTFC;

@SuppressWarnings("unused")
public class SandwichRecipe extends ShapedDamageRecipe
{
    public SandwichRecipe(ResourceLocation group, CraftingHelper.ShapedPrimer input, @Nonnull ItemStack result, int damage)
    {
        super(group, input, result, damage);
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv)
    {
        ItemStack output = super.getCraftingResult(inv);
        IFood food = output.getCapability(CapabilityFood.CAPABILITY, null);
        if (food instanceof ItemSandwich.SandwichHandler)
        {
            ItemSandwich.SandwichHandler sandwich = (ItemSandwich.SandwichHandler) food;
            List<FoodData> breads = new ArrayList<>();
            List<FoodData> ingredients = new ArrayList<>();
            getBreadsAndIngredients(inv, breads, ingredients);

            if (breads.size() != 2 || ingredients.size() < 1)
            {
                // Something weird happened
                return ItemStack.EMPTY;
            }

            sandwich.initCreationFoods(breads.get(0), breads.get(1), ingredients);
            sandwich.setCreationDate(CalendarTFC.PLAYER_TIME.getTicks()); // Meals get decay reset as they have on average, high decay modifiers. Also it's too much of a pain to re-calculate a remaining decay fraction average
        }
        return output;
    }

    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
    {
        if (super.matches(inv, world))
        {
            List<FoodData> breads = new ArrayList<>();
            List<FoodData> ingredients = new ArrayList<>();
            getBreadsAndIngredients(inv, breads, ingredients);
            return breads.size() == 2 && ingredients.size() > 0;
        }
        return false;
    }

    private void getBreadsAndIngredients(InventoryCrafting inv, List<FoodData> breads, List<FoodData> ingredients)
    {
        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack ingredientStack = inv.getStackInSlot(i);
            IFood ingredientCap = ingredientStack.getCapability(CapabilityFood.CAPABILITY, null);
            if (ingredientCap != null)
            {
                if (ingredientCap.isRotten())
                {
                    // Found a rotten ingredient, aborting
                    breads.clear();
                    ingredients.clear();
                    return;
                }
                if (OreDictionaryHelper.doesStackMatchOre(ingredientStack, "categoryBread"))
                {
                    // Found a bread item
                    breads.add(ingredientCap.getData());
                }
                else
                {
                    ingredients.add(ingredientCap.getData());
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Factory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(final JsonContext context, final JsonObject json)
        {
            String group = JsonUtils.getString(json, "group", "");

            CraftingHelper.ShapedPrimer primer = RecipeUtils.parsePhaped(context, json);

            ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
            final int damage;
            if (JsonUtils.hasField(json, "damage"))
                damage = JsonUtils.getInt(json, "damage");
            else damage = 1;
            return new SandwichRecipe(group.isEmpty() ? null : new ResourceLocation(group), primer, result, damage);
        }
    }
}
