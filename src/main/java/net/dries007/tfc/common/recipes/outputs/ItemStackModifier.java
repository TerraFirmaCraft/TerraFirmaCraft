/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.recipes.RecipeHelpers;

public interface ItemStackModifier
{
    Codec<ItemStackModifier> CODEC = ItemStackModifiers.REGISTRY.byNameCodec()
        .dispatch(ItemStackModifier::type, ItemStackModifierType::codec);

    StreamCodec<RegistryFriendlyByteBuf, ItemStackModifier> STREAM_CODEC = ByteBufCodecs.registry(ItemStackModifiers.KEY)
        .dispatch(ItemStackModifier::type, ItemStackModifierType::streamCodec);

    /**
     * Apply the modifier to the stack and input pair. This only supports single input -> output relations, for modifiers that
     * wish to consider all possible inputs, they must use {@link RecipeHelpers#getCraftingInput()}
     *
     * @param stack   The current output stack, which is passed between modifiers. A no-op modifier would just return this stack.
     * @param input   The provided 'input' stack - do not modify this stack during the modifier.
     * @param context Represents a contextual ask for the caller. The default is {@link Context#DEFAULT}, with options as documented
     *                in the {@link Context} values
     * @return The stack, after modification. Modifying the {@code stack} parameter directly without copying is allowed.
     * @see RecipeHelpers#getCraftingInput()
     */
    ItemStack apply(ItemStack stack, ItemStack input, Context context);

    /**
     * @return {@code true} if the modifier in question introduces a strong dependency on the input item. That is, with an empty
     * input provided, the output of this recipe makes no sense and needs to be marked as special.
     */
    default boolean dependsOnInput()
    {
        return false;
    }

    ItemStackModifierType<?> type();

    /**
     * Indicates a contextual ask of an item stack modifier.
     * <ul>
     *     <li>{@link #DEFAULT} is the default context, where everything should operate as per normal</li>
     *     <li>{@link #NO_RANDOM_CHANCE} is a context used when querying for the purposes of inventory display, or other non-gameplay
     *     purposes, where the output needs to be deterministic and not represent the exact state of the recipe.</li>
     * </ul>
     */
    enum Context
    {
        DEFAULT, NO_RANDOM_CHANCE
    }
}
