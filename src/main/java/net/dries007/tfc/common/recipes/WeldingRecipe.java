/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.recipes.inventory.EmptyInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

public class WeldingRecipe implements ISimpleRecipe<WeldingRecipe.Inventory>
{
    private final ResourceLocation id;
    private final Ingredient firstInput, secondInput;
    private final int tier;
    private final ItemStackProvider output;

    public WeldingRecipe(ResourceLocation id, Ingredient firstInput, Ingredient secondInput, int tier, ItemStackProvider output)
    {
        this.id = id;
        this.firstInput = firstInput;
        this.secondInput = secondInput;
        this.tier = tier;
        this.output = output;
    }

    public int getTier()
    {
        return tier;
    }

    /**
     * This is used when querying the matching recipe for a stack.
     * As such it doesn't check if the recipe is complete, but only if the recipe could be completed.
     */
    @Override
    public boolean matches(Inventory inventory, Level level)
    {
        final ItemStack left = inventory.getLeft(), right = inventory.getRight();
        return (firstInput.test(left) && secondInput.test(right)) || (firstInput.test(right) && secondInput.test(left));
    }

    @Override
    public ItemStack assemble(Inventory inventory)
    {
        return output.getSingleStack(inventory.getLeft());
    }

    @Override
    public ItemStack getResultItem()
    {
        return output.getEmptyStack();
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.WELDING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.WELDING.get();
    }

    public Ingredient getFirstInput()
    {
        return firstInput;
    }

    public Ingredient getSecondInput()
    {
        return secondInput;
    }

    public interface Inventory extends EmptyInventory
    {
        ItemStack getLeft();

        ItemStack getRight();

        int getTier();
    }

    public static class Serializer extends RecipeSerializerImpl<WeldingRecipe>
    {
        @Override
        public WeldingRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final Ingredient firstInput = Ingredient.fromJson(JsonHelpers.get(json, "first_input"));
            final Ingredient secondInput = Ingredient.fromJson(JsonHelpers.get(json, "second_input"));
            final int tier = JsonHelpers.getAsInt(json, "tier", -1);
            final ItemStackProvider output = ItemStackProvider.fromJson(JsonHelpers.getAsJsonObject(json, "result"));
            return new WeldingRecipe(recipeId, firstInput, secondInput, tier, output);
        }

        @Nullable
        @Override
        public WeldingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final Ingredient firstInput = Ingredient.fromNetwork(buffer);
            final Ingredient secondInput = Ingredient.fromNetwork(buffer);
            final int tier = buffer.readVarInt();
            final ItemStackProvider output = ItemStackProvider.fromNetwork(buffer);
            return new WeldingRecipe(recipeId, firstInput, secondInput, tier, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, WeldingRecipe recipe)
        {
            recipe.firstInput.toNetwork(buffer);
            recipe.secondInput.toNetwork(buffer);
            buffer.writeVarInt(recipe.tier);
            recipe.output.toNetwork(buffer);
        }
    }
}
