/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.recipes.inventory.AlloyInventory;
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
    private final Supplier<Map<Metal, Range>> metals;
    private final Supplier<Metal> result;

    public AlloyRecipe(ResourceLocation id, Supplier<Map<Metal, Range>> metals, Supplier<Metal> result)
    {
        this.id = id;
        this.metals = metals;
        this.result = result;
    }

    public Map<Metal, Range> getRanges()
    {
        return metals.get();
    }

    public Metal getResult()
    {
        return result.get();
    }

    @Override
    public boolean matches(AlloyInventory wrapper, @Nullable Level level)
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
            final Metal result = JsonHelpers.getFrom(json, "result", Metal.MANAGER);
            final JsonArray contents = JsonHelpers.getAsJsonArray(json, "contents");
            final ImmutableMap.Builder<Metal, Range> builder = ImmutableMap.builder();
            for (JsonElement element : contents)
            {
                final JsonObject content = JsonHelpers.convertToJsonObject(element, "entry in 'contents'");
                final Metal metal = JsonHelpers.getFrom(content, "metal", Metal.MANAGER);
                final double min = JsonHelpers.getAsDouble(content, "min");
                final double max = JsonHelpers.getAsDouble(content, "max");
                builder.put(metal, new Range(min, max));
            }
            final Map<Metal, Range> metals = builder.build();
            return new AlloyRecipe(recipeId, () -> metals, () -> result);
        }

        @Nullable
        @Override
        public AlloyRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            // Lazily initialize metals, since we cannot guarantee that when this deserializes, the metal manager is loaded.
            final int size = buffer.readVarInt();
            final Map<Supplier<Metal>, Range> metals = new HashMap<>();
            for (int i = 0; i < size; i++)
            {
                final Supplier<Metal> metal = Metal.MANAGER.fromNetwork(buffer);
                final double min = buffer.readDouble();
                final double max = buffer.readDouble();
                metals.put(metal, new Range(min, max));
            }
            final Supplier<Map<Metal, Range>> lazyMetals = Suppliers.memoize(() -> metals.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey().get(), Map.Entry::getValue)));
            final Supplier<Metal> result = Metal.MANAGER.fromNetwork(buffer);
            return new AlloyRecipe(recipeId, lazyMetals, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AlloyRecipe recipe)
        {
            buffer.writeVarInt(recipe.metals.get().size());
            recipe.metals.get().forEach((metal, range) -> {
                Metal.MANAGER.toNetwork(metal, buffer);
                buffer.writeDouble(range.min());
                buffer.writeDouble(range.max());
            });
            Metal.MANAGER.toNetwork(recipe.result.get(), buffer);
        }
    }
}
