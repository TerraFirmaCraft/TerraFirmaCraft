package net.dries007.tfc.common.recipes;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class HeatingRecipe extends SimpleItemRecipe
{
    public static final IndirectHashCollection<Item, HeatingRecipe> CACHE = new IndirectHashCollection<>(HeatingRecipe::getValidItems);
    private final float temperature;

    public HeatingRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result, float temperature)
    {
        super(id, ingredient, result);
        this.temperature = temperature;
    }

    @Nullable
    public static HeatingRecipe getRecipe(World world, ItemStackRecipeWrapper wrapper)
    {
        for (HeatingRecipe recipe : CACHE.getAll(wrapper.getStack().getItem()))
        {
            if (recipe.matches(wrapper, world))
            {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public ItemStack assemble(ItemStackRecipeWrapper wrapper)
    {
        ItemStack stack = wrapper.getStack();
        IHeat cap = stack.getCapability(HeatCapability.CAPABILITY, null).resolve().orElse(null);
        if (cap != null)
        {
            ItemStack output = result.copy();
            output.getCapability(HeatCapability.CAPABILITY, null).ifPresent(newCap -> newCap.setTemperature(cap.getTemperature()));
            return output;
        }
        return ItemStack.EMPTY;
    }

    public float getTemperature()
    {
        return temperature;
    }

    public boolean isValidTemperature(float temperatureIn)
    {
        return temperatureIn >= temperature;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.HEATING.get();
    }

    @Override
    public IRecipeType<?> getType()
    {
        return TFCRecipeTypes.HEATING;
    }

    public Collection<Item> getValidItems()
    {
        return Arrays.stream(this.ingredient.getItems()).map(ItemStack::getItem).collect(Collectors.toSet());
    }

    public static class Serializer extends RecipeSerializer<HeatingRecipe>
    {
        @Override
        public HeatingRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
            ItemStack stack = ShapedRecipe.itemFromJson(json.getAsJsonObject("result"));
            float temp = JSONUtils.getAsFloat(json, "temperature");
            return new HeatingRecipe(recipeId, ingredient, stack, temp);
        }

        @Nullable
        @Override
        public HeatingRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer)
        {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack stack = buffer.readItem();
            float temp = buffer.readFloat();
            return new HeatingRecipe(recipeId, ingredient, stack, temp);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, HeatingRecipe recipe)
        {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.getResultItem());
            buffer.writeFloat(recipe.getTemperature());
        }
    }
}
