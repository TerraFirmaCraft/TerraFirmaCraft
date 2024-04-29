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
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.recipes.inventory.BarrelInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public class SealedBarrelRecipe extends BarrelRecipe
{
    private final int duration;

    @Nullable private final ItemStackProvider onSeal;
    @Nullable private final ItemStackProvider onUnseal;

    public SealedBarrelRecipe(ResourceLocation id, Builder builder, int duration, @Nullable ItemStackProvider onSeal, @Nullable ItemStackProvider onUnseal)
    {
        super(id, builder);

        this.duration = duration;
        this.onSeal = onSeal;
        this.onUnseal = onUnseal;
    }

    @Override
    public boolean matches(BarrelInventory container, @Nullable Level level)
    {
        // Sealed barrel recipes match as long as both ingredients meet the minimum requirements (this is the call to super)
        // However, if the barrel recipe is infinite, it should only match as long as there is more fluid than items
        // We do this because infinite recipes must by definition, be onSeal + onUnseal, which operate independent of stack size
        // Note that this is the *opposite* of the ratio requirements for instant barrel recipes, which is kind of poetic.
        return super.matches(container, level) && (!isInfinite() || inputFluid.amount() == 0 || inputItem.count() == 0 || container.getFluidInTank(0).getAmount() / this.inputFluid.amount() >= container.getStackInSlot(BarrelBlockEntity.SLOT_ITEM).getCount() / this.inputItem.count());
    }

    public int getDuration()
    {
        return duration;
    }

    public boolean isInfinite()
    {
        return duration <= 0;
    }

    @Nullable
    public ItemStackProvider getOnSeal()
    {
        return onSeal;
    }

    @Nullable
    public ItemStackProvider getOnUnseal()
    {
        return onUnseal;
    }

    public void onSealed(BarrelInventory inventory)
    {
        if (onSeal != null)
        {
            inventory.whileMutable(() -> {
                final ItemStack stack = Helpers.removeStack(inventory, BarrelBlockEntity.SLOT_ITEM);
                inventory.insertItem(BarrelBlockEntity.SLOT_ITEM, onSeal.getStack(stack), false);
            });
        }
    }

    public void onUnsealed(BarrelInventory inventory)
    {
        if (onUnseal != null)
        {
            inventory.whileMutable(() -> {
                final ItemStack stack = Helpers.removeStack(inventory, BarrelBlockEntity.SLOT_ITEM);
                inventory.insertItem(BarrelBlockEntity.SLOT_ITEM, onUnseal.getStack(stack), false);
            });
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
            return new SealedBarrelRecipe(recipeId, builder, duration, onSeal, onUnseal);
        }

        @Nullable
        @Override
        public SealedBarrelRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final Builder builder = Builder.fromNetwork(buffer);
            final int duration = buffer.readVarInt();
            final ItemStackProvider onSeal = Helpers.decodeNullable(buffer, ItemStackProvider::fromNetwork);
            final ItemStackProvider onUnseal = Helpers.decodeNullable(buffer, ItemStackProvider::fromNetwork);
            return new SealedBarrelRecipe(recipeId, builder, duration, onSeal, onUnseal);
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
