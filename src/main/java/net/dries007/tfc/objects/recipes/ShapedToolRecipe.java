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
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

import net.dries007.tfc.api.capability.heat.IItemHeat;

import static net.dries007.tfc.api.capability.heat.CapabilityItemHeat.ITEM_HEAT_CAPABILITY;

/**
 * Temperature-sensitive ShapedOreRecipe
 */
@SuppressWarnings("unused")
public class ShapedToolRecipe extends ShapedOreRecipe
{
    private ShapedToolRecipe(ResourceLocation group, @Nonnull ItemStack result, CraftingHelper.ShapedPrimer primer)
    {
        super(group, result, primer);
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
            String group = JsonUtils.getString(json, "group", "");
            CraftingHelper.ShapedPrimer primer = RecipeUtils.parseShaped(context, json);
            ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
            return new ShapedToolRecipe(group.isEmpty() ? null : new ResourceLocation(group), result, primer);
        }
    }
}
