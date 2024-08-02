/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.block;

import java.util.Collections;
import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.blockentities.CrucibleBlockEntity;
import net.dries007.tfc.common.component.item.ItemComponent;
import net.dries007.tfc.util.FluidAlloy;
import net.dries007.tfc.util.Helpers;

public record CrucibleComponent(
    List<ItemStack> itemContent,
    FluidAlloy fluidContent
)
{
    public static final Codec<CrucibleComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
        ItemStack.OPTIONAL_CODEC.listOf().fieldOf("items").forGetter(c -> c.itemContent),
        FluidAlloy.CODEC.fieldOf("fluid").forGetter(c -> c.fluidContent)
    ).apply(i, CrucibleComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrucibleComponent> STREAM_CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()), c -> c.itemContent,
        FluidAlloy.STREAM_CODEC, c -> c.fluidContent,
        CrucibleComponent::new
    );

    public static final CrucibleComponent EMPTY = new CrucibleComponent(Collections.nCopies(CrucibleBlockEntity.SLOTS, ItemStack.EMPTY), FluidAlloy.empty());

    public boolean isEmpty()
    {
        return Helpers.isEmpty(itemContent) && fluidContent.isEmpty();
    }

    @Override
    public boolean equals(Object obj)
    {
        return this == obj || (obj instanceof CrucibleComponent that
            && ItemComponent.equals(itemContent, that.itemContent)
            && fluidContent.equals(that.fluidContent));
    }

    @Override
    public int hashCode()
    {
        return ItemComponent.hashCode(itemContent) * 31 + fluidContent.hashCode();
    }
}
