/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import net.dries007.tfc.common.recipes.ingredients.FluidIngredient;
import net.dries007.tfc.common.tileentity.PotTileEntity;
import net.dries007.tfc.util.Helpers;

/**
 * Recipe type for all cooking pot recipes
 */
public abstract class PotRecipe implements ISimpleRecipe<PotTileEntity.PotInventory>
{
    protected final ResourceLocation id;
    protected final List<Ingredient> itemIngredients;
    protected final FluidIngredient fluidIngredient;
    protected final int duration;
    protected final float minTemp;

    protected PotRecipe(ResourceLocation id, List<Ingredient> itemIngredients, FluidIngredient fluidIngredient, int duration, float minTemp)
    {
        this.id = id;
        this.itemIngredients = itemIngredients;
        this.fluidIngredient = fluidIngredient;
        this.duration = duration;
        this.minTemp = minTemp;
    }

    @Override
    public boolean matches(PotTileEntity.PotInventory inventory, World worldIn)
    {
        if (!fluidIngredient.test(inventory.getFluidInTank(0)))
        {
            return false;
        }
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = PotTileEntity.SLOT_EXTRA_INPUT_START; i <= PotTileEntity.SLOT_EXTRA_INPUT_END; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                stacks.add(stack);
            }
        }
        return Helpers.perfectMatchExists(stacks, itemIngredients);
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
    public IRecipeType<?> getType()
    {
        return TFCRecipeTypes.POT;
    }

    /**
     * @return true if the temperature is hot enough to boil
     */
    public boolean isHotEnough(float tempIn)
    {
        return tempIn > minTemp;
    }

    /**
     * @return The number of ticks needed to boil for.
     */
    public int getDuration()
    {
        return duration;
    }

    /**
     * @return The output of the pot recipe.
     */
    public abstract PotRecipe.Output getOutput(PotTileEntity.PotInventory inventory);

    /**
     * The output of a pot recipe
     * This output can be fairly complex, but follows a specific contract:
     * 1. The output is created, with access to the inventory, populated with the ingredient items (in {@link PotRecipe#getOutput(PotTileEntity.PotInventory)}
     * 2. {@link Output#onFinish(PotTileEntity.PotInventory)} is called, with a completely empty inventory. The output can then add fluids or items back into the pot as necessary
     * 3. THEN, if {@link Output#isEmpty()} returns true, the output is discarded. Otherwise...
     * 4. The output is saved to the tile entity. On a right click, {@link Output#onInteract(PotTileEntity, PlayerEntity, ItemStack)} is called, and after each call, {@link Output#isEmpty()} will be queried to see if the output is empty. The pot will not resume functionality until the output is empty
     */
    public interface Output extends INBTSerializable<CompoundNBT>
    {
        /**
         * If there is still something to be extracted from this output. If this returns false at any time the output must be serializable
         */
        default boolean isEmpty()
        {
            return true;
        }

        /**
         * If the pot, while storing this output, should render a default reddish-brown fluid as inside the pot, despite the pot itself not necessarily being filled with any fluid
         */
        default boolean renderDefaultFluid()
        {
            return false;
        }

        /**
         * Called with an empty pot inventory immediately after completion, before checking {@link #isEmpty()}. Fills the inventory with immediate outputs from the output.
         */
        default void onFinish(PotTileEntity.PotInventory inventory) {}

        /**
         * Called when a player interacts with the pot inventory, using the specific item stack, to try and extract output.
         */
        default ActionResultType onInteract(PotTileEntity entity, PlayerEntity player, ItemStack clickedWith)
        {
            return ActionResultType.PASS;
        }

        default CompoundNBT serializeNBT()
        {
            throw new UnsupportedOperationException();
        }

        default void deserializeNBT(CompoundNBT nbt) {}
    }

    public abstract static class Serializer<R extends PotRecipe> extends RecipeSerializer<R>
    {
        @Override
        public R fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final JsonArray array = JSONUtils.getAsJsonArray(json, "ingredients");
            final List<Ingredient> ingredients = new ArrayList<>();
            for (JsonElement element : array)
            {
                ingredients.add(Ingredient.fromJson(element));
            }

            final FluidIngredient fluidIngredient = FluidIngredient.fromJson(JSONUtils.getAsJsonObject(json, "fluid_ingredient"));
            final int duration = JSONUtils.getAsInt(json, "duration");
            final float minTemp = JSONUtils.getAsFloat(json, "temperature");
            return fromJson(recipeId, json, ingredients, fluidIngredient, duration, minTemp);
        }

        @Nullable
        @Override
        public R fromNetwork(ResourceLocation recipeId, PacketBuffer buffer)
        {
            final int count = buffer.readVarInt();
            final List<Ingredient> ingredients = new ArrayList<>();
            for (int i = 0; i < count; i++)
            {
                ingredients.add(Ingredient.fromNetwork(buffer));
            }
            final FluidIngredient fluidIngredient = FluidIngredient.fromNetwork(buffer);
            final int duration = buffer.readVarInt();
            final float minTemp = buffer.readFloat();
            return fromNetwork(recipeId, buffer, ingredients, fluidIngredient, duration, minTemp);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, R recipe)
        {
            buffer.writeVarInt(recipe.itemIngredients.size());
            for (Ingredient ingredient : recipe.itemIngredients)
            {
                ingredient.toNetwork(buffer);
            }
            FluidIngredient.toNetwork(buffer, recipe.fluidIngredient);
            buffer.writeVarInt(recipe.duration);
            buffer.writeFloat(recipe.minTemp);
        }

        protected abstract R fromJson(ResourceLocation recipeId, JsonObject json, List<Ingredient> ingredients, FluidIngredient fluidIngredient, int duration, float minTemp);

        protected abstract R fromNetwork(ResourceLocation recipeId, PacketBuffer buffer, List<Ingredient> ingredients, FluidIngredient fluidIngredient, int duration, float minTemp);
    }
}
