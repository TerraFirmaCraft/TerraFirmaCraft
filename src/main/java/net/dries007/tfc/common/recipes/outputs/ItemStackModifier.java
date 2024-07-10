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
     * @param stack The current output stack, which is passed between modifiers. A no-op modifier would just return this stack.
     * @param input The provided 'input' stack - do not modify this stack during the modifier.
     * @return The stack, after modification. Modifying the {@code stack} parameter directly without copying is allowed.
     *
     * @see RecipeHelpers#getCraftingInput()
     */
    ItemStack apply(ItemStack stack, ItemStack input);

    /**
     * @return {@code true} if the modifier in question introduces a strong dependency on the input item. That is, with an empty
     * input provided, the output of this recipe makes no sense and needs to be marked as special.
     */
    default boolean dependsOnInput()
    {
        return false;
    }

    ItemStackModifierType<?> type();
}
