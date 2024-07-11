/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public record Pannable(
    BlockIngredient ingredient,
    ResourceLocation lootTable,
    List<ResourceLocation> modelStages
) {
    public static final Codec<Pannable> CODEC = RecordCodecBuilder.create(i -> i.group(
        BlockIngredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        ResourceLocation.CODEC.fieldOf("loot_table").forGetter(c -> c.lootTable),
        ResourceLocation.CODEC.listOf().fieldOf("model_stages").forGetter(c -> c.modelStages)
    ).apply(i, Pannable::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Pannable> STREAM_CODEC = StreamCodec.composite(
        BlockIngredient.STREAM_CODEC, c -> c.ingredient,
        ResourceLocation.STREAM_CODEC, c -> c.lootTable,
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), c -> c.modelStages,
        Pannable::new
    );

    public static final DataManager<Pannable> MANAGER = new DataManager<>(Helpers.identifier("panning"), "panning", CODEC, STREAM_CODEC);
    public static final IndirectHashCollection<Block, Pannable> CACHE = IndirectHashCollection.create(s -> s.ingredient.blocks(), MANAGER::getValues);

    @Nullable
    public static Pannable get(BlockState state)
    {
        for (Pannable pannable : CACHE.getAll(state.getBlock()))
        {
            if (pannable.ingredient.test(state))
            {
                return pannable;
            }
        }
        return null;
    }
}
