/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.size;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record ItemSizeDefinition(
    Ingredient ingredient,
    Size size,
    Weight weight
) implements IItemSize
{
    public static final Codec<ItemSizeDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        Size.CODEC.fieldOf("size").forGetter(c -> c.size),
        Weight.CODEC.fieldOf("weight").forGetter(c -> c.weight)
    ).apply(i, ItemSizeDefinition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemSizeDefinition> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        Size.STREAM_CODEC, c -> c.size,
        Weight.STREAM_CODEC, c -> c.weight,
        ItemSizeDefinition::new
    );

    public ItemSizeDefinition(Size size, Weight weight)
    {
        this(Ingredient.EMPTY, size, weight);
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return size;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return weight;
    }
}
