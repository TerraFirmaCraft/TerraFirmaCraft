package net.dries007.tfc.common.recipes;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.recipes.inventory.AlloyRecipeWrapper;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.Metal;

public class AlloyRecipe implements ISimpleRecipe<AlloyRecipeWrapper>
{
    private final ResourceLocation id;
    private final Map<Metal, Range> metals;
    private final Metal result;

    public AlloyRecipe(ResourceLocation id, Map<Metal, Range> metals, Metal result)
    {
        this.id = id;
        this.metals = metals;
        this.result = result;
    }

    public Map<Metal, Range> getRanges()
    {
        return metals;
    }

    public Metal getResult()
    {
        return result;
    }

    @Override
    public boolean matches(AlloyRecipeWrapper wrapper, Level level)
    {
        return wrapper.getAlloy().matches(this);
    }

    @Override
    public ItemStack getResultItem()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.ALLOY.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.ALLOY;
    }

    public record Range(double min, double max)
    {
        public boolean isIn(double value, double epsilon)
        {
            return min - epsilon <= value && value <= max + epsilon;
        }
    }

    public static class Serializer extends RecipeSerializerImpl<AlloyRecipe>
    {
        @Override
        public AlloyRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final Metal result = JsonHelpers.getFrom(json, "result", Metal.MANAGER);
            final JsonArray contents = JsonHelpers.getAsJsonArray(json, "contents");
            final Map<Metal, Range> metals = new HashMap<>();
            for (JsonElement element : contents)
            {
                final JsonObject content = JsonHelpers.convertToJsonObject(element, "entry in 'contents'");
                final Metal metal = JsonHelpers.getFrom(content, "metal", Metal.MANAGER);
                final double min = JsonHelpers.getAsDouble(content, "min");
                final double max = JsonHelpers.getAsDouble(content, "max");
                metals.put(metal, new Range(min, max));
            }
            return new AlloyRecipe(recipeId, metals, result);
        }

        @Nullable
        @Override
        public AlloyRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final int size = buffer.readVarInt();
            final Map<Metal, Range> metals = new HashMap<>();
            for (int i = 0; i < size; i++)
            {
                final Metal metal = Metal.MANAGER.getOrThrow(buffer.readResourceLocation());
                final double min = buffer.readDouble();
                final double max = buffer.readDouble();
                metals.put(metal, new Range(min, max));
            }
            final Metal result = Metal.MANAGER.getOrThrow(buffer.readResourceLocation());
            return new AlloyRecipe(recipeId, metals, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AlloyRecipe recipe)
        {
            buffer.writeVarInt(recipe.metals.size());
            recipe.metals.forEach((metal, range) -> {
                buffer.writeResourceLocation(metal.getId());
                buffer.writeDouble(range.min());
                buffer.writeDouble(range.max());
            });
            buffer.writeResourceLocation(recipe.result.getId());
        }
    }
}
