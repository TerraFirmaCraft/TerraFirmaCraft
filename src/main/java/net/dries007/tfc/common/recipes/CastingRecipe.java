/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.capabilities.MoldLike;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import org.jetbrains.annotations.Nullable;

public class CastingRecipe implements ISimpleRecipe<MoldLike>
{
    public static final IndirectHashCollection<Item, CastingRecipe> CACHE = IndirectHashCollection.createForRecipe(recipe -> Arrays.stream(recipe.ingredient.getItems()).map(ItemStack::getItem).collect(Collectors.toList()), TFCRecipeTypes.CASTING);

    @Nullable
    public static CastingRecipe get(MoldLike mold)
    {
        for (CastingRecipe recipe : CACHE.getAll(mold.getContainer().getItem()))
        {
            if (recipe.matches(mold, null))
            {
                return recipe;
            }
        }
        return null;
    }

    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final FluidStackIngredient fluidIngredient;
    private final ItemStack result;
    private final float breakChance;

    public CastingRecipe(ResourceLocation id, Ingredient ingredient, FluidStackIngredient fluidIngredient, ItemStack result, float breakChance)
    {
        this.id = id;
        this.ingredient = ingredient;
        this.fluidIngredient = fluidIngredient;
        this.result = result;
        this.breakChance = breakChance;
    }

    public float getBreakChance()
    {
        return breakChance;
    }

    public Ingredient getIngredient()
    {
        return ingredient;
    }

    public FluidStackIngredient getFluidIngredient()
    {
        return fluidIngredient;
    }

    @Override
    public boolean matches(MoldLike mold, @Nullable Level level)
    {
        return ingredient.test(mold.getContainer()) && fluidIngredient.test(mold.getFluidInTank(0));
    }

    @Override
    public ItemStack assemble(MoldLike inventory)
    {
        final ItemStack stack = result.copy();
        stack.getCapability(HeatCapability.CAPABILITY).ifPresent(h -> h.setTemperatureIfWarmer(inventory.getTemperature()));
        return stack;
    }

    @Override
    public ItemStack getResultItem()
    {
        return result;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.CASTING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.CASTING.get();
    }

    public static class Serializer extends RecipeSerializerImpl<CastingRecipe>
    {
        @Override
        public CastingRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final Ingredient ingredient = Ingredient.fromJson(JsonHelpers.get(json, "mold"));
            final FluidStackIngredient fluidIngredient = FluidStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "fluid"));
            final ItemStack result = JsonHelpers.getItemStack(json, "result");
            final float breakChance = JsonHelpers.getAsFloat(json, "break_chance");
            return new CastingRecipe(recipeId, ingredient, fluidIngredient, result, breakChance);
        }

        @Nullable
        @Override
        public CastingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final Ingredient ingredient = Ingredient.fromNetwork(buffer);
            final FluidStackIngredient fluidIngredient = FluidStackIngredient.fromNetwork(buffer);
            final ItemStack result = buffer.readItem();
            final float breakChance = buffer.readFloat();
            return new CastingRecipe(recipeId, ingredient, fluidIngredient, result, breakChance);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CastingRecipe recipe)
        {
            recipe.ingredient.toNetwork(buffer);
            recipe.fluidIngredient.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeFloat(recipe.breakChance);
        }
    }
}
