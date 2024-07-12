/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public record Fuel(
    Ingredient ingredient,
    int duration,
    float temperature,
    float purity
) {
    public static final Codec<Fuel> CODEC = RecordCodecBuilder.create(i -> i.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        Codec.INT.fieldOf("duration").forGetter(c -> c.duration),
        Codec.FLOAT.fieldOf("temperature").forGetter(c -> c.temperature),
        Codec.FLOAT.optionalFieldOf("purity", 1f).forGetter(c -> c.purity)
    ).apply(i, Fuel::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Fuel> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        ByteBufCodecs.VAR_INT, c -> c.duration,
        ByteBufCodecs.FLOAT, c -> c.temperature,
        ByteBufCodecs.FLOAT, c -> c.purity,
        Fuel::new
    );

    public static final DataManager<Fuel> MANAGER = new DataManager<>(Helpers.identifier("fuels"), "fuel", CODEC, STREAM_CODEC);
    public static final IndirectHashCollection<Item, Fuel> CACHE = IndirectHashCollection.create(r -> RecipeHelpers.itemKeys(r.ingredient), MANAGER::getValues);

    @Nullable
    public static Fuel get(ItemStack stack)
    {
        for (Fuel def : CACHE.getAll(stack.getItem()))
        {
            if (def.ingredient.test(stack))
            {
                return def;
            }
        }
        return null;
    }
}
