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

public record BaitComponent(ItemStack stack)
{
    public static final Codec<BaitComponent> CODEC = ItemStack.CODEC.xmap(BaitComponent::new, BaitComponent::stack);
    public static final StreamCodec<RegistryFriendlyByteBuf, BaitComponent> STREAM_CODEC = ItemStack.STREAM_CODEC.map(BaitComponent::new, BaitComponent::stack);
    public static final BaitComponent EMPTY = new BaitComponent(ItemStack.EMPTY);

    /**
     * @return The bait currently attached to this {@code fishingRod}, or {@link ItemStack#EMPTY} if no bait exists
     */
    public static ItemStack getBait(ItemStack fishingRod)
    {
        return fishingRod.getOrDefault(TFCComponents.BAIT, EMPTY).stack();
    }

    /**
     * Attaches the {@code bait} to the {@code fishingRod} item.
     */
    public static void setBait(ItemStack fishingRod, ItemStack bait)
    {
        fishingRod.set(TFCComponents.BAIT, new BaitComponent(bait.copyWithCount(1)));
    }
}
