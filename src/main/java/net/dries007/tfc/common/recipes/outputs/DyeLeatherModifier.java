/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import java.util.Collections;

import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.*;

import net.dries007.tfc.util.JsonHelpers;

public record DyeLeatherModifier(DyeColor color) implements ItemStackModifier
{
    public static final MapCodec<DyeLeatherModifier> CODEC = DyeColor.CODEC.fieldOf("color").xmap(DyeLeatherModifier::new, DyeLeatherModifier::color);
    public static final StreamCodec<RegistryFriendlyByteBuf, DyeLeatherModifier> STREAM_CODEC = StreamCodec.composite(
        DyeColor.STREAM_CODEC, c -> c.color,
        DyeLeatherModifier::new
    );

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        return DyeableLeatherItem.dyeArmor(stack, Collections.singletonList(DyeItem.byColor(color)));
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ItemStackModifiers.DYE_LEATHER.get();
    }
}
