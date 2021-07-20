package net.dries007.tfc.common.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.container.KnappingContainer;

public class RockKnappingRecipe extends KnappingRecipe
{
    protected final Ingredient predicate;

    public RockKnappingRecipe(ResourceLocation id, SimpleCraftMatrix matrix, ItemStack result, Ingredient predicate)
    {
        super(id, matrix, result, TFCRecipeSerializers.ROCK_KNAPPING.get());
        this.predicate = predicate;
    }

    @Override
    public boolean matches(KnappingContainer container, World level)
    {
        return container.matrix.matches(this.matrix) && predicate.test(container.stackCopy);
    }

    public static class RockSerializer extends TypedRecipeSerializer<RockKnappingRecipe>
    {
        @Override
        public RockKnappingRecipe fromJson(ResourceLocation id, JsonObject json)
        {
            final boolean outsideSlotRequired = JSONUtils.getAsBoolean(json, "outside_slot_required", true);
            final String[] pattern = SimpleCraftMatrix.patternFromJson(json.getAsJsonArray("pattern"));
            final ItemStack stack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
            final Ingredient predicate = json.has("predicate") ? Ingredient.fromJson(json.get("predicate")) : Ingredient.of(TFCTags.Items.ROCK_KNAPPING);
            return new RockKnappingRecipe(id, new SimpleCraftMatrix(outsideSlotRequired, pattern), stack, predicate);
        }

        @Nullable
        @Override
        public RockKnappingRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer)
        {
            final boolean outsideSlotRequired = buffer.readBoolean();
            final SimpleCraftMatrix matrix = SimpleCraftMatrix.fromNetwork(buffer, outsideSlotRequired);
            final ItemStack stack = buffer.readItem();
            final Ingredient predicate = Ingredient.fromNetwork(buffer);
            return new RockKnappingRecipe(id, matrix, stack, predicate);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, RockKnappingRecipe recipe)
        {
            buffer.writeBoolean(recipe.matrix.outsideSlot);
            recipe.matrix.toNetwork(buffer, recipe.matrix.getWidth(), recipe.matrix.getHeight());
            buffer.writeItem(recipe.getResultItem());
            recipe.predicate.toNetwork(buffer);
        }

        @Override
        public IRecipeType<?> getRecipeType()
        {
            return TFCRecipeTypes.ROCK_KNAPPING;
        }
    }
}
