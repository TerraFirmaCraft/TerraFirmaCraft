/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.capabilities.food.FoodDefinition;
import net.dries007.tfc.common.capabilities.heat.HeatDefinition;
import net.dries007.tfc.common.capabilities.size.ItemSizeDefinition;
import net.dries007.tfc.util.DataManager;
import net.dries007.tfc.util.Fuel;
import net.dries007.tfc.util.Metal;

public abstract class DataManagerSyncPacket<T>
{
    private Map<ResourceLocation, T> elements;
    private int generation;

    public DataManagerSyncPacket() {}

    public DataManagerSyncPacket<T> with(Map<ResourceLocation, T> elements, int generation)
    {
        this.elements = elements;
        this.generation = generation;
        return this;
    }

    void encode(DataManager<T> manager, FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(generation);
        buffer.writeVarInt(elements.size());
        for (Map.Entry<ResourceLocation, T> entry : elements.entrySet())
        {
            buffer.writeResourceLocation(entry.getKey());
            manager.encode(buffer, entry.getValue());
        }
    }

    void decode(DataManager<T> manager, FriendlyByteBuf buffer)
    {
        this.generation = buffer.readVarInt();
        this.elements = new HashMap<>();
        final int size = buffer.readVarInt();
        for (int i = 0; i < size; i++)
        {
            final ResourceLocation id = buffer.readResourceLocation();
            final T element = manager.decode(id, buffer);
            elements.put(id, element);
        }
    }

    void handle(DataManager<T> manager)
    {
        manager.onSync(elements, generation);
    }

    public static class TMetal extends DataManagerSyncPacket<Metal> {}

    public static class TFuel extends DataManagerSyncPacket<Fuel> {}

    public static class TFoodDefinition extends DataManagerSyncPacket<FoodDefinition> {}

    public static class THeatDefinition extends DataManagerSyncPacket<HeatDefinition> {}

    public static class TItemSizeDefinition extends DataManagerSyncPacket<ItemSizeDefinition> {}
}
