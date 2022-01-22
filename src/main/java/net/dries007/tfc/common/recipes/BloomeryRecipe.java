package net.dries007.tfc.common.recipes;

import java.util.Arrays;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.blockentities.BloomeryBlockEntity;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.JsonHelpers;

public class BloomeryRecipe implements ISimpleRecipe<BloomeryBlockEntity.BloomeryInventory>
{
    public static final Logger LOGGER = LogManager.getLogger();

    private final ResourceLocation id;
    private final FluidStackIngredient fluidStack;
    private final ItemStackIngredient catalystStack;
    private final ItemStack result;
    private final int time;

    //todo: make catalyst optional? unsure how
    public BloomeryRecipe(ResourceLocation id, FluidStackIngredient fluidStack, ItemStackIngredient catalystStack, ItemStack result, int time)
    {
        LOGGER.info("constructing a bloomery recipe: "+id+" "+fluidStack+" "+catalystStack+" "+result+" "+time);
        this.id = id;
        this.fluidStack = fluidStack;
        this.catalystStack = catalystStack;
        this.result = result;
        this.time = time;
    }

    public int getTime()
    {
        return time;
    }

    public ItemStackIngredient getCatalyst()
    {
        return catalystStack;
    }

    public FluidStackIngredient getInputFluid()
    {
        return fluidStack;
    }

    @Override
    public boolean matches(BloomeryBlockEntity.BloomeryInventory inv, Level level)
    {
        Fluid inputFluid = inv.getInputFluid();
        if (inputFluid.isSame(Fluids.EMPTY))
        {
            return false;
        }
        LOGGER.info("Trying to match a recipe for "+inputFluid+" against "+fluidStack.getMatchingFluids());
        return fluidStack.getMatchingFluids().contains(inputFluid);
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
        return TFCRecipeSerializers.BLOOMERY.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.BLOOMERY.get();
    }

    public boolean isValidInput(ItemStack stack)
    {
        HeatingRecipe heatingRecipe = HeatingRecipe.getRecipe(stack);
        if (heatingRecipe != null)
        {
            return this.fluidStack.getMatchingFluids().contains(heatingRecipe.getOutputFluid(new ItemStackInventory(stack)).getFluid());
        }
        return false;
    }

    public boolean isValidCatalyst(ItemStack stack)
    {
        return Arrays.asList(this.catalystStack.getItem().getItems()).contains(stack);
    }

    public static class Serializer extends RecipeSerializerImpl<BloomeryRecipe>
    {
        @Override
        public BloomeryRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final FluidStackIngredient fluidStack = FluidStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "fluid"));
            final ItemStackIngredient catalystStack = ItemStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "catalyst"));
            final ItemStack result = JsonHelpers.getItemStack(json, "result");
            final int time = JsonHelpers.getAsInt(json, "time");
            return new BloomeryRecipe(recipeId, fluidStack, catalystStack, result, time);
        }

        @Nullable
        @Override
        public BloomeryRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final FluidStackIngredient fluidStack = FluidStackIngredient.fromNetwork(buffer);
            final ItemStackIngredient catalystStack = ItemStackIngredient.fromNetwork(buffer);
            final ItemStack result = buffer.readItem();
            final int time = buffer.readInt();
            return new BloomeryRecipe(recipeId, fluidStack, catalystStack, result, time);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BloomeryRecipe recipe)
        {
            FluidStackIngredient.toNetwork(buffer, recipe.fluidStack);
            recipe.catalystStack.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeInt(recipe.time);
        }
    }
}
