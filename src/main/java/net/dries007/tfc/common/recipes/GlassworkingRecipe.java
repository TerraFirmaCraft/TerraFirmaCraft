/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.capabilities.glass.GlassOperation;
import net.dries007.tfc.common.capabilities.glass.GlassOperations;
import net.dries007.tfc.common.capabilities.glass.GlassWorkData;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.JsonHelpers;

public class GlassworkingRecipe implements ISimpleRecipe<ItemStackInventory>
{

    private final ResourceLocation id;
    private final List<GlassOperation> operations;
    private final ItemStack resultItem;

    public GlassworkingRecipe(ResourceLocation id, List<GlassOperation> operations, ItemStack resultItem)
    {
        this.id = id;
        this.operations = operations;
        this.resultItem = resultItem;
    }

    @Override
    public boolean matches(ItemStackInventory inv, Level level)
    {
        final GlassWorkData data = GlassWorkData.get(inv.getStack());
        if (data != null)
        {
            final List<GlassOperation> steps = data.getOperations().getSteps();
            return steps.equals(operations);
        }
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access)
    {
        return resultItem.copy();
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.GLASSWORKING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.GLASSWORKING.get();
    }

    public static class Serializer extends RecipeSerializerImpl<GlassworkingRecipe>
    {
        @Override
        public GlassworkingRecipe fromJson(ResourceLocation id, JsonObject json)
        {
            final List<GlassOperation> operations = new ArrayList<>();
            for (JsonElement element : json.getAsJsonArray("operations"))
            {
                operations.add(JsonHelpers.getEnum(element, GlassOperation.class));
            }
            final ItemStack result = JsonHelpers.getItemStack(json, "result");
            return new GlassworkingRecipe(id, operations, result);
        }

        @Override
        public GlassworkingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
        {
            final int capacity = buffer.readVarInt();
            final List<GlassOperation> ops = new ArrayList<>(capacity);
            for (int i = 0; i < capacity; i++)
            {
                ops.add(buffer.readEnum(GlassOperation.class));
            }
            final ItemStack result = buffer.readItem();
            return new GlassworkingRecipe(id, ops, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, GlassworkingRecipe recipe)
        {
            buffer.writeVarInt(recipe.operations.size());
            for (GlassOperation op : recipe.operations)
            {
                buffer.writeEnum(op);
            }
            buffer.writeItem(recipe.resultItem);
        }
    }
}
