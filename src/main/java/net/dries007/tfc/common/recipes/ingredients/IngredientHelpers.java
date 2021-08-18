/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class IngredientHelpers
{
    public static FluidStack fluidStackFromJson(JsonObject json)
    {
        int amount = JSONUtils.getAsInt(json, "amount", -1);
        String fluidName = JSONUtils.getAsString(json, "fluid");
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
        if (fluid == null)
        {
            throw new JsonParseException("Not a fluid: " + fluidName);
        }
        return new FluidStack(fluid, amount);
    }
}
