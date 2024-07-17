/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public record Sluiceable(
    Ingredient ingredient,
    ResourceKey<LootTable> lootTable
) {
    public static final Codec<Sluiceable> CODEC = RecordCodecBuilder.create(i -> i.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(c -> c.lootTable)
    ).apply(i, Sluiceable::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Sluiceable> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        ResourceKey.streamCodec(Registries.LOOT_TABLE), c -> c.lootTable,
        Sluiceable::new
    );

    public static final DataManager<Sluiceable> MANAGER = new DataManager<>(Helpers.identifier("sluicing"), CODEC, STREAM_CODEC);
    public static final IndirectHashCollection<Item, Sluiceable> CACHE = IndirectHashCollection.create(r -> RecipeHelpers.itemKeys(r.ingredient), MANAGER::getValues);

    @Nullable
    public static Sluiceable get(ItemStack stack)
    {
        for (Sluiceable sluiceable : CACHE.getAll(stack.getItem()))
        {
            if (sluiceable.ingredient.test(stack))
            {
                return sluiceable;
            }
        }
        return null;
    }
}
