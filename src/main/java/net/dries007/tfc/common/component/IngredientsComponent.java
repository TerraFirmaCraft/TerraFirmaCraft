/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component;

import java.util.List;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.util.tooltip.Tooltips;

/**
 * A component for items that have a record of the ingredients that was used to create them, i.e. sandwiches.
 * @param ingredients The list of items, compressed, that was used to create them.
 */
public record IngredientsComponent(List<ItemStack> ingredients)
{
    public static final Codec<IngredientsComponent> CODEC = ItemStack.CODEC.listOf().xmap(IngredientsComponent::new, IngredientsComponent::ingredients);
    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientsComponent> STREAM_CODEC = ItemStack.STREAM_CODEC
        .apply(ByteBufCodecs.list())
        .map(IngredientsComponent::new, IngredientsComponent::ingredients);

    public static final IngredientsComponent EMPTY = of(List.of());

    public static IngredientsComponent of(List<ItemStack> ingredients)
    {
        return new IngredientsComponent(List.copyOf(ingredients));
    }

    public void addTooltipInfo(List<Component> text)
    {
        for (ItemStack ingredient : ingredients)
        {
            if (!ingredient.isEmpty())
            {
                text.add(Tooltips.countOfItem(ingredient).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean equals(Object obj)
    {
        return this == obj || (obj instanceof IngredientsComponent that && ItemStack.listMatches(ingredients, that.ingredients));
    }

    @Override
    @SuppressWarnings("deprecation")
    public int hashCode()
    {
        return ItemStack.hashStackList(ingredients);
    }
}
