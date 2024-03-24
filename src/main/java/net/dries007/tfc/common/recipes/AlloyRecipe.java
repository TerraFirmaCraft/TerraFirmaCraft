/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.recipes.inventory.AlloyInventory;
import net.dries007.tfc.util.DataManager;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.Metal;
import org.jetbrains.annotations.Nullable;

public class AlloyRecipe implements ISimpleRecipe<AlloyInventory>
{
    @SuppressWarnings("ConstantConditions")
    public static Optional<AlloyRecipe> get(RecipeManager recipes, AlloyInventory inventory)
    {
        return recipes.getRecipeFor(TFCRecipeTypes.ALLOY.get(), inventory, null);
    }

    private final ResourceLocation id;
    private final Map<DataManager.Reference<Metal>, Range> metals;
    private final DataManager.Reference<Metal> result;

    public AlloyRecipe(ResourceLocation id, Map<DataManager.Reference<Metal>, Range> metals, DataManager.Reference<Metal> result)
    {
        this.id = id;
        this.metals = metals;
        this.result = result;
    }

    public Map<DataManager.Reference<Metal>, Range> getRanges()
    {
        return metals;
    }

    public Metal getResult()
    {
        return result.get();
    }

    @Override
    public boolean matches(AlloyInventory wrapper, @Nullable Level level)
    {
        return wrapper.alloy().matches(this);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess)
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
        return TFCRecipeTypes.ALLOY.get();
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
            final DataManager.Reference<Metal> result = JsonHelpers.getReference(json, "result", Metal.MANAGER);
            final JsonArray contents = JsonHelpers.getAsJsonArray(json, "contents");
            final Map<DataManager.Reference<Metal>, Range> metals = new IdentityHashMap<>();
            for (JsonElement element : contents)
            {
                final JsonObject content = JsonHelpers.convertToJsonObject(element, "entry in 'contents'");
                final DataManager.Reference<Metal> metal = JsonHelpers.getReference(content, "metal", Metal.MANAGER);
                final double min = JsonHelpers.getAsDouble(content, "min");
                final double max = JsonHelpers.getAsDouble(content, "max");
                if (metals.containsKey(metal))
                {
                    throw new JsonParseException("Duplicate entry for metal: " + metal.id());
                }
                metals.put(metal, new Range(min, max));
            }
            return new AlloyRecipe(recipeId, metals, result);
        }

        @Nullable
        @Override
        public AlloyRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            // Lazily initialize metals, since we cannot guarantee that when this deserializes, the metal manager is loaded.
            final int size = buffer.readVarInt();
            final Map<DataManager.Reference<Metal>, Range> metals = new IdentityHashMap<>();
            for (int i = 0; i < size; i++)
            {
                final DataManager.Reference<Metal> metal = Metal.MANAGER.getReference(buffer.readResourceLocation());
                final double min = buffer.readDouble();
                final double max = buffer.readDouble();
                metals.put(metal, new Range(min, max));
            }
            final DataManager.Reference<Metal> result = Metal.MANAGER.getReference(buffer.readResourceLocation());
            return new AlloyRecipe(recipeId, metals, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AlloyRecipe recipe)
        {
            buffer.writeVarInt(recipe.metals.size());
            recipe.metals.forEach((metal, range) -> {
                buffer.writeResourceLocation(metal.id());
                buffer.writeDouble(range.min());
                buffer.writeDouble(range.max());
            });
            buffer.writeResourceLocation(recipe.result.get().getId());
        }
    }
}
