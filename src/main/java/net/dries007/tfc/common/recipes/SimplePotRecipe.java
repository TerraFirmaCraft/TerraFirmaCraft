/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

import com.mojang.serialization.JsonOps;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.PerfectMatchingWithEdmondsMatrix;

public class SimplePotRecipe implements IPotRecipe
{
    protected final ResourceLocation id;
    protected final List<Ingredient> inputItems;
    protected final NonNullList<ItemStack> outputItems;
    protected final int duration;
    protected final float temperature;
    protected final FluidStack inputFluid;
    protected final FluidStack outputFluid;

    public SimplePotRecipe(ResourceLocation id, List<Ingredient> inputItems, NonNullList<ItemStack> outputItems, FluidStack inputFluid, FluidStack outputFluid, float temperature, int duration)
    {
        this.id = id;
        this.inputItems = inputItems;
        this.outputItems = outputItems;
        this.duration = duration;
        this.temperature = temperature;
        this.inputFluid = inputFluid;
        this.outputFluid = outputFluid;
    }

    @Override
    public int getDuration()
    {
        return duration;
    }

    @Override
    public boolean isValidTemperature(float temperatureIn)
    {
        return temperatureIn >= temperature;
    }

    @Override
    public boolean matches(FluidInventoryRecipeWrapper wrapper, World worldIn)
    {
        if (!wrapper.getInputFluid().isFluidEqual(inputFluid)) return false;

        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < wrapper.getContainerSize(); i++)
        {
            ItemStack item = wrapper.getItem(i);
            if (!item.isEmpty())
            {
                stacks.add(item.copy());
            }
        }
        return PerfectMatchingWithEdmondsMatrix.perfectMatchExists(stacks, inputItems);
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.SIMPLE_POT.get();
    }

    /**
     * @return A copy of the output fluid
     */
    @Override
    public FluidStack getOutputFluid()
    {
        return outputFluid.copy();
    }

    /**
     * @return A copy of the output items
     */
    @Override
    public NonNullList<ItemStack> getOutputItems()
    {
        return Helpers.copyItemList(outputItems);
    }

    @Override
    public Output getOutput(ItemStackHandler inv, FluidStack fluid)
    {
        return new Output() {}; // returns isEmpty = true by default, so this will get discarded right away
    }

    public static class Serializer extends RecipeSerializer<SimplePotRecipe>
    {
        private final SimplePotRecipe.Serializer.Factory<SimplePotRecipe> factory;

        public Serializer(SimplePotRecipe.Serializer.Factory<SimplePotRecipe> factory)
        {
            this.factory = factory;
        }

        @Override
        public SimplePotRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            List<Ingredient> ingredients = new ArrayList<>(5);
            json.getAsJsonArray("ingredients").forEach(i -> ingredients.add(Ingredient.fromJson(i)));

            NonNullList<ItemStack> outputs = NonNullList.create();
            json.getAsJsonArray("outputs").forEach(i -> outputs.add(ShapedRecipe.itemFromJson(i.getAsJsonObject())));

            FluidStack input = FluidStack.CODEC.decode(JsonOps.INSTANCE, json.get("fluidInput")).getOrThrow(false, null).getFirst();
            FluidStack output = FluidStack.CODEC.decode(JsonOps.INSTANCE, json.get("fluidOutput")).getOrThrow(false, null).getFirst();
            int duration = json.get("duration").getAsInt();
            float temp = json.get("temperature").getAsFloat();

            return factory.create(recipeId, ingredients, outputs, input, output, temp, duration);
        }

        @Nullable
        @Override
        public SimplePotRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer)
        {
            List<Ingredient> ingredients = new ArrayList<>(5);
            int inputCount = buffer.readInt();
            for (int i = 0; i < inputCount; i++)
                ingredients.add(Ingredient.fromNetwork(buffer));

            NonNullList<ItemStack> outputs = NonNullList.create();
            int outputCount = buffer.readInt();
            for (int i = 0; i < outputCount; i++)
                outputs.add(buffer.readItem());

            FluidStack inputFluid = buffer.readFluidStack();
            FluidStack outputFluid = buffer.readFluidStack();

            int duration = buffer.readInt();
            float temp = buffer.readFloat();

            return factory.create(recipeId, ingredients, outputs, inputFluid, outputFluid, temp, duration);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, SimplePotRecipe recipe)
        {
            buffer.writeInt(recipe.inputItems.size());
            recipe.inputItems.forEach(i -> i.toNetwork(buffer));

            buffer.writeInt(recipe.outputItems.size());
            recipe.outputItems.forEach(buffer::writeItem);

            buffer.writeFluidStack(recipe.inputFluid);
            buffer.writeFluidStack(recipe.outputFluid);

            buffer.writeInt(recipe.duration);
            buffer.writeFloat(recipe.temperature);
        }

        protected interface Factory<SimplePotRecipe>
        {
            SimplePotRecipe create(ResourceLocation id, List<Ingredient> inputItems, NonNullList<ItemStack> outputItems, FluidStack inputFluid, FluidStack outputFluid, float temperature, int duration);
        }
    }
}
