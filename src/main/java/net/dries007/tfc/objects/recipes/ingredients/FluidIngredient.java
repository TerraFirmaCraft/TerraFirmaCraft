/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes.ingredients;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

@SuppressWarnings("unused")
public class FluidIngredient extends Ingredient
{
    private FluidStack fluid;

    public FluidIngredient(String fluidName)
    {

        super(FluidUtil.getFilledBucket(new FluidStack(FluidRegistry.getFluid(fluidName), Fluid.BUCKET_VOLUME)));
        fluid = FluidRegistry.getFluidStack(fluidName, Fluid.BUCKET_VOLUME);
    }

    @Override
    public boolean apply(@Nullable ItemStack input)
    {
        if (input == null || input.isEmpty())
        {
            return false;
        }

        ItemStack stack = input.copy();
        stack.setCount(1);
        IFluidHandler handler = input.getCount() > 1 ? FluidUtil.getFluidHandler(stack) : FluidUtil.getFluidHandler(input);

        if (handler == null)
        {
            return false;
        }
        return fluid.isFluidStackIdentical(handler.drain(Fluid.BUCKET_VOLUME, false));
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    public static class Factory implements IIngredientFactory
    {
        @Nonnull
        @Override
        public Ingredient parse(JsonContext context, JsonObject json)
        {
            String fluidName = JsonUtils.getString(json, "fluid", "");

            return new FluidIngredient(fluidName);
        }
    }
}
