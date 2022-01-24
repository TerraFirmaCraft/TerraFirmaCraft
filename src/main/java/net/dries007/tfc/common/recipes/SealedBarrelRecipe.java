package net.dries007.tfc.common.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.recipes.ingredients.FluidIngredient;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public class SealedBarrelRecipe extends BarrelRecipe
{
    private final int duration;

    @Nullable private final ItemStackProvider onSeal;
    @Nullable private final ItemStackProvider onUnseal;

    public SealedBarrelRecipe(ResourceLocation id, ItemStackIngredient inputItem, FluidStackIngredient inputFluid, ItemStackProvider outputItem, FluidStack outputFluid, int duration, @Nullable ItemStackProvider onSeal, @Nullable ItemStackProvider onUnseal)
    {
        super(id, inputItem, inputFluid, outputItem, outputFluid);

        this.duration = duration;
        this.onSeal = onSeal;
        this.onUnseal = onUnseal;
    }

    public int getDuration()
    {
        return duration;
    }

    public boolean isInfinite()
    {
        return duration <= 0;
    }

    public void onSealed(BarrelBlockEntity.BarrelInventory inventory)
    {
        if (onSeal != null)
        {
            final ItemStack stack = Helpers.removeStack(inventory, BarrelBlockEntity.SLOT_ITEM);
            inventory.setStackInSlot(BarrelBlockEntity.SLOT_ITEM, onSeal.getStack(stack));
        }
    }

    public void onUnsealed(BarrelBlockEntity.BarrelInventory inventory)
    {
        if (onUnseal != null)
        {
            final ItemStack stack = Helpers.removeStack(inventory, BarrelBlockEntity.SLOT_ITEM);
            inventory.setStackInSlot(BarrelBlockEntity.SLOT_ITEM, onUnseal.getStack(stack));
        }
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.BARREL_SEALED.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.SEALED_BARREL.get();
    }

    public static class Serializer extends RecipeSerializerImpl<SealedBarrelRecipe>
    {
        @Override
        public SealedBarrelRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final Builder builder = Builder.fromJson(json);
            final int duration = JsonHelpers.getAsInt(json, "duration");
            final ItemStackProvider onSeal = json.has("on_seal") ? ItemStackProvider.fromJson(JsonHelpers.getAsJsonObject(json, "on_seal")) : null;
            final ItemStackProvider onUnseal = json.has("on_unseal") ? ItemStackProvider.fromJson(JsonHelpers.getAsJsonObject(json, "on_unseal")) : null;
            return new SealedBarrelRecipe(recipeId, builder.inputItem(), builder.inputFluid(), builder.outputItem(), builder.outputFluid(), duration, onSeal, onUnseal);
        }

        @Nullable
        @Override
        public SealedBarrelRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final Builder builder = Builder.fromNetwork(buffer);
            final int duration = buffer.readVarInt();
            final ItemStackProvider onSeal = Helpers.decodeNullable(buffer, ItemStackProvider::fromNetwork);
            final ItemStackProvider onUnseal = Helpers.decodeNullable(buffer, ItemStackProvider::fromNetwork);
            return new SealedBarrelRecipe(recipeId, builder.inputItem(), builder.inputFluid(), builder.outputItem(), builder.outputFluid(), duration, onSeal, onUnseal);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SealedBarrelRecipe recipe)
        {
            Builder.toNetwork(recipe, buffer);
            buffer.writeVarInt(recipe.duration);
            Helpers.encodeNullable(recipe.onSeal, buffer, ItemStackProvider::toNetwork);
            Helpers.encodeNullable(recipe.onUnseal, buffer, ItemStackProvider::toNetwork);
        }
    }
}
