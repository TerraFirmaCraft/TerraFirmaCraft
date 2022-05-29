/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.recipes.RecipeHelpers;

public interface ItemStackModifier
{
    /**
     * Apply the modifier to the stack and input pair.
     *
     * @param stack The current output stack, which is passed between modifiers. A no-op modifier would just return this stack.
     * @param input The provided 'input' stack - do not modify this stack during the modifier.
     * @return The stack, after modification. Modifying the {@code stack} parameter directly without copying is allowed.
     * @see RecipeHelpers#getCraftingContainer()
     */
    ItemStack apply(ItemStack stack, ItemStack input);

    /**
     * @return {@code true} if the modifier in question introduces a strong dependency on the input item. That is, with an empty input provided, the output of this recipe makes no sense and needs to be marked as special.
     */
    default boolean dependsOnInput()
    {
        return false;
    }

    Serializer<?> serializer();

    @SuppressWarnings("unchecked")
    default void toNetwork(FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(ItemStackModifiers.getId(serializer()));
        ((Serializer<ItemStackModifier>) serializer()).toNetwork(this, buffer);
    }

    interface Serializer<T extends ItemStackModifier>
    {
        T fromJson(JsonObject json);

        T fromNetwork(FriendlyByteBuf buffer);

        void toNetwork(T modifier, FriendlyByteBuf buffer);
    }

    interface SingleInstance<T extends ItemStackModifier> extends ItemStackModifier, ItemStackModifier.Serializer<T>
    {
        T instance();

        @Override
        default Serializer<?> serializer()
        {
            return this;
        }

        @Override
        default T fromJson(JsonObject json)
        {
            return instance();
        }

        @Override
        default T fromNetwork(FriendlyByteBuf buffer)
        {
            return instance();
        }

        @Override
        default void toNetwork(T modifier, FriendlyByteBuf buffer) {}
    }
}
