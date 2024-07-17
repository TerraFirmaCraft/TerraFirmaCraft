/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import java.util.List;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public record DyeLeatherModifier(DyeColor color) implements ItemStackModifier
{
    public static final MapCodec<DyeLeatherModifier> CODEC = DyeColor.CODEC.fieldOf("color").xmap(DyeLeatherModifier::new, DyeLeatherModifier::color);
    public static final StreamCodec<RegistryFriendlyByteBuf, DyeLeatherModifier> STREAM_CODEC = StreamCodec.composite(
        DyeColor.STREAM_CODEC, c -> c.color,
        DyeLeatherModifier::new
    );

    public static DyeLeatherModifier of(DyeColor color)
    {
        return new DyeLeatherModifier(color);
    }

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        return DyedItemColor.applyDyes(stack, List.of(DyeItem.byColor(color)));
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ItemStackModifiers.DYE_LEATHER.get();
    }
}
