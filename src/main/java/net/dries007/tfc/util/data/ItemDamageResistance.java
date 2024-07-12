/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.PhysicalDamage;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public record ItemDamageResistance(
    Ingredient ingredient,
    PhysicalDamage damages
) {
    public static final Codec<ItemDamageResistance> CODEC = RecordCodecBuilder.create(i -> i.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        PhysicalDamage.CODEC.forGetter(c -> c.damages)
    ).apply(i, ItemDamageResistance::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemDamageResistance> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        PhysicalDamage.STREAM_CODEC, c -> c.damages,
        ItemDamageResistance::new
    );

    public static final DataManager<ItemDamageResistance> MANAGER = new DataManager<>(Helpers.identifier("item_damage_resistances"), "item_damage_resistances", CODEC, STREAM_CODEC);
    public static final IndirectHashCollection<Item, ItemDamageResistance> CACHE = IndirectHashCollection.create(c -> RecipeHelpers.itemKeys(c.ingredient), MANAGER::getValues);

    @Nullable
    public static ItemDamageResistance get(ItemStack item)
    {
        for (ItemDamageResistance resist : CACHE.getAll(item.getItem()))
        {
            if (resist.ingredient.test(item))
            {
                return resist;
            }
        }
        return null;
    }
}
