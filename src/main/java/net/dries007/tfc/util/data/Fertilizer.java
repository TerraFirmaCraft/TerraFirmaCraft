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

import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public record Fertilizer(
    Ingredient ingredient,
    float nitrogen,
    float phosphorus,
    float potassium
) {
    public static final Codec<Fertilizer> CODEC = RecordCodecBuilder.create(i -> i.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        Codec.FLOAT.optionalFieldOf("nitrogen", 0f).forGetter(c -> c.nitrogen),
        Codec.FLOAT.optionalFieldOf("phosphorus", 0f).forGetter(c -> c.phosphorus),
        Codec.FLOAT.optionalFieldOf("potassium", 0f).forGetter(c -> c.potassium)
    ).apply(i, Fertilizer::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Fertilizer> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        ByteBufCodecs.FLOAT, c -> c.nitrogen,
        ByteBufCodecs.FLOAT, c -> c.phosphorus,
        ByteBufCodecs.FLOAT, c -> c.potassium,
        Fertilizer::new
    );

    public static final DataManager<Fertilizer> MANAGER = new DataManager<>(Helpers.identifier("fertilizers"), CODEC, STREAM_CODEC);
    public static final IndirectHashCollection<Item, Fertilizer> CACHE = IndirectHashCollection.create(c -> RecipeHelpers.itemKeys(c.ingredient), MANAGER::getValues);

    @Nullable
    public static Fertilizer get(ItemStack stack)
    {
        for (Fertilizer def : CACHE.getAll(stack.getItem()))
        {
            if (def.ingredient.test(stack))
            {
                return def;
            }
        }
        return null;
    }

    public float getNutrient(FarmlandBlockEntity.NutrientType type)
    {
        return switch (type) {
            case NITROGEN -> nitrogen;
            case PHOSPHOROUS -> phosphorus;
            case POTASSIUM -> potassium;
        };
    }
}
