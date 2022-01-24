package net.dries007.tfc.common.recipes.outputs;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public interface ItemStackModifier
{
    ItemStack apply(ItemStack stack, ItemStack input);

    Serializer<?> serializer();

    @SuppressWarnings("unchecked")
    default void toNetwork(FriendlyByteBuf buffer)
    {
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
