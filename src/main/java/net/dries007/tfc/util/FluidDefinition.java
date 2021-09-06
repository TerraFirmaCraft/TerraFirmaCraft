package net.dries007.tfc.util;

import java.util.Collection;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.common.recipes.ingredients.FluidIngredient;
import net.dries007.tfc.common.recipes.ingredients.FluidIngredients;

public class FluidDefinition
{
    protected final ResourceLocation id;
    protected final FluidIngredient ingredient;

    protected FluidDefinition(ResourceLocation id, JsonObject json)
    {
        this(id, FluidIngredients.fromJson(GsonHelper.getAsJsonObject(json, "ingredient")));
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

    public boolean matches(FluidStack stack)
    {
        return ingredient.test(stack);
    }

    public Collection<Fluid> getFluids()
    {
        return ingredient.getMatchingFluids();
    }
}
