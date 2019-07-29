/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;

@SuppressWarnings("unused")
public class ShapelessFluidFoodRecipe extends ShapelessOreRecipe
{
    public ShapelessFluidFoodRecipe(ResourceLocation group, NonNullList<Ingredient> input, @Nonnull ItemStack result)
    {
        super(group, input, result);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < ret.size(); i++)
        {
            ItemStack itemStack = inv.getStackInSlot(i);

            ItemStack stack = itemStack.copy();
            stack.setCount(1);

            IFluidHandlerItem handler = itemStack.getCount() > 1 ? FluidUtil.getFluidHandler(stack) : FluidUtil.getFluidHandler(itemStack);

            if (handler == null)
            {
                ret.set(i, ForgeHooks.getContainerItem(itemStack));
            }
            else
            {
                handler.drain(Fluid.BUCKET_VOLUME, true);
                ret.set(i, handler.getContainer().copy());
            }
        }
        return ret;
    }

    @Override
    public boolean isDynamic()
    {
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        ItemStack out = output.copy();

        long smallestRottenDate = -1;
        ItemStack foodStack = null;
        for (int slot = 0; slot < inv.getSizeInventory(); slot++)
        {
            ItemStack stack = inv.getStackInSlot(slot);
            if (!stack.isEmpty())
            {
                if (stack.hasCapability(CapabilityFood.CAPABILITY, null))
                {
                    IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);
                    if (smallestRottenDate == -1 || smallestRottenDate > cap.getRottenDate())
                    {
                        smallestRottenDate = stack.getCapability(CapabilityFood.CAPABILITY, null).getRottenDate();
                        foodStack = stack;
                    }
                }
            }
        }

        if (out.hasCapability(CapabilityFood.CAPABILITY, null))
        {
            out.getCapability(CapabilityFood.CAPABILITY, null).setCreationDate(foodStack.getCapability(CapabilityFood.CAPABILITY, null).getCreationDate());
        }

        return out;
    }

    public static class Factory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(final JsonContext context, final JsonObject json)
        {
            final String group = JsonUtils.getString(json, "group", "");
            final NonNullList<Ingredient> ingredients = RecipeUtils.parseShapeless(context, json);
            final ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

            return new ShapelessFluidFoodRecipe(group.isEmpty() ? null : new ResourceLocation(group), ingredients, result);
        }
    }
}
