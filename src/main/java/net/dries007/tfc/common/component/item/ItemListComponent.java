/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.item;

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
 * A component representing a fixed, immutable list of item stacks. This could be used by {@link ItemContainer} and the insert/extract
 * methods in {@link ItemComponent}, although these don't work.
 */
public record ItemListComponent(List<ItemStack> ingredients)
{
    public static final Codec<ItemListComponent> CODEC = ItemStack.CODEC.listOf().xmap(ItemListComponent::new, ItemListComponent::ingredients);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemListComponent> STREAM_CODEC = ItemStack.STREAM_CODEC
        .apply(ByteBufCodecs.list())
        .map(ItemListComponent::new, ItemListComponent::ingredients);

    public static final ItemListComponent EMPTY = of(List.of());

    public static ItemListComponent of(List<ItemStack> ingredients)
    {
        return new ItemListComponent(List.copyOf(ingredients));
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
    public boolean equals(Object obj)
    {
        return this == obj || (obj instanceof ItemListComponent that && ItemComponent.equals(ingredients, that.ingredients));
    }

    @Override
    public int hashCode()
    {
        return ItemComponent.hashCode(ingredients);
    }
}
