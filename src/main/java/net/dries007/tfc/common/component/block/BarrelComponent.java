/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.block;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.component.item.ItemComponent;
import net.dries007.tfc.common.component.item.ItemContainer;
import net.dries007.tfc.util.Helpers;

public record BarrelComponent(
    List<ItemStack> itemContent,
    FluidStack fluidContent,
    long sealedTick,
    long recipeTick
)
{
    public static final Codec<BarrelComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
        ItemStack.OPTIONAL_CODEC.listOf().fieldOf("items").forGetter(c -> c.itemContent),
        FluidStack.OPTIONAL_CODEC.fieldOf("fluid").forGetter(c -> c.fluidContent),
        Codec.LONG.optionalFieldOf("sealedTick", -1L).forGetter(c -> c.sealedTick),
        Codec.LONG.optionalFieldOf("recipeTick", -1L).forGetter(c -> c.recipeTick)
    ).apply(i, BarrelComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BarrelComponent> STREAM_CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()), c -> c.itemContent,
        FluidStack.OPTIONAL_STREAM_CODEC, c -> c.fluidContent,
        ByteBufCodecs.VAR_LONG, c -> c.sealedTick,
        ByteBufCodecs.VAR_LONG, c -> c.recipeTick,
        BarrelComponent::new
    );

    public static final BarrelComponent EMPTY = new BarrelComponent(Collections.nCopies(BarrelBlockEntity.SLOTS, ItemStack.EMPTY), FluidStack.EMPTY, -1L, -1L);

    public boolean isEmpty()
    {
        return fluidContent.isEmpty() && Helpers.isEmpty(itemContent);
    }

    public boolean hasActiveRecipe()
    {
        return recipeTick != -1;
    }

    BarrelComponent with(FluidStack content)
    {
        return new BarrelComponent(itemContent, content, sealedTick, recipeTick);
    }

    @Override
    public boolean equals(Object obj)
    {
        return this == obj || (obj instanceof BarrelComponent that
            && ItemComponent.equals(itemContent, that.itemContent)
            && FluidStack.matches(fluidContent, that.fluidContent)
            && sealedTick == that.sealedTick
            && recipeTick == that.recipeTick);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(ItemComponent.hashCode(itemContent), FluidStack.hashFluidAndComponents(fluidContent), sealedTick, recipeTick);
    }
}
