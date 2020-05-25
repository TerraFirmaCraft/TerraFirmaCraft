/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

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
@ParametersAreNonnullByDefault
public class ShapelessFluidFoodRecipe extends ShapelessOreRecipe
{
    public ShapelessFluidFoodRecipe(ResourceLocation group, NonNullList<Ingredient> input, @Nonnull ItemStack result)
    {
        super(group, input, result);
    }

    @Override
    @Nonnull
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
                ItemStack updatedItem = handler.getContainer().copy();
                ret.set(i, updatedItem);
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
    @Nonnull
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
                IFood foodCap = stack.getCapability(CapabilityFood.CAPABILITY, null);
                if (foodCap != null && (smallestRottenDate == -1 || smallestRottenDate > foodCap.getRottenDate()))
                {
                    smallestRottenDate = foodCap.getRottenDate();
                    foodStack = stack;
                }
            }
        }
        return foodStack != null ? CapabilityFood.updateFoodFromPrevious(foodStack, out) : ItemStack.EMPTY;
    }

    public static class Factory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(final JsonContext context, final JsonObject json)
        {
            final String group = JsonUtils.getString(json, "group", "");
            final NonNullList<Ingredient> ingredients = RecipeUtils.parseShapeless(context, json);
            final ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
            //noinspection ConstantConditions
            return new ShapelessFluidFoodRecipe(group.isEmpty() ? null : new ResourceLocation(group), ingredients, result);
        }
    }
}
