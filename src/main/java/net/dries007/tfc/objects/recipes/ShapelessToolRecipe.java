/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import net.dries007.tfc.api.capability.heat.IItemHeat;

import static net.dries007.tfc.api.capability.heat.CapabilityItemHeat.ITEM_HEAT_CAPABILITY;

/**
 * Temperature-sensitive ShapelessOreRecipe
 */
@SuppressWarnings("unused")
public class ShapelessToolRecipe extends ShapelessOreRecipe
{
    private ShapelessToolRecipe(ResourceLocation group, NonNullList<Ingredient> input, @Nonnull ItemStack result)
    {
        super(group, input, result);
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv)
    {
        ItemStack outputStack = output.copy();
        float outputTemp = 0;
        int count = 0;

        for (int slot = 0; slot < inv.getSizeInventory(); slot++)
        {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            IItemHeat heat = stack.getCapability(ITEM_HEAT_CAPABILITY, null);
            if (heat != null && stack.getItem() != Items.STICK)
            {
                outputTemp = (count == 0) ? heat.getTemperature() : outputTemp + heat.getTemperature();
                count++;
            }
        }

        if (count != 0)
        {
            IItemHeat heat = outputStack.getCapability(ITEM_HEAT_CAPABILITY, null);
            if (heat != null)
            {
                heat.setTemperature(outputTemp / count);
            }
        }

        return outputStack;
    }

    public static class Factory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(final JsonContext context, final JsonObject json)
        {
            final String group = JsonUtils.getString(json, "group", "");
            final NonNullList<Ingredient> ingredients = RecipeUtils.parseShapeless(context, json);
            final ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
            return new ShapelessToolRecipe(group.isEmpty() ? null : new ResourceLocation(group), ingredients, result);
        }
    }
}
