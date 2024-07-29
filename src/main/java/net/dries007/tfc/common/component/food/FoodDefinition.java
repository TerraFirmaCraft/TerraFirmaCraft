/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.food;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.recipes.IRecipePredicate;

public record FoodDefinition(
    Ingredient ingredient,
    FoodData food,
    boolean edible
) implements IRecipePredicate<ItemStack>
{
    public static final Codec<FoodDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        FoodData.MAP_CODEC.forGetter(c -> c.food),
        Codec.BOOL.optionalFieldOf("edible", true).forGetter(c -> c.edible)
    ).apply(i, FoodDefinition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FoodDefinition> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        FoodData.STREAM_CODEC, c -> c.food,
        ByteBufCodecs.BOOL, c -> c.edible,
        FoodDefinition::new
    );

    public static final FoodDefinition DEFAULT = new FoodDefinition(Ingredient.EMPTY, FoodData.EMPTY, true);

    @Override
    public boolean matches(ItemStack input)
    {
        return ingredient.test(input);
    }
}
