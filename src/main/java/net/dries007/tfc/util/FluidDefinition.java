/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Collection;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.common.recipes.ingredients.FluidIngredient;

public class FluidDefinition
{
    protected final ResourceLocation id;
    protected final FluidIngredient ingredient;

    protected FluidDefinition(ResourceLocation id, JsonObject json)
    {
        this(id, FluidIngredient.fromJson(JsonHelpers.get(json, "ingredient")));
    }

    protected FluidDefinition(ResourceLocation id, FluidIngredient ingredient)
    {
        this.id = id;
        this.ingredient = ingredient;
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public boolean matches(Fluid fluid)
    {
        return ingredient.test(fluid);
    }

    public Collection<Fluid> getFluids()
    {
        return ingredient.getMatchingFluids();
    }
}
