/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.IRecipePredicate;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public record Deposit(
    Ingredient ingredient,
    ResourceKey<LootTable> lootTable,
    List<ResourceLocation> modelStages
) implements IRecipePredicate<ItemStack>
{
    public static final Codec<Deposit> CODEC = RecordCodecBuilder.create(i -> i.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(c -> c.lootTable),
        ResourceLocation.CODEC.listOf().optionalFieldOf("model_stages", List.of()).forGetter(c -> c.modelStages)
    ).apply(i, Deposit::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Deposit> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        ResourceKey.streamCodec(Registries.LOOT_TABLE), c -> c.lootTable,
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), c -> c.modelStages,
        Deposit::new
    );

    public static final DataManager<Deposit> MANAGER = new DataManager<>(Helpers.identifier("deposit"), CODEC, STREAM_CODEC);
    public static final IndirectHashCollection<Item, Deposit> CACHE = IndirectHashCollection.create(r -> RecipeHelpers.itemKeys(r.ingredient), MANAGER::getValues);

    /**
     * Returns the deposit represented by this stack. Note this does not access the stored deposit for pan items!
     * @return The deposit matching this stack, or {@code null} if none are present.
     */
    @Nullable
    public static Deposit get(@Nullable ItemStack stack)
    {
        return stack == null ? null : RecipeHelpers.getRecipe(CACHE, stack, stack.getItem());
    }

    @Override
    public boolean matches(ItemStack input)
    {
        return ingredient.test(input);
    }
}
