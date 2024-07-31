/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodTrait;

public record AddTraitModifier(Holder<FoodTrait> trait) implements ItemStackModifier
{
    public static final MapCodec<AddTraitModifier> CODEC = FoodTrait.CODEC.fieldOf("trait").xmap(AddTraitModifier::new, AddTraitModifier::trait);
    public static final StreamCodec<RegistryFriendlyByteBuf, AddTraitModifier> STREAM_CODEC = FoodTrait.STREAM_CODEC.map(AddTraitModifier::new, AddTraitModifier::trait);

    public static AddTraitModifier of(Holder<FoodTrait> trait)
    {
        return new AddTraitModifier(trait);
    }

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        return FoodCapability.applyTrait(stack, trait);
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ItemStackModifiers.ADD_TRAIT.get();
    }
}
