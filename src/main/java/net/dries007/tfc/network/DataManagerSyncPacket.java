/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import net.dries007.tfc.common.capabilities.food.FoodDefinition;
import net.dries007.tfc.common.capabilities.heat.HeatDefinition;
import net.dries007.tfc.common.capabilities.size.ItemSizeDefinition;
import net.dries007.tfc.util.Metal;

public abstract class DataManagerSyncPacket<T>
{
    Map<ResourceLocation, T> elements;

    public DataManagerSyncPacket() {}

    public void setElements(Map<ResourceLocation, T> elements)
    {
        this.elements = elements;
    }

    void encode(BiConsumer<FriendlyByteBuf, T> encoder, FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(elements.size());
        for (Map.Entry<ResourceLocation, T> entry : elements.entrySet())
        {
            buffer.writeResourceLocation(entry.getKey());
            encoder.accept(buffer, entry.getValue());
        }
    }

    void decode(BiFunction<ResourceLocation, FriendlyByteBuf, T> decoder, FriendlyByteBuf buffer)
    {
        this.elements = new HashMap<>();
        final int size = buffer.readVarInt();
        for (int i = 0; i < size; i++)
        {
            final ResourceLocation id = buffer.readResourceLocation();
            final T element = decoder.apply(id, buffer);
            elements.put(id, element);
        }
    }

    void handle(Consumer<Map<ResourceLocation, T>> manager, NetworkEvent.Context context)
    {
        context.enqueueWork(() -> manager.accept(elements));
    }

    public static class TMetal extends DataManagerSyncPacket<Metal> {}

    public static class TFoodDefinition extends DataManagerSyncPacket<FoodDefinition> {}

    public static class THeatDefinition extends DataManagerSyncPacket<HeatDefinition> {}

    public static class TItemSizeDefinition extends DataManagerSyncPacket<ItemSizeDefinition> {}
}
