/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.util.JsonHelpers;

public record AddRemoveTraitModifier(boolean add, FoodTrait trait) implements ItemStackModifier
{
    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        return add ? FoodCapability.applyTrait(stack, trait) : FoodCapability.removeTrait(stack, trait);
    }

    @Override
    public Serializer serializer()
    {
        return add ? Serializer.ADD : Serializer.REMOVE;
    }

    public record Serializer(boolean add) implements ItemStackModifier.Serializer<AddRemoveTraitModifier>
    {
        static final Serializer ADD = new Serializer(true);
        static final Serializer REMOVE = new Serializer(false);

        @Override
        public AddRemoveTraitModifier fromJson(JsonObject json)
        {
            final FoodTrait trait = FoodTrait.getTraitOrThrow(new ResourceLocation(JsonHelpers.getAsString(json, "trait")));
            return new AddRemoveTraitModifier(add, trait);
        }

        @Override
        public AddRemoveTraitModifier fromNetwork(FriendlyByteBuf buffer)
        {
            final FoodTrait trait = FoodTrait.getTraitOrThrow(new ResourceLocation(buffer.readUtf()));
            return new AddRemoveTraitModifier(add, trait);
        }

        @Override
        public void toNetwork(AddRemoveTraitModifier modifier, FriendlyByteBuf buffer)
        {
            buffer.writeResourceLocation(FoodTrait.getId(modifier.trait));
        }
    }
}
