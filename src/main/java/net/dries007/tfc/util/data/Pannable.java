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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.PannableComponent;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.recipes.IRecipePredicate;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public record Pannable(
    BlockIngredient ingredient,
    ResourceKey<LootTable> lootTable,
    List<ResourceLocation> modelStages
) implements IRecipePredicate<BlockState>
{
    public static final Codec<Pannable> CODEC = RecordCodecBuilder.create(i -> i.group(
        BlockIngredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(c -> c.lootTable),
        ResourceLocation.CODEC.listOf().fieldOf("model_stages").forGetter(c -> c.modelStages)
    ).apply(i, Pannable::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Pannable> STREAM_CODEC = StreamCodec.composite(
        BlockIngredient.STREAM_CODEC, c -> c.ingredient,
        ResourceKey.streamCodec(Registries.LOOT_TABLE), c -> c.lootTable,
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), c -> c.modelStages,
        Pannable::new
    );

    public static final DataManager<Pannable> MANAGER = new DataManager<>(Helpers.identifier("panning"), "panning", CODEC, STREAM_CODEC);
    public static final IndirectHashCollection<Block, Pannable> CACHE = IndirectHashCollection.create(s -> s.ingredient.blocks(), MANAGER::getValues);

    @Nullable
    public static Pannable get(BlockState state)
    {
        return RecipeHelpers.getRecipe(CACHE, state, state.getBlock());
    }

    /**
     * Retrieves the pannable for a block state stored on the item stack, using the {@link TFCComponents#PANNABLE} component
     */
    @Nullable
    public static Pannable get(ItemStack stack)
    {
        final @Nullable PannableComponent pannable = stack.get(TFCComponents.PANNABLE);
        return pannable != null ? get(pannable.state()) : null;
    }

    @Override
    public boolean matches(BlockState input)
    {
        return ingredient.test(input);
    }
}
