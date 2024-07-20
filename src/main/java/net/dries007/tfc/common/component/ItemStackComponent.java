/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * An immutable wrapper around an {@link ItemStack} which respects proper equality and hash code functionality, as is
 * required for any data attached to components.
 */
public record ItemStackComponent(ItemStack stack)
{
    public static final Codec<ItemStackComponent> CODEC = ItemStack.CODEC.xmap(ItemStackComponent::new, ItemStackComponent::stack);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackComponent> STREAM_CODEC = ItemStack.STREAM_CODEC.map(ItemStackComponent::new, ItemStackComponent::stack);
    public static final ItemStackComponent EMPTY = new ItemStackComponent(ItemStack.EMPTY);

    @Override
    public boolean equals(Object obj)
    {
        return obj == this || (obj instanceof ItemStackComponent that && ItemStack.matches(stack, that.stack));
    }

    @Override
    public int hashCode()
    {
        return ItemStack.hashItemAndComponents(stack);
    }
}
