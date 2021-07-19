package net.dries007.tfc.common.recipes.knapping;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.dries007.tfc.common.container.KnappingContainer;
import net.dries007.tfc.common.recipes.*;

public class KnappingRecipe implements ISimpleRecipe<KnappingContainer>
{
    protected final ResourceLocation id;
    protected final SimpleCraftMatrix matrix;
    protected final ItemStack result;
    protected final Serializer serializer;

    public KnappingRecipe(ResourceLocation id, SimpleCraftMatrix matrix, ItemStack result, Serializer serializer)
    {
        this.id = id;
        this.matrix = matrix;
        this.result = result;
        this.serializer = serializer;
    }

    @Override
    public boolean matches(KnappingContainer container, World level)
    {
        return container.matrix.matches(this.matrix);
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
    public IRecipeSerializer<?> getSerializer()
    {
        return serializer;
    }

    @Override
    public IRecipeType<?> getType()
    {
        return serializer.type;
    }

    public static class Serializer extends RecipeSerializer<KnappingRecipe>
    {
        private final IRecipeType<?> type;

        public Serializer(IRecipeType<?> type)
        {
            this.type = type;
        }

        @Override
        public KnappingRecipe fromJson(ResourceLocation id, JsonObject json)
        {
            final boolean outsideSlotRequired = JSONUtils.getAsBoolean(json, "outside_slot_required", true);
            final String[] pattern = SimpleCraftMatrix.patternFromJson(json.getAsJsonArray("pattern"));
            final ItemStack stack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
            return new KnappingRecipe(id, new SimpleCraftMatrix(outsideSlotRequired, pattern), stack, this);
        }

        @Nullable
        @Override
        public KnappingRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer)
        {
            final boolean outsideSlotRequired = buffer.readBoolean();
            final SimpleCraftMatrix matrix = SimpleCraftMatrix.fromNetwork(buffer, outsideSlotRequired);
            final ItemStack stack = buffer.readItem();
            return new KnappingRecipe(id, matrix, stack, this);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, KnappingRecipe recipe)
        {
            buffer.writeBoolean(recipe.matrix.outsideSlot);
            recipe.matrix.toNetwork(buffer, recipe.matrix.getWidth(), recipe.matrix.getHeight());
            buffer.writeItem(recipe.getResultItem());
        }
    }
}
